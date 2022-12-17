package com.golfzon.lastspacezbe.space.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SpaceImage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_spaceImage")
    @SequenceGenerator(sequenceName = "seq_spaceImage", allocationSize = 1, name="seq_spaceImage")
    @Column(name = "spaceImage_id")
    private Long spaceImageId; // 예약 번호

    private String spaceImage; // image url

    @ManyToOne
    @JoinColumn(name = "space_id", nullable = false)
    private Space space;

    public SpaceImage(String spaceImage,Space space) {
        this.spaceImage = spaceImage;
        this.space = space;
    }
}
