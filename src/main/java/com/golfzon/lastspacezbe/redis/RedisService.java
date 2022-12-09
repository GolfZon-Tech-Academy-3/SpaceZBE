package com.golfzon.lastspacezbe.redis;

import com.golfzon.lastspacezbe.reservation.dto.ReservationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
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
    public void setRefreshValues(String email, String refresh){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
//        values.set(name, age);
        values.set(email, refresh,Duration.ofDays(7));
    }

    // 키값으로 벨류 가져오기
    public String getRefreshValues(String email){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(email);
    }

    // 키-벨류 삭제
    public void delRefreshValues(String email) {
        redisTemplate.delete(email);
    }

    // 키-벨류 설정
    public void setValues(String id, String pwd){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(id, pwd, Duration.ofDays(1));
    }

    // 키값으로 벨류 가져오기
    public String getValues(String id){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(id);
    }

    // 키-벨류 삭제
    public void delValues(String username) {
        redisTemplate.delete(username);
    }

    public void checkReservation(ReservationRequestDto requestDto, List<String> checktimes) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi(); // transaction start
                Boolean result;
                for (String checktime:checktimes) {
                    result = operations.opsForValue().setIfAbsent(requestDto.getSpaceId()+checktime,true, Duration.ofMinutes(5));
                    if (Boolean.FALSE.equals(result)) {
                        throw new RuntimeException("exception");
                    }
                }
                return operations.exec(); // transaction end
            }
        });
    }
}
