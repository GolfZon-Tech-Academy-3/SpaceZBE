package com.golfzon.lastspacezbe.reservation.entity;

import com.golfzon.lastspacezbe.space.entity.Space;
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
@SequenceGenerator(sequenceName = "seq_reservation", allocationSize = 1, name="seq_reservation")
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "reservation_id")
    Long reservationId; // 예약 번호
    @Column(name = "start_date")
    String startDate; // 이용 시작 날짜
    @Column(name = "end_date")
    String endDate; // 이용 끝 날짜
    String status; // 예약 상태
    @Column(name = "pay_status")
    String payStatus; // 결제 상태
    int price; // 이용 가격
    String prepay; // 결제종류 (선결제, 보증금 결제, 후결제)
    @Column(name = "imp_uid")
    String impUid;
    @Column(name = "prepay_uid")
    String prepayUid;
    @Column(name = "postpay_uid")
    String postpayUid;
    @Column(name = "reservation_name")
    String reservationName;

    @CreationTimestamp
    LocalDateTime reserveTime;

    @Column(name = "company_id")
    Long companyId;

    @Column(name = "member_id")
    Long memberId;

    @Column(name = "space_id")
    Long spaceId;

    @Column(name = "mileage")
    int mileage;



//    @ManyToOne
//    @JoinColumn(name = "member_id", nullable = false)
//    private Member member;

    // reserveTime 을 제외한 생성자
    public Reservation(Long memberId,String reservationName, String startDate, String endDate,
                       String status, String payStatus, int price, String prepay,
                       String impUid, String prepayUid, String postpayUid, int mileage, Long spaceId, Long companyId) {
        this.memberId = memberId;
        this.reservationName = reservationName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.payStatus = payStatus;
        this.price = price;
        this.prepay = prepay;
        this.impUid = impUid;
        this.prepayUid = prepayUid;
        this.postpayUid = postpayUid;
        this.mileage = mileage;
        this.spaceId = spaceId;
        this.companyId = companyId;
    }

    // 예약 취소 생성자
    public Reservation(Long reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId=" + reservationId +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", status='" + status + '\'' +
                ", payStatus='" + payStatus + '\'' +
                ", price=" + price +
                ", prepay='" + prepay + '\'' +
                ", impUid='" + impUid + '\'' +
                ", prepayUid='" + prepayUid + '\'' +
                ", postpayUid='" + postpayUid + '\'' +
                ", reservationName='" + reservationName + '\'' +
                ", reserveTime=" + reserveTime +
                ", companyId=" + companyId +
                ", memberId=" + memberId +
                '}';
    }
}
