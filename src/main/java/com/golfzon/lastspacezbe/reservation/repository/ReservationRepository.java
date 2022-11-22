package com.golfzon.lastspacezbe.reservation.repository;

import com.golfzon.lastspacezbe.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ReservationRepository extends JpaRepository<Reservation,Long> {

    // 예약취소
    Reservation findAllByReservationId(Long reservationId);

    // 오늘 예약 조회용
    List<Reservation> findAllByCompanyId(Long companyId);
    // 예약한 시간
    Reservation findByReservationId(Long reservationId);
    // 현재시간 기준 이후의 예약들 조회
    @Query(nativeQuery = true, value = "select * from reservation where space_id = ?1 and (to_date(start_date, 'YYYY-MM-DD HH24:MI')>= to_date(TO_CHAR(NOW(), 'YYYY-MM-DD HH24:MI'), 'YYYY-MM-DD HH24:MI'))")
    List<Reservation> findReservedTime(Long spaceId);

//    @Query(value = "SELECT r (TO_DATE(TO_CHAR(NOW(), 'YYYY-MM-DD HH24:MI'),'YYYY-MM-DD HH24:MI') - TO_DATE(r.reserveTime,'YYYY-MM-DD HH24:MI'))*24 from Reservation r where r.reservationId=?1")
//    float findByOneTime(Long reservationId);
//    List<TroubleComment> findAllByNickname(String nickname);
}
