package com.golfzon.lastspacezbe.payment;

import com.golfzon.lastspacezbe.payment.service.TossPaymentService;
import com.golfzon.lastspacezbe.reservation.dto.ReservationRequestDto;
import com.golfzon.lastspacezbe.reservation.entity.Reservation;
import com.golfzon.lastspacezbe.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final ReservationRepository reservationRepository;
    private final TossPaymentService tossPaymentService;

    @Scheduled(cron = "0 50 23 * * *")
        public void autoPostPay(){
        log.info("후결제 시작");
        LocalDate now = LocalDate.now();
        // 포맷 정의
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 포맷 적용
        String todayStart = now.format(formatter)+" 00:00";
        String todayEnd = now.format(formatter)+" 23:59";
        log.info("오늘 시작:{}",todayStart);
        log.info("오늘 끝:{}",todayEnd);
        List<Reservation> reservationList = reservationRepository.findAllTodayPostPayments(todayStart, todayEnd);
        for (Reservation reservation:reservationList) {
            log.info("저장 시작 reservation:{}",reservation);
            int originPrice = reservation.getPrice();
            if(reservation.getPrepay().equals("001")){
                int totalPrice = reservation.getPrice()+reservation.getMileage();
                int depositPrice = (int) (totalPrice * 0.2);
                reservation.setPrice(totalPrice-depositPrice-reservation.getMileage());
            }
            log.info("결제될 가격:{}", reservation.getPrice());
            tossPaymentService.tossPostReserve(new ReservationRequestDto(reservation));
            reservation.setPrice(originPrice);
            reservation.setPayStatus("002");
            reservationRepository.save(reservation);
            log.info("이용완료 저장:{}", reservation);
        }
    }
}
