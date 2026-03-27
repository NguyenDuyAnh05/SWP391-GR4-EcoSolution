package org.swp391_group4_backend.ecosolution.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private Long id;
    private Long subscriptionId;
    private BigDecimal amount;
    private String bankCode;
    private String vnpTransactionNo;
    private String responseCode;
    private String orderInfo;
    private LocalDateTime payDate;
}
