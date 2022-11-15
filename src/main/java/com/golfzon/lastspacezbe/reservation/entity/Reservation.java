package com.golfzon.lastspacezbe.reservation.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column
    Long reservation_id; // 예약 번호

    String start_date;
    String end_date;
    String status;
    String pay_status;
    int price;
    String prepay;
    String reserve_time;
    String imp_uid;
    String prepay_uid;
    String postpay_uid;





}
