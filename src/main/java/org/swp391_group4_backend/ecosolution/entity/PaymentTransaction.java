package org.swp391_group4_backend.ecosolution.entity;

import jakarta.persistence.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transaction")
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private CitizenSubscription subscription;
    BigDecimal amount;
    private String bankCode;
    private String vnpTransactionNo;
    private String responseCode;
    private String orderInfo;
    private LocalDateTime payDate;
}
