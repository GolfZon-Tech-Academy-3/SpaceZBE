package com.golfzon.lastspacezbe.review.entity;

import com.golfzon.lastspacezbe.review.dto.ReviewDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(sequenceName = "seq_review", allocationSize = 1, name = "seq_review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "review_id")
    Long reviewId; // 리뷰 번호

    //    @ManyToOne
//    @JoinColumn(name = "company_id", nullable = false)
    @Column(name = "space_id")
    Long spaceId;

    @Column(name = "reservation_id")
    Long reservationId;
    @Column(name = "company_id")
    Long companyId;
    @Column(name = "member_id")
    Long memberId;
    String content;
    double rating; // 리뷰점수

    @CreationTimestamp
    LocalDateTime reviewTime;


    public Review(ReviewDto requestDto, Long memberId) {
        this.spaceId = requestDto.getSpaceId();
        this.companyId = requestDto.getCompanyId();
        this.memberId = memberId;
        this.content = requestDto.getContent();
        this.rating = requestDto.getRating();
        this.reservationId = requestDto.getReservationId();
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewId=" + reviewId +
                ", spaceId=" + spaceId +
                ", reservationId=" + reservationId +
                ", companyId=" + companyId +
                ", memberId=" + memberId +
                ", content='" + content + '\'' +
                ", rating=" + rating +
                ", reviewTime=" + reviewTime +
                '}';
    }
}
