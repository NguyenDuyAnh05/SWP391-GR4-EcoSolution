package org.swp391_group4_backend.ecosolution.service;


import java.util.Map;

public interface SubscriptionService {
    void processPaymentResult(Map<String, String> vnpayParams);
    Map<String, String> processIpn(Map<String, String> vnpayParams);
}
