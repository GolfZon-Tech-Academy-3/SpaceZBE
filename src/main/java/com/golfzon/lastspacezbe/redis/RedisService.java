package com.golfzon.lastspacezbe.redis;

import com.golfzon.lastspacezbe.reservation.dto.ReservationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public void redisString() {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set("test", "test");
        String redis = operations.get("test");
        log.info(redis);
    }

    // 키-벨류 설정
    public void setRefreshValues(String email, String refresh) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
//        values.set(name, age);
        values.set(email, refresh, Duration.ofDays(7));
    }

    // 키값으로 벨류 가져오기
    public String getRefreshValues(String email) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(email);
    }

    // 키-벨류 삭제
    public void delRefreshValues(String email) {
        redisTemplate.delete(email);
    }


    // 예약날짜 저장
    @Transactional
    public void checkReservation(ReservationRequestDto requestDto, List<String> checktimes) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();

        String key;
        List<String> keys = new ArrayList<>();
        String value;
        for (String checktime : checktimes) {
            key = requestDto.getSpaceId() + checktime;
            value = values.get(key);
            if (value != null) {
                log.info("이미 있습니다.");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 예약진행중입니다.");
            } else keys.add(key);
        }

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.watch(keys);
                operations.multi();
                for (String checktime : checktimes) {
                    String key = requestDto.getSpaceId() + checktime;
                    operations.opsForValue().setIfAbsent(key, "true", Duration.ofMinutes(5));
                    log.info("저장:{}", key);
                }
                Object obj;
                obj = operations.exec();
                log.info("obj:{}", obj);
                if(obj.toString().equals("[]")) throw new RuntimeException("이미 예약 진행중입니다.");
                return obj;
            }
        });
    }
}


