package org.swp391_group4_backend.ecosolution.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.swp391_group4_backend.ecosolution.constant.SubscriptionStatus;
import org.swp391_group4_backend.ecosolution.entity.CitizenSubscription;
import org.swp391_group4_backend.ecosolution.entity.PaymentTransaction;
import org.swp391_group4_backend.ecosolution.repository.CitizenSubscriptionRepository;
import org.swp391_group4_backend.ecosolution.repository.PaymentTransactionRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    private CitizenSubscriptionRepository subscriptionRepository;

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    @Mock
    private VNPAYPaymentService paymentService;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private CitizenSubscription pendingSubscription;
    private Map<String, String> successParams;

    @BeforeEach
    void setUp() {
        pendingSubscription = new CitizenSubscription();
        pendingSubscription.setId(1L);
        pendingSubscription.setStatus(SubscriptionStatus.PENDING_PAYMENT);

        successParams = new HashMap<>();
        successParams.put("vnp_ResponseCode", "00");
        successParams.put("vnp_TxnRef", "1");
        successParams.put("vnp_Amount", "1000000");
        successParams.put("vnp_BankCode", "NCB");
        successParams.put("vnp_TransactionNo", "12345");
        successParams.put("vnp_OrderInfo", "Test Payment");
        successParams.put("vnp_PayDate", "20231027101010");
    }

    @Test
    void processPaymentResult_Success() {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(pendingSubscription));

        subscriptionService.processPaymentResult(successParams);

        assertEquals(SubscriptionStatus.ACTIVE, pendingSubscription.getStatus());
        assertNotNull(pendingSubscription.getEndDate());
        verify(paymentTransactionRepository, times(1)).save(any(PaymentTransaction.class));
        verify(subscriptionRepository, times(1)).save(pendingSubscription);
    }

    @Test
    void processPaymentResult_AlreadyActive_ShouldSkip() {
        pendingSubscription.setStatus(SubscriptionStatus.ACTIVE);
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(pendingSubscription));

        subscriptionService.processPaymentResult(successParams);

        verify(paymentTransactionRepository, never()).save(any());
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    void processPaymentResult_FailureResponse() {
        successParams.put("vnp_ResponseCode", "99");
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(pendingSubscription));

        subscriptionService.processPaymentResult(successParams);

        assertEquals(SubscriptionStatus.CANCELLED, pendingSubscription.getStatus());
        verify(paymentTransactionRepository, never()).save(any());
        verify(subscriptionRepository, times(1)).save(pendingSubscription);
    }

    @Test
    void processIpn_Success() {
        when(paymentService.verifyIpnSignature(any())).thenReturn(true);
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(pendingSubscription));

        Map<String, String> response = subscriptionService.processIpn(successParams);

        assertEquals("00", response.get("RspCode"));
        assertEquals(SubscriptionStatus.ACTIVE, pendingSubscription.getStatus());
        verify(subscriptionRepository).save(pendingSubscription);
    }

    @Test
    void processIpn_InvalidSignature() {
        when(paymentService.verifyIpnSignature(any())).thenReturn(false);

        Map<String, String> response = subscriptionService.processIpn(successParams);

        assertEquals("97", response.get("RspCode"));
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    void processIpn_OrderNotFound() {
        when(paymentService.verifyIpnSignature(any())).thenReturn(true);
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.empty());

        Map<String, String> response = subscriptionService.processIpn(successParams);

        assertEquals("01", response.get("RspCode"));
    }

    @Test
    void processIpn_AlreadyConfirmed() {
        pendingSubscription.setStatus(SubscriptionStatus.ACTIVE);
        when(paymentService.verifyIpnSignature(any())).thenReturn(true);
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(pendingSubscription));

        Map<String, String> response = subscriptionService.processIpn(successParams);

        assertEquals("02", response.get("RspCode"));
        verify(subscriptionRepository, never()).save(any());
    }
}
