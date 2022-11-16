package com.golfzon.lastspacezbe.reservation.service;

import com.golfzon.lastspacezbe.reservation.dto.ReservationRequestDto;
import com.golfzon.lastspacezbe.reservation.entity.Reservation;
import com.golfzon.lastspacezbe.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    // 예약 취소하기
    public void cancel(Long reservationId){

        Reservation reservation = reservationRepository.findAllByReservationId(reservationId);
        reservation.setStatus("002"); // 예약상태를 예약 취소로 바꾸기
        reservationRepository.save(reservation); // 002 예약상태 저장

    }
}
