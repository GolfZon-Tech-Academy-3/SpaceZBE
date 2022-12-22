package com.golfzon.lastspacezbe.reservation.service;

import com.golfzon.lastspacezbe.reservation.dto.ReservationRequestDto;
import com.golfzon.lastspacezbe.reservation.dto.ReservationResponseDto;
import com.golfzon.lastspacezbe.reservation.entity.Reservation;
import com.golfzon.lastspacezbe.reservation.repository.ReservationRepository;
import com.golfzon.lastspacezbe.space.entity.Space;
import com.golfzon.lastspacezbe.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

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

    // 금일 취소
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

        List<Reservation> reservations =  reservationRepository.findAllByCompanyIdOrderByStartDateDesc(companyId);
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

    // 기간별 예약 금액, 예약 현황 조회
    public Map<String, Object> totalIncomes(Long companyId, ReservationRequestDto requestDto) {
        log.info("dto:{}",requestDto);
        List<Reservation> reservations =  reservationRepository.findReservations(companyId);
        log.info("reservation:{}",reservations);
        log.info("reservation:{}",reservations.size());
        List<ReservationResponseDto> reservationResponseDtos = new ArrayList<>();
        int totalIncome = 0;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        Calendar rStartCal = Calendar.getInstance();
        Calendar rEndCal = Calendar.getInstance();
        try {
            Date startDate = formatter.parse(requestDto.getStartDate());
            startCal.setTime(startDate);
            startCal.add(Calendar.DATE, -1);
            Date endDate = formatter.parse(requestDto.getEndDate());
            endCal.setTime(endDate);
            endCal.add(Calendar.DATE, +1);
            for (Reservation reservation:reservations) {
                Date reservSdate = formatter.parse(reservation.getStartDate());
                rStartCal.setTime(reservSdate);
                Date reservEdate = formatter.parse(reservation.getEndDate());
                rEndCal.setTime(reservEdate);
                if((rStartCal.after(startCal) && rStartCal.before(endCal)) || (rEndCal.after(startCal) && rEndCal.before(endCal))){
                    String type = ""; // 공간 타입
                    String spaceName = ""; // 공간 이름

                    // space 공간 타입과 공간 이름 찾기
                    List<Space> spaces = spaceRepository.findAllByCompanyId(companyId);
                    for (Space space: spaces
                    ) {
                        if(space.getSpaceId().equals(reservation.getSpaceId())){
                            type = space.getType();
                            spaceName = space.getSpaceName();
                        }
                    }

                    ReservationResponseDto responseDto = new ReservationResponseDto();
                    responseDto.setReservationId(reservation.getReservationId());
                    responseDto.setReservationName(reservation.getReservationName());
                    responseDto.setStartDate(reservation.getStartDate());
                    responseDto.setEndDate(reservation.getEndDate());
                    responseDto.setPrice(reservation.getPrice());
                    responseDto.setStatus(reservation.getStatus()); // 예약 상태
                    responseDto.setType(type); // 공간타입
                    responseDto.setSpaceName(spaceName);

                    reservationResponseDtos.add(responseDto);
                    totalIncome+= responseDto.getPrice();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Map<String, Object> map = new HashMap<>();
        map.put("totalIncome", totalIncome);
        map.put("reservations",reservationResponseDtos);
        return map;
    }
}
