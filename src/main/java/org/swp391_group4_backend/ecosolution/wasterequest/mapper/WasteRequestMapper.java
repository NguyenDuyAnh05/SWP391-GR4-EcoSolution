package org.swp391_group4_backend.ecosolution.wasterequest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.swp391_group4_backend.ecosolution.wasterequest.domain.dto.request.WasteRequestCreateRequestDto;
import org.swp391_group4_backend.ecosolution.wasterequest.domain.dto.response.WasteRequestResponseDto;
import org.swp391_group4_backend.ecosolution.wasterequest.domain.entity.WasteRequest;

@Mapper(componentModel = "spring")
public interface WasteRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "citizen", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assignedCollector", ignore = true)
    @Mapping(target = "actualWasteType", ignore = true)
    @Mapping(target = "actualQuantity", ignore = true)
    @Mapping(target = "evidenceImage", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    WasteRequest toEntity(WasteRequestCreateRequestDto dto);

    @Mapping(source = "citizen.id", target = "citizenId")
    @Mapping(source = "citizen.name", target = "citizenName")
    @Mapping(source = "assignedCollector.id", target = "assignedCollectorId")
    @Mapping(source = "assignedCollector.name", target = "assignedCollectorName")
    @Mapping(source = "evidenceImage", target = "hasEvidenceImage", qualifiedByName = "hasEvidence")
    WasteRequestResponseDto toResponseDto(WasteRequest entity);

    @Named("hasEvidence")
    default boolean hasEvidence(byte[] evidenceImage) {
        return evidenceImage != null && evidenceImage.length > 0;
    }
}
