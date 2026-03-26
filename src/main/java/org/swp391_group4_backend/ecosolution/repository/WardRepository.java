package org.swp391_group4_backend.ecosolution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp391_group4_backend.ecosolution.entity.Ward;

import java.util.Optional;

@Repository
public interface WardRepository extends JpaRepository<Ward, Long> {

    // Tìm Phường theo tên để lấy ra thông tin Phường (bao gồm cả Collector bên trong)
    Optional<Ward> findByWardName(String wardName);
}
