package org.swp391_group4_backend.ecosolution.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.swp391_group4_backend.ecosolution.constant.ReportStatus;
import org.swp391_group4_backend.ecosolution.constant.UserRole;
import org.swp391_group4_backend.ecosolution.dto.response.*;
import org.swp391_group4_backend.ecosolution.entity.PickupTask;
import org.swp391_group4_backend.ecosolution.entity.User;
import org.swp391_group4_backend.ecosolution.repository.PaymentTransactionRepository;
import org.swp391_group4_backend.ecosolution.repository.PickupTaskRepository;
import org.swp391_group4_backend.ecosolution.repository.UserRepository;
import org.swp391_group4_backend.ecosolution.service.AdminService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final PickupTaskRepository pickupTaskRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Override
    public StatsSummaryResponse getSummaryStats() {
        return null; // Keep existing signature structure
    }

    @Override
    public List<CollectorStatResponse> getTopCollectors() {
        return null; // Keep existing signature structure
    }

    @Override
    public AdminStatsResponse getStats() {
        long citizens = userRepository.findByRole(UserRole.CITIZEN).orElse(List.of()).size();
        long collectors = userRepository.findByRole(UserRole.COLLECTOR).orElse(List.of()).size();

        List<PickupTask> allTasks = pickupTaskRepository.findAll();
        long pending = allTasks.stream().filter(t -> t.getStatus() == ReportStatus.PENDING).count();
        long completed = allTasks.stream().filter(t -> t.getStatus() == ReportStatus.COMPLETED).count();

        return new AdminStatsResponse(citizens, collectors, pending, completed);
    }

    @Override
    public List<UserResponse> getCollectors() {
        List<User> list = userRepository.findByRole(UserRole.COLLECTOR).orElse(List.of());
        return list.stream()
                .map(u -> new UserResponse(u.getId(), u.getUsername(), u.getLastName() + " " + u.getFirstName(), u.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponse> getAllTransactions() {
        return paymentTransactionRepository.findAll().stream()
                .map(tx -> TransactionResponse.builder()
                        .id(tx.getId())
                        .subscriptionId(tx.getSubscription() != null ? tx.getSubscription().getId() : null)
                        .amount(tx.getAmount())
                        .bankCode(tx.getBankCode())
                        .vnpTransactionNo(tx.getVnpTransactionNo())
                        .responseCode(tx.getResponseCode())
                        .orderInfo(tx.getOrderInfo())
                        .payDate(tx.getPayDate())
                        .build())
                .collect(Collectors.toList());
    }
}
