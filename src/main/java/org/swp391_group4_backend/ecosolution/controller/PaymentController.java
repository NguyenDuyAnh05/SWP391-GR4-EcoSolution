package org.swp391_group4_backend.ecosolution.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.swp391_group4_backend.ecosolution.entity.CitizenSubscription;
import org.swp391_group4_backend.ecosolution.repository.CitizenSubscriptionRepository;
import org.swp391_group4_backend.ecosolution.service.PaymentService;
import org.swp391_group4_backend.ecosolution.service.SubscriptionService;
import org.swp391_group4_backend.ecosolution.service.impl.VNPAYPaymentService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final SubscriptionService subscriptionService;

    // BEST PRACTICE 1: Inject Interface, không inject Class cụ thể
    private final PaymentService paymentService;

    // BEST PRACTICE 2: Đã xóa CitizenSubscriptionRepository khỏi Controller

    // Nếu bạn muốn giữ API này để hỗ trợ tính năng "Thanh toán lại" cho hóa đơn cũ
    @GetMapping("/create-url")
    public ResponseEntity<String> createPayment(
            @RequestParam Long subscriptionId, // BEST PRACTICE 3: Chỉ nhận ID, không nhận tiền mặt
            HttpServletRequest request) {

        // Lý tưởng nhất: Logic tìm đơn hàng và lấy số tiền này nên nằm trong SubscriptionService
        // Tạm thời mình viết giả mã (pseudo-code) để bạn hiểu luồng:
        /*
        CitizenSubscription sub = subscriptionService.findById(subscriptionId);
        String paymentUrl = paymentService.createPaymentUrl(
                sub.getTier().getMonthlyFee(),
                "Thanh toan lai cho Sub ID: " + subscriptionId,
                String.valueOf(subscriptionId), // Truyền ID làm vnp_TxnRef
                request
        );
        return ResponseEntity.ok(paymentUrl);
        */

        return ResponseEntity.status(501).body("Tạm thời gọi tạo link qua API /users/activate");
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<String> vnpayReturn(@RequestParam Map<String, String> params) {
        subscriptionService.processPaymentResult(params);

        String responseCode = params.get("vnp_ResponseCode");
        if ("00".equals(responseCode)) {
            return ResponseEntity.ok("Thanh toán thành công! Cảm ơn bạn đã sử dụng dịch vụ.");
        }
        return ResponseEntity.badRequest().body("Thanh toán thất bại hoặc đã bị hủy.");
    }

    @GetMapping("/vnpay-ipn")
    public ResponseEntity<Map<String, String>> vnpayIpn(@RequestParam Map<String, String> params) {
        Map<String, String> result = subscriptionService.processIpn(params);
        return ResponseEntity.ok(result);
    }

}
