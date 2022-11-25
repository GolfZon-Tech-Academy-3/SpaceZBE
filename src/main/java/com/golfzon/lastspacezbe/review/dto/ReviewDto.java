package com.golfzon.lastspacezbe.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    Long reviewId;
    Long companyId;
    Long spaceId;
    String memberImage;
    String memberName;
    double rating;
    String type;
    String spaceName;
    String content;

    @Override
    public String toString() {
        return "ReviewDto{" +
                "reviewId=" + reviewId +
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
