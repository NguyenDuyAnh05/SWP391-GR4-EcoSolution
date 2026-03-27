package org.swp391_group4_backend.ecosolution.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp391_group4_backend.ecosolution.constant.SubscriptionStatus;
import org.swp391_group4_backend.ecosolution.constant.UserRole;
import org.swp391_group4_backend.ecosolution.dto.request.ActivationRequest;
import org.swp391_group4_backend.ecosolution.dto.request.LoginRequest;
import org.swp391_group4_backend.ecosolution.dto.request.RegisterRequest;
import org.swp391_group4_backend.ecosolution.dto.response.ActivationResponse;
import org.swp391_group4_backend.ecosolution.dto.response.SubscriptionResponse;
import org.swp391_group4_backend.ecosolution.dto.response.UserResponse;
import org.swp391_group4_backend.ecosolution.entity.CitizenSubscription;
import org.swp391_group4_backend.ecosolution.entity.SubscriptionTier;
import org.swp391_group4_backend.ecosolution.entity.User;
import org.swp391_group4_backend.ecosolution.entity.Ward;
import org.swp391_group4_backend.ecosolution.repository.CitizenSubscriptionRepository;
import org.swp391_group4_backend.ecosolution.repository.UserRepository;
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



    @Override
    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Username not found"));

        if (!user.getPassword().equals(request.password())) {
            throw new RuntimeException("Password is incorrect");
        }

        // Gộp firstName và lastName khi trả về
        return new UserResponse(user.getId(), user.getUsername(), fullName(user), user.getRole());
    }

    @Override
    public UserResponse register(RegisterRequest request) {
        userRepository.findByUsername(request.username()).ifPresent(u -> {
            throw new RuntimeException("Username already exists");
        });

        User user = modelMapper.map(request, User.class);
        user.setRole(UserRole.CITIZEN);

        userRepository.save(user);

        return new UserResponse(user.getId(), user.getUsername(), fullName(user), user.getRole());
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

        //3. Create a subscription with status PENDING
        CitizenSubscription sub = new CitizenSubscription();
        sub.setUser(user);
        sub.setTier(tier);
        sub.setStatus(SubscriptionStatus.PENDING_PAYMENT);
        subscriptionRepository.save(sub);

        //4. Get VNPAY link
        String paymentUrl = VNPAYPaymentService.createPaymentUrl(
                tier.getMonthlyFee(),
                "PaymentForSubID" + sub.getId(),
                String.valueOf(sub.getId()),
                httpServletRequest
        );


        // 2. Chuyển phần mapping lằng nhằng xuống hàm phụ
        return buildActivationResponse(user, ward, tier, sub, paymentUrl);    }

    private ActivationResponse buildActivationResponse(User user, Ward ward, SubscriptionTier tier, CitizenSubscription sub, String paymentUrl) {
        String fullName = fullName(user);

        return ActivationResponse.builder()
                .userId(user.getId())
                .fullName(fullName)
                .address(user.getAddress())
                .wardName(ward.getWardName())
                .tierType(tier.getTierType())
                .paymentUrl(paymentUrl) // Trả về URL thanh toán cho FE
                .message("Initiated VNPay payment. Please complete payment to activate your service.")
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

    private String fullName(User user) {
        return user.getLastName() + " " + user.getFirstName();
    }
}

