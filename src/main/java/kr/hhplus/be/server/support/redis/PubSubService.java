package kr.hhplus.be.server.support.redis;

import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class PubSubService {

    private final RedissonClient redissonClient;

    public PubSubService(RedissonClient redissonClient){
        this.redissonClient = redissonClient;
    }

    public void subscribe(String topicName, MessageListener<String> listener){
        RTopic topic = redissonClient.getTopic(topicName);
        topic.addListener(String.class, listener);
    }

    public void publish(String topicName, String message){
        RTopic topic = redissonClient.getTopic(topicName);
        topic.publish(message);
    }
}
