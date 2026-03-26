package org.swp391_group4_backend.ecosolution.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp391_group4_backend.ecosolution.constant.SubscriptionStatus;
import org.swp391_group4_backend.ecosolution.entity.CitizenSubscription;
import org.swp391_group4_backend.ecosolution.repository.CitizenSubscriptionRepository;
import org.swp391_group4_backend.ecosolution.service.SubscriptionService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final CitizenSubscriptionRepository subscriptionRepository;
    private final VNPAYPaymentService paymentService;
    @Override
    @Transactional
    public void processPaymentResult(Map<String, String> vnpayParams) {
        String responseCode = vnpayParams.get("vnp_ResponseCode");
        String subIdStr = vnpayParams.get("vnp_TxnRef");

        if ("00".equals(responseCode)) {
            Long subId = Long.parseLong(subIdStr);
            CitizenSubscription sub = subscriptionRepository.findById(subId)
                    .orElseThrow(() -> new RuntimeException("Subscription not found"));

            // Logic nghiệp vụ nằm ở đây
            sub.setStatus(SubscriptionStatus.ACTIVE);
            sub.setEndDate(LocalDate.now().plusMonths(1));

            subscriptionRepository.save(sub);
        } else {
            // Xử lý nếu thanh toán thất bại (ví dụ: đổi status thành CANCELLED)
            Long subId = Long.parseLong(subIdStr);
            subscriptionRepository.findById(subId).ifPresent(sub -> {
                sub.setStatus(SubscriptionStatus.CANCELLED);
                subscriptionRepository.save(sub);
            });
        }
    }

    @Override
    public Map<String, String> processIpn(Map<String, String> vnpayParams) {
        Map<String, String> response = new HashMap<>();

        try {
            // 1. Kiểm tra chữ ký bảo mật (Verify Signature)
            if (!paymentService.verifyIpnSignature(vnpayParams)) {
                response.put("RspCode", "97");
                response.put("Message", "Invalid signature");
                return response;
            }

            // 2. Lấy thông tin đơn hàng
            String txnRef = vnpayParams.get("vnp_TxnRef"); // Đây là ID của CitizenSubscription
            String responseCode = vnpayParams.get("vnp_ResponseCode");
            Long subId = Long.parseLong(txnRef);

            // 3. Kiểm tra sự tồn tại của đơn hàng trong DB
            Optional<CitizenSubscription> subOpt = subscriptionRepository.findById(subId);

            if (subOpt.isEmpty()) {
                response.put("RspCode", "01");
                response.put("Message", "Order not found");
                return response;
            }

            CitizenSubscription sub = subOpt.get();

            // 4. Kiểm tra trạng thái đơn hàng (Chỉ xử lý nếu đang PENDING)
            if (sub.getStatus() != SubscriptionStatus.PENDING_PAYMENT) {
                response.put("RspCode", "02");
                response.put("Message", "Order already confirmed");
                return response;
            }

            // 5. Cập nhật kết quả thanh toán
            if ("00".equals(responseCode)) {
                // THÀNH CÔNG
                sub.setStatus(SubscriptionStatus.ACTIVE);
                sub.setEndDate(java.time.LocalDate.now().plusMonths(1));
                // Có thể lưu thêm mã giao dịch VNPay vào nếu cần đối soát
            } else {
                // THẤT BẠI (Khách hủy hoặc lỗi thẻ)
                sub.setStatus(SubscriptionStatus.CANCELLED);
            }

            subscriptionRepository.save(sub);

            // 6. Phản hồi thành công cho VNPay
            response.put("RspCode", "00");
            response.put("Message", "Confirm Success");

        } catch (Exception e) {
            response.put("RspCode", "99");
            response.put("Message", "Unknown error");
        }

        return response;

    }


}
