package org.swp391_group4_backend.ecosolution.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompleteTaskRequest {

    @NotBlank(message = "Image is required")
    private String proofImageUrl;
    private String note;

}
