package com.golfzon.lastspacezbe.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.payment.dto.RefundDto;
import com.golfzon.lastspacezbe.reservation.dto.ReservationRequestDto;
import com.golfzon.lastspacezbe.reservation.entity.Reservation;
import com.golfzon.lastspacezbe.reservation.repository.ReservationRepository;
import com.golfzon.lastspacezbe.reservation.service.ReservationService;
import com.golfzon.lastspacezbe.space.entity.Space;
import com.golfzon.lastspacezbe.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final SpaceRepository spaceRepository;
    private final ReservationRepository reservationRepository;

    @Value("${import.imp_key}")
    private String imp_key;

    @Value("${import.imp_secret}")
    private String imp_secret;

    // import에서 accesstoken 생성하여 받아오기.
    public String getAccessToken() {
        log.info("imp_key: {}", imp_key);
        log.info("imp_secret: {}", imp_secret);

        RestTemplate rt = new RestTemplate();
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Request body 생성
        JSONObject body = new JSONObject();
        body.put("imp_key", imp_key);
        body.put("imp_secret", imp_secret);

        // HTTP 요청 보내기
        HttpEntity<JSONObject> entity = new HttpEntity<>(body, headers);
        ResponseEntity<JSONObject> token = rt.postForEntity("https://api.iamport.kr/users/getToken", entity, JSONObject.class);

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = Objects.requireNonNull(token.getBody()).toString().replace("token:", "");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (jsonNode == null) {
            throw new NullPointerException("jsonNode가 null입니다.");
        }
        log.info("token:{}", jsonNode.get("response").get("access_token").asText());
        return jsonNode.get("response").get("access_token").asText();
    }

    // imp_uid로 아임포트 서버에서 결제 및 결제 정보 조회
    public String getPaymentInfo(ReservationRequestDto vo) {
        String token = getAccessToken();

        RestTemplate rt = new RestTemplate();
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);


        // HTTP 요청 보내기
        HttpEntity<JSONObject> entity = new HttpEntity<>(headers);
        ResponseEntity<JSONObject> response = rt.postForEntity("https://api.iamport.kr/payments/" + vo.getImpUid(), entity, JSONObject.class);

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = Objects.requireNonNull(response.getBody()).toString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (jsonNode == null) {
            throw new NullPointerException("jsonNode가 null입니다.");
        }
        log.info("price:{}", jsonNode.get("response").get("amount").asText());
        if(vo.getPrepay().equals("003")) {
            return jsonNode.get("response").get("status").asText();
        } else return jsonNode.get("response").get("amount").asText().split("\\.")[0];
    }

    // 보증금 결제하기
    public int depositOK(ReservationRequestDto vo) {
        int flag = 0;

        // 계산되어야 할 값과 실제 계산된 값이 맞는지 확인.
        int price = Integer.parseInt( getPaymentInfo(vo));
        log.info("실제 계산된 돈: {}", price);
        Optional<Space> vo2 = spaceRepository.findById(vo.getSpaceId());
        if (vo2.isPresent()) {
            int price2 = vo2.get().getPrice() * getReserveTime(vo.getStartDate(), vo.getEndDate());
            int depositPrice = (int) (price2 * 0.2);
            log.info("price2:{}",price2);
            log.info("계산되어야할 depositPrice: {}", depositPrice);
            if (price == depositPrice) {
                vo.setPrice(price2 - depositPrice - vo.getMileage());
                log.info("예약될 가격:{}",vo.getPrice());
                reserve(vo);
                flag = 1;
            }
        }
        return flag;
    }

    // 결제정보와 DB 정보와 일치여부 확인
    public int verifyPayInfo(ReservationRequestDto vo) {
        int flag = 1;

        // 실제 결제된 가격 확인(인증토큰 발급)
        int price = Integer.parseInt(getPaymentInfo(vo));
        log.info("실제 계산된 돈: {}", price);
        // 계산되어야할 가격 확인
        Optional<Space> vo2 = spaceRepository.findById(vo.getSpaceId());
        if (vo2.isPresent()) {
            int price2 = vo2.get().getPrice() * getReserveTime(vo.getStartDate(), vo.getEndDate()) - vo.getMileage();
            log.info("계산되어야할 돈: {}", price2);
            // 가격 비교
            if (price != price2) {
                // 일치하지 않을 시, 오류로 계산된 금액 환불처리
                refund(new RefundDto(vo.getPrepayUid(), "계산된 금액과 일치하지 않습니다.", price, vo.getMemberId()));
                flag = 0;
            } else{
                vo.setPrice(price);
            }
        }
        return flag;
    }

    // 후불결제 예약
    public int reserve(ReservationRequestDto vo) {
        int flag = 0;
        String token = getAccessToken();
        String merchant_uid = getRanStr();
        vo.setPostpayUid(merchant_uid);
        RestTemplate rt = new RestTemplate();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        // HTTP Body 생성
        Map<String, Object> map = new HashMap<>();
        map.put("amount", vo.getPrice());
        map.put("schedule_at", getUnixTime(vo.getEndDate()));
        map.put("merchant_uid", merchant_uid);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(map);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("schedules",jsonArray);
        jsonObject.put("customer_uid", vo.getMemberId());
        log.info("jsonObject:{}", jsonObject);

        // HTTP 요청 보내기
        HttpEntity<JSONObject> entity = new HttpEntity<>(jsonObject, headers);
        ResponseEntity<JSONObject> response = rt.postForEntity("https://api.iamport.kr/subscribe/payments/schedule", entity, JSONObject.class);

        log.info("response:{}", response);
        log.info("response:{}", response.getBody());
        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = Objects.requireNonNull(response.getBody()).toString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (jsonNode == null) {
            throw new NullPointerException("jsonNode가 null입니다.");
        }
        log.info(responseBody);
        log.info("code:{}", jsonNode.get("code").asText());
        if (jsonNode.get("code").asText().equals("0")) {
            log.info("response:{}",jsonNode.get("response"));
            String price = jsonNode.get("response").get(0).get("amount").asText();
            log.info("amount:{}", price);
            flag = 1;
        }
        return flag;
    }

    // 예약 취소 시, 환불
    public int refund(RefundDto vo) {
        int flag = 0;
        log.info("vo:{}", vo);

        String token = getAccessToken();
        String url;

        RestTemplate rt = new RestTemplate();
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);


        // HTTP 요청 보내기
        // response date
        JSONObject body = new JSONObject();
        if (vo.getCancel_request_amount() == 0) {
            body.put("customer_uid", String.valueOf(vo.getMemberId())); // 가맹점 클라이언트로부터 받은 환불사유
            body.put("merchant_uid", vo.getMerchant_uid()); // imp_uid를 환불 `unique key`로 입력
            url = "https://api.iamport.kr/subscribe/payments/unschedule";
        } else {
            body.put("reason", vo.getReason()); // 가맹점 클라이언트로부터 받은 환불사유
            body.put("merchant_uid", vo.getMerchant_uid()); // imp_uid를 환불 `unique key`로 입력
            body.put("amount", String.valueOf(vo.getCancel_request_amount())); // 가맹점 클라이언트로부터 받은 환불금액
            url = "https://api.iamport.kr/payments/cancel";
//			json.addProperty("checksum", vo.getCancel_request_amount()); // [권장] 환불 가능 금액 입력
        }

        HttpEntity<JSONObject> entity = new HttpEntity<>(body, headers);
        ResponseEntity<JSONObject> response = rt.postForEntity(url, entity, JSONObject.class);

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = Objects.requireNonNull(response.getBody()).toString();
        log.info("responseBody:{}",responseBody);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (jsonNode == null) {
            throw new NullPointerException("jsonNode가 null입니다.");
        }
        log.info("code:{}", jsonNode.get("code").asText());
        if (jsonNode.get("code").asText().equals("0")) {
            log.info("response:{}",jsonNode.get("response"));
            flag = 1;
        }
        return flag;
    }


    // 결제 시도 시각 in Unix Time Stamp.
    public long getUnixTime(String endDate) {
        long unixTime = 0;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            Date date = formatter.parse(endDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            unixTime = c.getTimeInMillis() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        log.info("unixTime:{}", unixTime);

        return unixTime;
    }

    // 예약된 총 시간 계산
    public int getReserveTime(String startDate, String endDate) {
        int time = 0;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            Date date1 = formatter.parse(startDate);
            Date date2 = formatter.parse(endDate);
            time = (int) ((date2.getTime() - date1.getTime()) / 3600000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        log.info("time:{}hr",time);
        return time;
    }

    // random 주문번호 생성
    public String getRanStr() {
        // 주문번호 고유값 설정 위해, 난수생성 -> 이것은 공간 등록 시 생성되어 추가되어야 한다.
        char random_alphabet = (char) ((Math.random() * 26) + 97);
        // merchant_uid : 고유값 -> 프론트에서 받아서 결제완료 버튼 클릭 시, VO Data로 다시 넘어와야 함.
        String merchant_uid = String.valueOf(random_alphabet) + System.currentTimeMillis();
        log.info("merchantid:{}", merchant_uid);
        return merchant_uid;
    }


    public String getMerchantUid() {
        // 주문번호 고유값 설정 위해, 난수생성 -> 이것은 공간 예약 시 생성되어 추가되어야 한다.
        char random_alphabet = (char) ((Math.random() * 26) + 97);
        // merchant_uid : 고유값 -> 프론트에서 받아서 결제완료 버튼 클릭 시, VO Data로 다시 넘어와야 함.
        return String.valueOf(random_alphabet) + System.currentTimeMillis();
    }

    public String changeStatus(HashMap<String, Object> map) {
        log.info("map:{}",map);
        String status = getPaymentInfo(new ReservationRequestDto("003", (String) map.get("imp_uid"), (String) map.get("merchant_uid")));
        log.info("status:{}",status);
        if(status.equals("paid")){
            Reservation reservation = reservationRepository.findByImpUid((String) map.get("imp_uid"));
            reservation.setPayStatus("002");
            reservationRepository.save(reservation);
            return "result : 예약결제 완료";
        } else{
            return "result : 예약결제 실패";
        }
    }
}
