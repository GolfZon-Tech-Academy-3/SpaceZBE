package com.golfzon.lastspacezbe.mileage.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Mileage {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_mileage")
	@SequenceGenerator(sequenceName = "seq_mileage", allocationSize = 1, name="seq_mileage")
	@Column(name = "mileage_id")
	private long mileageId;

	@Column(name = "member_id")
	Long memberId;

	@Column(name = "space_id")
	private long spaceId;

	@Column(name = "space_name")
	private String spaceName;
	private int score;
	private String status;

	@CreationTimestamp
	LocalDateTime mileageDate;



	@Override
	public String toString() {
		return "Mileage{" +
				"mileageId=" + mileageId +
				", memberId=" + memberId +
				", spaceId=" + spaceId +
				", spaceName='" + spaceName + '\'' +
				", score=" + score +
				", mileageDate='" + mileageDate + '\'' +
				", status='" + status + '\'' +
				'}';
	}
}
