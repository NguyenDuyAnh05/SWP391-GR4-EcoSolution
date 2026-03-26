package org.swp391_group4_backend.ecosolution.entity;

import jakarta.persistence.*;
import lombok.*;
import org.swp391_group4_backend.ecosolution.constant.UserRole;

import java.time.LocalDateTime;

@Entity
@Getter // Dùng Getter/Setter thay cho @Data để tránh vòng lặp toString tự động
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id")
    private Ward ward;

    private Double latitude;
    private Double longitude;

    @Builder.Default // QUAN TRỌNG: Để Builder không làm mất giá trị mặc định
    private LocalDateTime createdAt = LocalDateTime.now();

    public String getFullName(){
        return firstName + " " + lastName;
    }
}