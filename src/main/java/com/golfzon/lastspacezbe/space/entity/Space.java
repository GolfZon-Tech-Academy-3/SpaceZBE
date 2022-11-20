package com.golfzon.lastspacezbe.space.entity;

import com.golfzon.lastspacezbe.company.entity.Company;
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
@SequenceGenerator(sequenceName = "seq_space", allocationSize = 1, name="seq_space")
public class Space {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "space_id")
    Long spaceId; // 예약 번호

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "space_name")
    String spaceName; // 사무공간 이름

    String facilities; // 편의시설
    String type; // 공간형태
    int price; //가격

    @Column(name = "open_time")
    String openTime; // 오픈시간

    @Column(name = "close_time")
    String closeTime; // 마감시간

    @Column(name = "break_open")
    String breakOpen; // 쉬는 시작시간 (청소)

    @Column(name = "break_close")
    String breakClose; // 쉬는 마감시간 (청소)

    public Space(String spaceName, String facilities, String type, int price,
                 String openTime, String closeTime, String breakOpen, String breakClose) {
//        this.company = company;
        this.spaceName = spaceName;
        this.facilities = facilities;
        this.type = type;
        this.price = price;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.breakOpen = breakOpen;
        this.breakClose = breakClose;
    }

}
