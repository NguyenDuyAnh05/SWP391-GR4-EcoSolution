package org.swp391_group4_backend.ecosolution.constant;

public enum SubscriptionStatus {
    PENDING_PAYMENT, // Chờ người dùng thanh toán
    ACTIVE,          // Đã thanh toán, đang sử dụng
    EXPIRED,         // Hết hạn 1 tháng, cần đóng tiền tiếp
    CANCELLED
}
