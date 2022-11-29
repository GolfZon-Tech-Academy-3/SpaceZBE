package com.golfzon.lastspacezbe.mileage.dto;

import com.golfzon.lastspacezbe.mileage.entity.Mileage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MileageDto {

	long mileageId;
	Long memberId;
	private long spaceId;
	private String spaceName;
	private int score;
	private String status;
	String mileageDate;

	public MileageDto(Mileage mileage, String mileageDate){
		this.mileageId = mileage.getMileageId();
		this.spaceId = mileage.getSpaceId();
		this.spaceName = mileage.getSpaceName();
		this.score = mileage.getScore();
		this.status = mileage.getStatus();
		this.mileageDate = mileageDate;
	}

	@Override
	public String toString() {
		return "mileageDto{" +
				"mileageId=" + mileageId +
				", memberId=" + memberId +
				", spaceId=" + spaceId +
				", spaceName='" + spaceName + '\'' +
				", score=" + score +
				", status='" + status + '\'' +
				", mileageDate=" + mileageDate +
				'}';
	}
}
