package com.golfzon.lastspacezbe.reservation.service;

import com.golfzon.lastspacezbe.company.entity.Company;
import com.golfzon.lastspacezbe.company.repository.CompanyRepository;
import com.golfzon.lastspacezbe.reservation.dto.ReservationResponseDto;
import com.golfzon.lastspacezbe.reservation.entity.Reservation;
import com.golfzon.lastspacezbe.reservation.repository.ReservationRepository;
import com.golfzon.lastspacezbe.review.dto.ReviewDto;
import com.golfzon.lastspacezbe.review.entity.Review;
import com.golfzon.lastspacezbe.review.repository.ReviewRepository;
import com.golfzon.lastspacezbe.space.entity.Space;
import com.golfzon.lastspacezbe.space.entity.SpaceImage;
import com.golfzon.lastspacezbe.space.repository.SpaceImageRepository;
import com.golfzon.lastspacezbe.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReservMypageService {

    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;
    private final SpaceRepository spaceRepository;
    private final SpaceImageRepository spaceImageRepository;
    private final CompanyRepository companyRepository;

    // 예약 이력
    public List<ReservationResponseDto> totalReserveSelectAll(Long memberId) {

        String type = ""; // 공간 타입
        String spaceName = ""; // 공간 이름

        List<ReservationResponseDto> reservationResponseDtos = new ArrayList<>();
        List<Reservation> reservations = reservationRepository.findAllByMemberId(memberId);
        for (Reservation data : reservations
        ) {
            // 예약 취소와 예약 완료 상태 일때 만
            if (data.getStatus().equals("002") || data.getStatus().equals("004")) {
                Space space = spaceRepository.findBySpaceId(data.getSpaceId());
                Company company = companyRepository.findByCompanyId(space.getCompanyId());

                List<SpaceImage> spaceImages = spaceImageRepository.findAllBySpace(space);
                type = space.getType();
                spaceName = space.getSpaceName();

                ReservationResponseDto responseDto = new ReservationResponseDto();
                responseDto.setReservationId(data.getReservationId());
                responseDto.setStartDate(data.getStartDate());
                responseDto.setEndDate(data.getEndDate());
                responseDto.setPrice(data.getPrice());
                responseDto.setPayStatus(data.getPayStatus());
                responseDto.setStatus(data.getStatus());
                responseDto.setReserveTime(data.getReserveTime().toString().substring(0, 10)
                        + " " + data.getReserveTime().toString().substring(11, 16));
                responseDto.setSpaceId(data.getSpaceId()); // 공간 번호
                responseDto.setCompanyId(data.getCompanyId()); // 업체 번호
                responseDto.setReservationName(data.getReservationName());// 예약자이름
                responseDto.setType(space.getType());// 공간 타입
                responseDto.setSpaceName(space.getSpaceName());// 공간 이름
                responseDto.setImageName(spaceImages.get(0).getSpaceImage());// 이미지 첫번째
                responseDto.setLocation(company.getLocation());// 장소 위치
                responseDto.setDetails(company.getDetails());// 장소 상세 위치

                Review review = reviewRepository.findByReservationIdAndMemberId(data.getReservationId(), memberId);
                if (review != null) {
                    responseDto.setReview(new ReviewDto(review));
                }
                reservationResponseDtos.add(responseDto);
            }
        }
        return reservationResponseDtos;
    }

    // 예약 현황
    public List<ReservationResponseDto> proceedReserveSelectAll(Long memberId) {

        String type = ""; // 공간 타입
        String spaceName = ""; // 공간 이름

        List<ReservationResponseDto> reservationResponseDtos = new ArrayList<>();
        List<Reservation> reservations = reservationRepository.findAllByMemberId(memberId);
        for (Reservation data : reservations
        ) {
            // 예약중일때만
            if (data.getStatus().equals("001")) {
                Space space = spaceRepository.findBySpaceId(data.getSpaceId());
                Company company = companyRepository.findByCompanyId(space.getCompanyId());

                List<SpaceImage> spaceImages = spaceImageRepository.findAllBySpace(space);
                type = space.getType();
                spaceName = space.getSpaceName();
                ReservationResponseDto responseDto = new ReservationResponseDto();

                responseDto.setReservationId(data.getReservationId());
                responseDto.setStartDate(data.getStartDate());
                responseDto.setEndDate(data.getEndDate());
                responseDto.setPrice(data.getPrice());
                responseDto.setPayStatus(data.getPayStatus());
                responseDto.setStatus(data.getStatus());
                responseDto.setReserveTime(data.getReserveTime().toString().substring(0, 10)
                        + " " + data.getReserveTime().toString().substring(11, 16));
                responseDto.setSpaceId(data.getSpaceId()); // 공간 번호
                responseDto.setCompanyId(data.getCompanyId()); // 업체 번호
                responseDto.setReservationName(data.getReservationName());// 예약자이름
                responseDto.setType(space.getType());// 공간 타입
                responseDto.setSpaceName(space.getSpaceName());// 공간 이름
                responseDto.setImageName(spaceImages.get(0).getSpaceImage());// 이미지 첫번째
                responseDto.setLocation(company.getLocation());// 장소 위치
                responseDto.setDetails(company.getDetails());// 장소 상세 위치

                reservationResponseDtos.add(responseDto);
            }
        }
        return reservationResponseDtos;
    }

}
