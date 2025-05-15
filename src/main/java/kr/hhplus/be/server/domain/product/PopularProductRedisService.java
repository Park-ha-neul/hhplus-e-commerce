package kr.hhplus.be.server.domain.product;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class PopularProductRedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public PopularProductRedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String getDailyZSetKey(PopularProduct.PeriodType periodType, String date) {
        return "popular:products:" + periodType.name() + ":" + date;
    }

    public List<String> getDailyKeysForPeriod(PopularProduct.PeriodType periodType, LocalDate now) {
        List<String> keys = new ArrayList<>();

        if (periodType == PopularProduct.PeriodType.WEEKLY) {
            LocalDate startOfWeek = now.with(DayOfWeek.MONDAY);
            for (int i = 0; i < 7; i++) {
                LocalDate date = startOfWeek.plusDays(i);
                keys.add(getPeriodKey(PopularProduct.PeriodType.DAILY, date));
            }
        } else if (periodType == PopularProduct.PeriodType.MONTHLY) {
            LocalDate startOfMonth = now.withDayOfMonth(1);
            LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
            for (LocalDate date = startOfMonth; !date.isAfter(endOfMonth); date = date.plusDays(1)) {
                keys.add(getPeriodKey(PopularProduct.PeriodType.DAILY, date));
            }
        }

        return keys;
    }

    private LocalDate getBaseDateByPeriodType(PopularProduct.PeriodType periodType, LocalDate now) {
        switch (periodType) {
            case WEEKLY:
                return now.with(DayOfWeek.MONDAY);
            case MONTHLY:
                return now.withDayOfMonth(1);
            default:
                return now;
        }
    }

    String getPeriodKey(PopularProduct.PeriodType periodType, LocalDate now){
        LocalDate baseDate = getBaseDateByPeriodType(periodType, now);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        switch (periodType) {
            case DAILY:
                return "popular:products:" + now.format(formatter);
            case WEEKLY, MONTHLY:
                return getZSetKeyForUnion(periodType, baseDate);
            default:
                throw new IllegalArgumentException("Unkown period type");
        }
    }

    String getZSetKeyForUnion(PopularProduct.PeriodType periodType, LocalDate now) {
        LocalDate baseDate = getBaseDateByPeriodType(periodType, now);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        if (periodType == PopularProduct.PeriodType.WEEKLY) {
            return getDailyZSetKey(periodType, baseDate.format(formatter));
        } else if (periodType == PopularProduct.PeriodType.MONTHLY) {
            return getDailyZSetKey(periodType, baseDate.format(formatter));
        }
        return "";
    }

    public Duration getTTLByPeriodType(PopularProduct.PeriodType periodType) {
        switch (periodType) {
            case DAILY:
                return Duration.ofDays(1);
            case WEEKLY:
                return Duration.ofDays(7);
            case MONTHLY:
                return Duration.ofDays(30);
            default:
                throw new IllegalArgumentException("Invalid period type: " + periodType);
        }
    }

    public Set<ZSetOperations.TypedTuple<Object>> getZSetData(String zsetKey) {
        return redisTemplate.opsForZSet().rangeWithScores(zsetKey, 0, -1);
    }
}
