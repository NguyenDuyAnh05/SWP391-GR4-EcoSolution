package org.swp391_group4_backend.ecosolution.reporting.domain.entity;

import org.swp391_group4_backend.ecosolution.core.domain.entity.User;
import org.swp391_group4_backend.ecosolution.core.domain.entity.Ward;
import org.swp391_group4_backend.ecosolution.reporting.domain.ReportStatus;
import org.swp391_group4_backend.ecosolution.reporting.domain.WasteType;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "waste_reports")
public class WasteReport {
    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    private WasteType wasteType;

    @Column(name = "address")
    private String address;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "quantity")
    private Double quantity;

    @Lob
    @Column(name = "image", columnDefinition = "MEDIUMBLOB")
    private byte[] image;
    
    @Column(name = "image_path")
    private String imagePath;

    // actualQuantity measured by collector when collection is completed
    @Column(name = "actual_quantity")
    private Double actualQuantity;

    // proof image provided by collector on completion
    @Lob
    @Column(name = "proof_image", columnDefinition = "MEDIUMBLOB")
    private byte[] proofImage;

    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "ward_id")
    private Ward ward;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    // assignment fields
    @Column(name = "assigned_to")
    private java.util.UUID assignedTo;

    @Column(name = "assigned_by")
    private java.util.UUID assignedBy;

    @Column(name = "assigned_at")
    private OffsetDateTime assignedAt;

    private OffsetDateTime createdAt = OffsetDateTime.now();

    // getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public WasteType getWasteType() { return wasteType; }
    public void setWasteType(WasteType wasteType) { this.wasteType = wasteType; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public byte[] getImage() { return image; }
    public void setImage(byte[] image) { this.image = image; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public Double getActualQuantity() { return actualQuantity; }
    public void setActualQuantity(Double actualQuantity) { this.actualQuantity = actualQuantity; }
    public byte[] getProofImage() { return proofImage; }
    public void setProofImage(byte[] proofImage) { this.proofImage = proofImage; }
    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }
    public Ward getWard() { return ward; }
    public void setWard(Ward ward) { this.ward = ward; }
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public java.util.UUID getAssignedTo() { return assignedTo; }
    public void setAssignedTo(java.util.UUID assignedTo) { this.assignedTo = assignedTo; }
    public java.util.UUID getAssignedBy() { return assignedBy; }
    public void setAssignedBy(java.util.UUID assignedBy) { this.assignedBy = assignedBy; }
    public OffsetDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(OffsetDateTime assignedAt) { this.assignedAt = assignedAt; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}

