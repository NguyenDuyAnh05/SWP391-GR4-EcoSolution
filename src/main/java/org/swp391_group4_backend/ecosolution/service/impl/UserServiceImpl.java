package org.swp391_group4_backend.ecosolution.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp391_group4_backend.ecosolution.constant.SubscriptionStatus;
import org.swp391_group4_backend.ecosolution.constant.UserRole;
import org.swp391_group4_backend.ecosolution.dto.request.ActivationRequest;
import org.swp391_group4_backend.ecosolution.dto.request.CreateUserRequest;
import org.swp391_group4_backend.ecosolution.dto.request.LoginRequest;
import org.swp391_group4_backend.ecosolution.dto.request.RegisterRequest;
import org.swp391_group4_backend.ecosolution.dto.response.ActivationResponse;
import org.swp391_group4_backend.ecosolution.dto.response.SubscriptionResponse;
import org.swp391_group4_backend.ecosolution.dto.response.UserResponse;
import org.swp391_group4_backend.ecosolution.dto.response.TransactionResponse;
import org.swp391_group4_backend.ecosolution.entity.CitizenSubscription;
import org.swp391_group4_backend.ecosolution.entity.SubscriptionTier;
import org.swp391_group4_backend.ecosolution.entity.User;
import org.swp391_group4_backend.ecosolution.entity.Ward;
import org.swp391_group4_backend.ecosolution.repository.CitizenSubscriptionRepository;
import org.swp391_group4_backend.ecosolution.repository.UserRepository;
import org.swp391_group4_backend.ecosolution.repository.PaymentTransactionRepository;
import org.swp391_group4_backend.ecosolution.service.PaymentService;
import org.swp391_group4_backend.ecosolution.service.SubscriptionTierService;
import org.swp391_group4_backend.ecosolution.service.UserService;
import org.swp391_group4_backend.ecosolution.service.WardService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CitizenSubscriptionRepository subscriptionRepository;
    private final WardService wardService;
    private final SubscriptionTierService tierService;
    private final PaymentService VNPAYPaymentService;
    private final ModelMapper modelMapper;
    private final PaymentTransactionRepository paymentTransactionRepository;



    @Override
    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Username not found"));

        if (!user.getPassword().equals(request.password())) {
            throw new RuntimeException("Password is incorrect");
        }

        // Gộp firstName và lastName khi trả về
        return new UserResponse(user.getId(), user.getUsername(), fullName(user), user.getRole(), user.getRewardPoints() != null ? user.getRewardPoints() : 0);
    }

    @Override
    public UserResponse register(RegisterRequest request) {
        userRepository.findByUsername(request.username()).ifPresent(u -> {
            throw new RuntimeException("Username already exists");
        });

        User user = modelMapper.map(request, User.class);
        user.setRole(UserRole.CITIZEN);

        userRepository.save(user);

        return new UserResponse(user.getId(), user.getUsername(), fullName(user), user.getRole(), 0);
    }

    @Override
    public UserResponse createUserByAdmin(CreateUserRequest request) {
        userRepository.findByUsername(request.username()).ifPresent(u -> {
            throw new RuntimeException("Username already exists");
        });

        User user = modelMapper.map(request, User.class);
        user.setRole(request.role()); // Admin assigns role directly

        userRepository.save(user);

        return new UserResponse(user.getId(), user.getUsername(), fullName(user), user.getRole(), 0);
    }

    @Override
    @Transactional
    public ActivationResponse activateService(ActivationRequest request, HttpServletRequest httpServletRequest) {
        boolean hasActive = subscriptionRepository.existsByUserIdAndStatus(request.userId(), SubscriptionStatus.ACTIVE);
        if (hasActive) {
            throw new RuntimeException("You are ready have an active subscription !");
        }

        boolean hasPending = subscriptionRepository.existsByUserIdAndStatus(request.userId(), SubscriptionStatus.PENDING_PAYMENT);
        if (hasPending) {
            throw new RuntimeException("You need to pay for pending subscription first !");
        }

        //1. Find User, Ward, Tier
        User user = userRepository.findById(request.userId()).orElseThrow(() -> new RuntimeException("User not found"));
        Ward ward = wardService.getWardById(request.wardId());
        SubscriptionTier tier = tierService.getTierById(request.tierId());

        //2. Update Citizen address
        user.setWard(ward);
        user.setAddress(request.address());
        user.setLatitude(request.latitude());
        user.setLongitude(request.longitude());
        userRepository.save(user);

        //3. Create a subscription
        CitizenSubscription sub = new CitizenSubscription();
        sub.setUser(user);
        sub.setTier(tier);
        
        // 4. BỘ NÃO TÍNH TOÁN (BUSINESS LOGIC)
        long POINT_CONVERSION_RATE = 1000L; // 1 EcoPoint = 1000 VND
        long originalFee = tier.getMonthlyFee().longValue();
        int availablePoints = user.getRewardPoints() != null ? user.getRewardPoints() : 0;
        
        long discount = availablePoints * POINT_CONVERSION_RATE;
        long finalAmount = originalFee - discount;
        
        if (finalAmount < 0) {
            finalAmount = 0;
        }

        // Tính số điểm thực sự đã dùng
        int pointsUsed = 0;
        if (availablePoints > 0) {
            if (discount >= originalFee) {
                // Chỉ dùng đủ số điểm để trả hóa đơn
                pointsUsed = (int) Math.ceil((double) originalFee / POINT_CONVERSION_RATE);
            } else {
                pointsUsed = availablePoints;
            }
            user.setRewardPoints(availablePoints - pointsUsed);
            userRepository.save(user); // Lưu điểm đã trừ
            // TOD0: Lưu lịch sử trừ điểm vào bảng PointTransaction (Thực hiện ở feature Points sau)
        }

        // 5. Kiểm tra trường hợp đặc biệt: Thanh toán 100% bằng điểm
        if (finalAmount == 0) {
            sub.setStatus(SubscriptionStatus.ACTIVE);
            sub.setStartDate(java.time.LocalDate.now());
            sub.setEndDate(java.time.LocalDate.now().plusDays(tier.getFrequencyDays()));
            subscriptionRepository.save(sub);
            
            return buildActivationResponse(user, ward, tier, sub, null, "Kích hoạt gói thành công bằng Eco-points! Không cần thanh toán qua VNPay.");
        }

        // 6. Trường hợp cần thanh toán phần còn lại qua VNPay
        sub.setStatus(SubscriptionStatus.PENDING_PAYMENT);
        subscriptionRepository.save(sub);

        String paymentUrl = VNPAYPaymentService.createPaymentUrl(
                finalAmount,
                "PaymentForSubID" + sub.getId(),
                String.valueOf(sub.getId()),
                httpServletRequest
        );

        return buildActivationResponse(user, ward, tier, sub, paymentUrl, "Initiated VNPay payment. Please complete payment to activate your service.");    
    }

    private ActivationResponse buildActivationResponse(User user, Ward ward, SubscriptionTier tier, CitizenSubscription sub, String paymentUrl, String message) {
        String fullName = fullName(user);

        return ActivationResponse.builder()
                .userId(user.getId())
                .fullName(fullName)
                .address(user.getAddress())
                .wardName(ward.getWardName())
                .tierType(tier.getTierType())
                .paymentUrl(paymentUrl) // Trả về URL thanh toán cho FE
                .message(message)
                .build();
    }

    @Override
    public SubscriptionResponse getSubscription(Long userId) {
        return subscriptionRepository.findByUserId(userId).map(sub -> SubscriptionResponse.builder()
                .id(sub.getId())
                .userId(sub.getUser().getId())
                .tierType(sub.getTier().getTierType().name())
                .monthlyFee(sub.getTier().getMonthlyFee())
                .frequencyDays(sub.getTier().getFrequencyDays())
                .status(sub.getStatus())
                .startDate(sub.getStartDate())
                .endDate(sub.getEndDate())
                .build()
        ).orElseThrow(() -> new RuntimeException("No subscription found user"));
    }

    @Override
    @Transactional
    public void cancelPendingSubscription(Long userId) {
        CitizenSubscription pending = subscriptionRepository.findByUserId(userId)
                .filter(s -> s.getStatus() == SubscriptionStatus.PENDING_PAYMENT)
                .orElseThrow(() -> new RuntimeException("No pending subscription to cancel"));
        subscriptionRepository.delete(pending);
    }

    @Override
    public java.util.List<TransactionResponse> getUserTransactions(Long userId) {
        return paymentTransactionRepository.findAll().stream()
                .filter(tx -> tx.getSubscription() != null && tx.getSubscription().getUser().getId().equals(userId))
                .map(tx -> TransactionResponse.builder()
                        .id(tx.getId())
                        .subscriptionId(tx.getSubscription().getId())
                        .amount(tx.getAmount())
                        .bankCode(tx.getBankCode())
                        .vnpTransactionNo(tx.getVnpTransactionNo())
                        .responseCode(tx.getResponseCode())
                        .orderInfo(tx.getOrderInfo())
                        .payDate(tx.getPayDate())
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }

    private String fullName(User user) {
        return user.getLastName() + " " + user.getFirstName();
    }
}

