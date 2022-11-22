package com.golfzon.lastspacezbe.mileage.service;

import com.golfzon.lastspacezbe.member.repository.MemberRepository;
import com.golfzon.lastspacezbe.mileage.dto.ProfileDTO;
import com.golfzon.lastspacezbe.mileage.entity.Mileage;
import com.golfzon.lastspacezbe.mileage.repository.MileageRepository;
import com.golfzon.lastspacezbe.reservation.dto.ReservationRequestDto;
import com.golfzon.lastspacezbe.space.entity.Space;
import com.golfzon.lastspacezbe.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MileageService{

	private final SpaceRepository spaceRepository;
	private final MileageRepository mileageRepository;

	// 마일리지 적립
	public void insertMileage(ReservationRequestDto vo) {
		log.info("insertMileage");
		log.info("vo:{}", vo);
		int flag = 0;

		Space space = spaceRepository.findById(vo.getSpaceId())
				.orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 spaceId는 존재하지 않습니다."));
		log.info("space:{}", space);

		Mileage mileage = new Mileage();
		mileage.setMemberId(vo.getMemberId());
		mileage.setSpaceName(space.getSpaceName());
		mileage.setScore((int) ((vo.getPrice()+vo.getMileage()) * 0.05));
		mileage.setSpaceId(vo.getSpaceId());
		mileage.setStatus("적립");
		log.info("mileage:{}", mileage);

		mileageRepository.save(mileage);
	}

	// 마일리지 사용
	
	public void updateMileage(ReservationRequestDto vo) {
		log.info("updateMileage");
		log.info("vo:{}", vo);

		Space space = spaceRepository.findById(vo.getSpaceId())
				.orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 spaceId는 존재하지 않습니다."));
		log.info("space:{}", space);

		Mileage mileage = new Mileage();
		mileage.setMemberId(vo.getMemberId());
		mileage.setSpaceName(space.getSpaceName());
		mileage.setScore(vo.getMileage() * -1);
		mileage.setSpaceId(vo.getSpaceId());
		mileage.setStatus("사용");
		log.info("mileage:{}", mileage);

		mileageRepository.save(mileage);
	}

	// 마일리지 취소
	
	public void cancelMileage(ReservationRequestDto vo) {
		log.info("deleteMileage");
		log.info("vo:{}", vo);

		Space space = spaceRepository.findById(vo.getSpaceId())
				.orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 spaceId는 존재하지 않습니다."));
		log.info("space:{}", space);

		Mileage mileage = new Mileage();
		mileage.setMemberId(vo.getMemberId());
		mileage.setSpaceName(space.getSpaceName());
		mileage.setScore((int) (vo.getPrice() * 0.05) * -1);
		mileage.setSpaceId(vo.getSpaceId());
		mileage.setStatus("취소");
		log.info("mileage:{}", mileage);

		mileageRepository.save(mileage);
	}

	// 사용자 프로필 조회 - memberId 만 알면 됨.
	
	public ProfileDTO selectAll(Long memberId) {
		log.info("selectAll");
		log.info("memberId:{}", memberId);

		ProfileDTO dto = new ProfileDTO();
		
//		Member member = memberRepository.findById(memberId)
//				.orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 memberId는 존재하지 않습니다."));
//		dto.setEmail(vo.getEmail());
//		dto.setImgname(vo.getImgname());
//		dto.setMembername(vo.getMembername());
		
		return dto;
	}

	// 사용자 마일리지 조회
	public int getTotalScore(Long memberId) {
		log.info("getTotal_score");
		log.info("memberId:{}", memberId);

		List<Mileage> mileages = mileageRepository.findAllByMemberId(memberId);
		log.info("mileages.size:{}", mileages.size());

		int totalScore = 0;
		for (Mileage mileage : mileages) {
			totalScore += mileage.getScore();
		}
		log.info("totalScore:{}", totalScore);

		return totalScore;
	}

	// 사용한 마일리지 환급
	public void refundMileage(ReservationRequestDto vo) {
		log.info("refundMileage");
		log.info("vo:{}", vo);
		List<Mileage> mileages = mileageRepository.findAllBySpaceId(vo.getSpaceId());
		
		int flag = 0;
		
		if(mileages.size()>0) {
			for (Mileage mileage : mileages) {
				if(mileage.getStatus().equals("사용")) {
					Mileage vo2 = new Mileage();
					vo2.setMemberId(vo.getMemberId());
					vo2.setSpaceName(mileage.getSpaceName());
					vo2.setScore(mileage.getScore()*-1);
					vo2.setSpaceId(vo.getSpaceId());
					vo2.setStatus("환급");
					log.info("mileageVO:{}", vo2);

					mileageRepository.save(vo2);
					log.info("마일리지 환급 완료");
				}
			}
		}
	}

}
