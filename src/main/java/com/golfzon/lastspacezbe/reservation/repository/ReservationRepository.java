package com.golfzon.lastspacezbe.reservation.repository;

import com.golfzon.lastspacezbe.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ReservationRepository extends JpaRepository<Reservation,Long> {

    // 예약취소
    Reservation findAllByReservationId(Long reservationId);
    // 예약한 시간
    Reservation findByReservationId(Long reservationId);

//    @Query(value = "SELECT r (TO_DATE(TO_CHAR(NOW(), 'YYYY-MM-DD HH24:MI'),'YYYY-MM-DD HH24:MI') - TO_DATE(r.reserveTime,'YYYY-MM-DD HH24:MI'))*24 from Reservation r where r.reservationId=?1")
//    float findByOneTime(Long reservationId);
//    List<TroubleComment> findAllByNickname(String nickname);
}
