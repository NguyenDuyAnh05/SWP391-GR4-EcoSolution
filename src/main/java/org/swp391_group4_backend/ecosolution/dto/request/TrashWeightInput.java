package org.swp391_group4_backend.ecosolution.dto.request;

import lombok.Data;
import org.swp391_group4_backend.ecosolution.constant.WasteType;

@Data
public class TrashWeightInput {
    private WasteType category;
    private Double weightInKg;
}
