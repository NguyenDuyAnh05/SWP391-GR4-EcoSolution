package org.swp391_group4_backend.ecosolution.mapper;

import java.util.List;
import java.util.stream.Collectors;

public interface BaseMapper <D, E>{
    // Chuyển đổi một Entity đơn lẻ sang Response DTO (Dùng ở Controller)
    D toDto(E entity);

    // Chuyển đổi một DTO sang Entity (Thường dùng cho Request)
    E toEntity(D dto);

    // Chuyển đổi danh sách Entities sang danh sách DTOs
    // Dùng default để không cần viết lại logic ở các lớp con
    default List<D> toDtoList(List<E> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
