package com.golfzon.lastspacezbe.reservation.repository;

import com.golfzon.lastspacezbe.reservation.dto.ReservationResponseDto;
import com.golfzon.lastspacezbe.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ReservationRepository extends JpaRepository<Reservation,Long> {

    // 예약취소
    Reservation findAllByReservationId(Long reservationId);

    // 업체 예약 현황용 (백오피스)
    List<Reservation> findAllByCompanyIdOrderByStartDateDesc(Long companyId);
    List<Reservation> findAllByCompanyId(Long companyId);
    // 예약한 시간
    Reservation findByReservationId(Long reservationId);
    // 현재시간 기준 이후의 예약들 조회
    @Query(nativeQuery = true, value = "select * from reservation where space_id = ?1 and (to_date(start_date, 'YYYY-MM-DD HH24:MI')>= to_date(TO_CHAR(NOW(), 'YYYY-MM-DD HH24:MI'), 'YYYY-MM-DD HH24:MI')) and status='001'")
    List<Reservation> findReservedTime(Long spaceId);

    // 나의 예약 현황(마이페이지)
    List<Reservation> findAllByMemberId(Long memberId);

    // 기간별 예약 조회
    // 현재시간 기준 이후의 예약들 조회
    @Query(nativeQuery = true, value = "select * from reservation where company_id =?1 and (status='001' or status='004')")
    List<Reservation> findReservations(Long compnayId);

    // 오늘 후결제될 예약들 조회
    @Query(nativeQuery = true, value = "select * from (select * from reservation where toss = true) reservation where (to_date(end_date, 'YYYY-MM-DD HH24:MI')>= to_date(?1, 'YYYY-MM-DD HH24:MI')) and (to_date(end_date, 'YYYY-MM-DD HH24:MI') <= to_date(?2, 'YYYY-MM-DD HH24:MI')) and status !='002' and (pay_status='001' or pay_status='003')")
    List<Reservation> findAllTodayPostPayments(String todayStart, String todayEnd);

    Reservation findByPostpayUid(String merchant_uid);


//    @Query(value = "SELECT r (TO_DATE(TO_CHAR(NOW(), 'YYYY-MM-DD HH24:MI'),'YYYY-MM-DD HH24:MI') - TO_DATE(r.reserveTime,'YYYY-MM-DD HH24:MI'))*24 from Reservation r where r.reservationId=?1")
//    float findByOneTime(Long reservationId);
//    List<TroubleComment> findAllByNickname(String nickname);
}
