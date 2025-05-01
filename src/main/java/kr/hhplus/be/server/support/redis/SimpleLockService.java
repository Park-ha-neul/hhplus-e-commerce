package kr.hhplus.be.server.support.redis;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class SimpleLockService {

    private final RedissonClient redissonClient;

    public SimpleLockService(RedissonClient redissonClient){
        this.redissonClient = redissonClient;
    }

    public boolean trySimpleLock(String key, Duration ttl){
        RLock lock = redissonClient.getLock(key);
        try{
            return lock.tryLock(0, ttl.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void releaseLock(String key) {
        RLock lock = redissonClient.getLock(key);
        if(lock.isHeldByCurrentThread()){
            lock.unlock();
        }
    }
}
