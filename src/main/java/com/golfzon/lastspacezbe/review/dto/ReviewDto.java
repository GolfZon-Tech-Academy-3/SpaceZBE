package com.golfzon.lastspacezbe.review.dto;

import com.golfzon.lastspacezbe.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    Long reservationId;
    Long reviewId;
    Long companyId;
    Long spaceId;
    String memberImage;
    String memberName;
    double rating;
    String type;
    String spaceName;
    String content;

    public ReviewDto(Review review) {
        this.reviewId = review.getReviewId();
        this.rating = review.getRating();
        this.content = review.getContent();
    }

    @Override
    public String toString() {
        return "ReviewDto{" +
                "reservationId=" + reservationId +
                ", reviewId=" + reviewId +
                ", companyId=" + companyId +
                ", spaceId=" + spaceId +
                ", memberImage='" + memberImage + '\'' +
                ", memberName='" + memberName + '\'' +
                ", rating=" + rating +
                ", type='" + type + '\'' +
                ", spaceName='" + spaceName + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
