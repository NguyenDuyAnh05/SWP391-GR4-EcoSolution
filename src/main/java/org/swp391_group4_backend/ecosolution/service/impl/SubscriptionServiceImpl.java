package org.swp391_group4_backend.ecosolution.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp391_group4_backend.ecosolution.constant.SubscriptionStatus;
import org.swp391_group4_backend.ecosolution.entity.CitizenSubscription;
import org.swp391_group4_backend.ecosolution.entity.PaymentTransaction;
import org.swp391_group4_backend.ecosolution.repository.CitizenSubscriptionRepository;
import org.swp391_group4_backend.ecosolution.repository.PaymentTransactionRepository;
import org.swp391_group4_backend.ecosolution.service.SubscriptionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final CitizenSubscriptionRepository subscriptionRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final VNPAYPaymentService paymentService;
    @Override
    @Transactional
    public void processPaymentResult(Map<String, String> vnpayParams) {
        String responseCode = vnpayParams.get("vnp_ResponseCode");
        String subIdStr = vnpayParams.get("vnp_TxnRef");

// 1. Rút phần parse ID và tìm Subscription ra ngoài (Dùng chung cho cả IF và ELSE)
        Long subId = Long.parseLong(subIdStr);
        CitizenSubscription sub = subscriptionRepository.findById(subId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

// 2. CHỐNG LẶP GIAO DỊCH (Quan trọng nhất)
        if (sub.getStatus() == SubscriptionStatus.ACTIVE) {
            // Nếu đơn đã kích hoạt rồi (do IPN hoặc Return URL đã xử lý trước đó), bỏ qua luôn
            return;
        }

        if ("00".equals(responseCode)) {
            // 3. Cập nhật Subscription
            sub.setStatus(SubscriptionStatus.ACTIVE);
            sub.setEndDate(java.time.LocalDate.now().plusMonths(1));

            // 4. Lấy thời gian chuẩn từ VNPay (Format: yyyyMMddHHmmss)
            String vnpPayDateStr = vnpayParams.get("vnp_PayDate");
            java.time.LocalDateTime payDate = java.time.LocalDateTime.now(); // Fallback
            if (vnpPayDateStr != null && vnpPayDateStr.length() == 14) {
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                payDate = java.time.LocalDateTime.parse(vnpPayDateStr, formatter);
            }

            // 5. Lưu lịch sử giao dịch
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .subscription(sub)
                    .amount(new BigDecimal(vnpayParams.get("vnp_Amount")).divide(new BigDecimal(100)))
                    .bankCode(vnpayParams.get("vnp_BankCode"))
                    .vnpTransactionNo(vnpayParams.get("vnp_TransactionNo"))
                    .responseCode(responseCode)
                    .orderInfo(vnpayParams.get("vnp_OrderInfo"))
                    .payDate(payDate)
                    .build();

            paymentTransactionRepository.save(transaction);

        } else {
            // Xử lý thất bại (Tùy logic nghiệp vụ nhóm bạn quyết định)
            // Nếu vẫn muốn hủy luôn thì để CANCELLED, nếu cho phép thanh toán lại thì cứ để PENDING_PAYMENT
            sub.setStatus(SubscriptionStatus.CANCELLED);
        }

// 6. Lưu Subscription (Dùng chung 1 lệnh save cho cả trường hợp Thành công và Thất bại)
        subscriptionRepository.save(sub);
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
