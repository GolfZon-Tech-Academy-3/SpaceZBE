package com.golfzon.lastspacezbe.reservation.service;

import com.golfzon.lastspacezbe.reservation.dto.ReservationRequestDto;
import com.golfzon.lastspacezbe.reservation.entity.Reservation;
import com.golfzon.lastspacezbe.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    // 예약하기
    public void reserve(ReservationRequestDto requestDto){

        // 선결제
        Reservation reservation = new Reservation(
                requestDto.getReservationName(),requestDto.getStartDate(),requestDto.getEndDate(),
        "001", "002", requestDto.getPrice(),"000","imp","prepay","postPay");
        // 저장
        reservationRepository.save(reservation);
    }

    // 오피스 예약 취소하기
    public void officeCancel(Long reservationId){

        Reservation reservation = reservationRepository.findAllByReservationId(reservationId);
        reservation.setStatus("002"); // 예약상태를 예약 취소로 바꾸기
        reservationRepository.save(reservation); // 002 예약상태 저장

    }
    // 데스크/회의실 예약 취소하기
    public void deskCancel(Long reservationId){

        Reservation reservation = reservationRepository.findAllByReservationId(reservationId);
        reservation.setStatus("002"); // 예약상태를 예약 취소로 바꾸기
        // 예약한 시간
        LocalDateTime reserveTime = reservationRepository.findByReservationId(reservationId).getReserveTime();
        // 현재 시간
        LocalDateTime currentDateTime = LocalDateTime.now();
        // 예약한 시간 현재시간 차이
        Duration duration = Duration.between(reserveTime, currentDateTime);

        log.info("reserveTime : {}",reserveTime);
        log.info("currentTime : {}",currentDateTime);
        log.info("Time check : {}초",duration.getSeconds());

        // 예약한지 1시간 이내
        if(duration.getSeconds() <= 3600){

        }
        // 예약한지 1시간 이후
        else if(duration.getSeconds() > 3600){

        }

        reservationRepository.save(reservation); // 002 예약상태 저장

    }
}
