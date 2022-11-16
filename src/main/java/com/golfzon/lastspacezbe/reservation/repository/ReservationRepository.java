package com.golfzon.lastspacezbe.reservation.repository;

import com.golfzon.lastspacezbe.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReservationRepository extends JpaRepository<Reservation,Long> {

    // 예약취소
    Reservation findAllByReservationId(Long reservationId);
//    List<TroubleComment> findAllByNickname(String nickname);
}
