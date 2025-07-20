package com.sigmat.lms.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class VideoLesson extends Lesson {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    @JsonIgnore
    private Video video;
}
