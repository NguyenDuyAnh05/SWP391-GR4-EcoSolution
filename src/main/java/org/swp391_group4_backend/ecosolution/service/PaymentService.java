package org.swp391_group4_backend.ecosolution.service;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

public interface PaymentService {
    String createPaymentUrl(BigDecimal amount, String orderInfo, String vnp_TxnRef, HttpServletRequest request);

}
