package com.golfzon.lastspacezbe.reservation.service;

import com.golfzon.lastspacezbe.reservation.dto.ReservationResponseDto;
import com.golfzon.lastspacezbe.reservation.entity.Reservation;
import com.golfzon.lastspacezbe.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReservMypageService {

    private final ReservationRepository reservationRepository;

    // 예약 이력
    public List<ReservationResponseDto> totalReserveSelectAll(Long memberId) {

        List<ReservationResponseDto> reservationResponseDtos = new ArrayList<>();
        List<Reservation> reservations = reservationRepository.findAllByMemberId(memberId);
        for (Reservation data : reservations
        ) {
            // 예약 취소와 예약 완료 상태 일때 만
            if (data.getStatus().equals("002") || data.getStatus().equals("004")) {
                reserveResponse(reservationResponseDtos, data);
            }
        }
        return reservationResponseDtos;
    }

    // 예약 현황
    public List<ReservationResponseDto> proceedReserveSelectAll(Long memberId) {
        List<ReservationResponseDto> reservationResponseDtos = new ArrayList<>();
        List<Reservation> reservations = reservationRepository.findAllByMemberId(memberId);
        for (Reservation data : reservations
        ) {
            // 예약 취소와 예약 완료 상태 일때 만
            if (data.getStatus().equals("001")) {
                reserveResponse(reservationResponseDtos, data);
            }
        }
        return reservationResponseDtos;
    }
    // 예약 response 메서드
    private void reserveResponse(List<ReservationResponseDto> reservationResponseDtos, Reservation data) {
        ReservationResponseDto responseDto = new ReservationResponseDto();
        responseDto.setReservationId(data.getReservationId());
        responseDto.setStartDate(data.getStartDate());
        responseDto.setEndDate(data.getEndDate());
        responseDto.setPrice(data.getPrice());
        responseDto.setPayStatus(data.getPayStatus());
        responseDto.setStatus(data.getStatus());
        responseDto.setReserveTime(data.getReserveTime().toString().substring(0, 10)
                                    +" "+data.getReserveTime().toString().substring(11, 16));
        reservationResponseDtos.add(responseDto);
    }
}
