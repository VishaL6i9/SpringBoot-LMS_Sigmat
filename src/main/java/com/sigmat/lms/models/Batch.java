package com.sigmat.lms.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "batches")
public class Batch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Long batchId;

    @Column(nullable = false)
    private String batchName;

    @Column(unique = true)
    private String batchCode;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Institute relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institute_id", nullable = false)
    private Institute institute;

    // Primary instructor for the batch
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    // Course assignment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    // Batch schedule
    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "max_students")
    private Integer maxStudents;

    // Academic details
    private String semester;
    private String academicYear;
    private String grade;
    private String division;

    // Students in this batch
    @JsonIgnore
    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Users> students;

    // Batch status
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private BatchStatus status = BatchStatus.PLANNED;

    @Builder.Default
    @Column(name = "is_active")
    private boolean isActive = true;

    // Timestamps
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum BatchStatus {
        PLANNED,
        ACTIVE,
        COMPLETED,
        CANCELLED,
        SUSPENDED
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Batch batch = (Batch) o;
        return batchId != null && batchId.equals(batch.batchId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}