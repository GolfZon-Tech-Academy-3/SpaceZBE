package com.golfzon.lastspacezbe.reservation.service;

import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.reservation.dto.ReservationRequestDto;
import com.golfzon.lastspacezbe.reservation.dto.ReservationResponseDto;
import com.golfzon.lastspacezbe.reservation.entity.Reservation;
import com.golfzon.lastspacezbe.reservation.repository.ReservationRepository;
import com.golfzon.lastspacezbe.space.entity.Space;
import com.golfzon.lastspacezbe.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReservBackOfficeService {

    private final ReservationRepository reservationRepository;
    private final SpaceRepository spaceRepository;

    // 금일 업체 예약 수
    public int todayReserve(Long companyId) {
        int reserveCount = 0; // 금일 예약 수
        // 업체 번호로 예약 가져오기.
        List<Reservation> reservations = reservationRepository.findAllByCompanyId(companyId);
        // 빈 리스트
        List<Reservation> reservationList = new ArrayList<>();

        LocalDateTime currentDateTime = LocalDateTime.now(); // 오늘 날짜
        String today = currentDateTime.toString().substring(0,10);
        log.info("today substring : {}", today);

        for (Reservation data: reservations
             ) {

            // 예약 시작 날짜가 오늘 날짜와 같은지
            if(data.getStartDate().contains(today) && data.getStatus().equals("001")){
            log.info("data : {}", data.getStartDate());
                reservationList.add(data);
            }
        }
        // 카운트
        reserveCount = reservationList.size();

        return reserveCount;
    }

    // 금일 예약
    public int todayCancel(Long companyId) {

        log.info("companyId : {}", companyId);
        int cancelCount = 0;
        // 업체 번호로 예약 가져오기.
        List<Reservation> reservations = reservationRepository.findAllByCompanyId(companyId);
        // 빈 리스트
        List<Reservation> reservationList = new ArrayList<>();

        LocalDateTime currentDateTime = LocalDateTime.now(); // 오늘 날짜
        String today = currentDateTime.toString().substring(0,10);
        log.info("today substring : {}", today);
        log.info("reservations : {}", reservations.get(0).toString());

        for (Reservation data: reservations
        ) {
            log.info("data : {}", data);
            // 예약 시작 날짜가 오늘 날짜와 같고 status 가 002 상태일 때
            if(data.getStartDate().contains(today) && data.getStatus().equals("002")){
                log.info("data startdate : {}", data.getStartDate());
                log.info("data status : {}", data.getStatus());
                reservationList.add(data);
            }
        }
        
        cancelCount = reservationList.size();

        return cancelCount;
    }

    // 예약 전체 조회
    public List<ReservationResponseDto> totalReserveSelectAll(Long companyId) {

        List<Reservation> reservations =  reservationRepository.findAllByCompanyId(companyId);
        List<ReservationResponseDto> reservationResponseDtos = new ArrayList<>();

        for (Reservation data :reservations
             ) {
            String type = ""; // 공간 타입
            String spaceName = ""; // 공간 이름

            // space 공간 타입과 공간 이름 찾기
            List<Space> spaces = spaceRepository.findAllByCompanyId(companyId);
            for (Space space: spaces
                 ) {
                if(space.getSpaceId().equals(data.getSpaceId())){
                    type = space.getType();
                    spaceName = space.getSpaceName();
                }
            }

            ReservationResponseDto responseDto = new ReservationResponseDto();
            responseDto.setReservationId(data.getReservationId());
            responseDto.setReservationName(data.getReservationName());
            responseDto.setStartDate(data.getStartDate());
            responseDto.setEndDate(data.getEndDate());
            responseDto.setPrice(data.getPrice());
            responseDto.setStatus(data.getStatus()); // 예약 상태
            responseDto.setType(type); // 공간타입
            responseDto.setSpaceName(spaceName);

            reservationResponseDtos.add(responseDto);
        }

        return reservationResponseDtos;
    }
}
