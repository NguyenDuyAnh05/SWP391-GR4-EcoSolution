package org.swp391_group4_backend.ecosolution.core.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "wards")
public class Ward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Ward w = new Ward();
        public Builder name(String name) { w.setName(name); return this; }
        public Ward build() { return w; }
    }
}