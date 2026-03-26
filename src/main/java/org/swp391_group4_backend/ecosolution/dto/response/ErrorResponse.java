package org.swp391_group4_backend.ecosolution.dto.response;

import java.time.LocalDateTime;

//standardization response to the client code
public record ErrorResponse(
        int statusCode,      // Ví dụ: 400, 404, 500
        String message,      // Ví dụ: "Username already exists"
        LocalDateTime time   // Thời gian xảy ra lỗi
) {
}
