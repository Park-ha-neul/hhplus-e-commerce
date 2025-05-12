package kr.hhplus.be.server.support.redis;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class SpinLockService {

    private final RedissonClient redissonClient;

    public SpinLockService(RedissonClient redissonClient){
        this.redissonClient = redissonClient;
    }

    public boolean trySpinLock(String key, String value, Duration ttl, Duration timeout, Duration retryInterval) {
        RLock rLock = redissonClient.getLock(key);
        long endTime = System.currentTimeMillis() + timeout.toMillis();

        while (System.currentTimeMillis() < endTime) {
            try {
                if (rLock.tryLock()) {
                    return true; // 락 획득 성공
                }
                Thread.sleep(retryInterval.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false; // 락 획득 실패
    }

    public void releaseLock(String key) {
        RLock rLock = redissonClient.getLock(key);
        if(rLock.isHeldByCurrentThread()){
            rLock.unlock();
        }
    }
}
