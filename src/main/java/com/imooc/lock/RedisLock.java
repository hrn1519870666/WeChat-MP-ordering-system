package com.imooc.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Redis分布式锁
 */
@Component
@Slf4j
public class RedisLock {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 加锁，使用setnx命令
     * @param key
     * @param value  当前时间+超时时间
     * @return
     */
    public boolean lock(String key,String value){
        //加锁成功
        if(redisTemplate.opsForValue().setIfAbsent(key,value)){
            return true;
        }

        //解决因未执行解锁操作导致的死锁问题——通过锁超时解决，并且实现多个线程到来，只会有一个线程获取锁
        //currentValue=A,新来的两个线程A和B的value都是B，则只会有其中一个线程拿到锁
        String currentValue = redisTemplate.opsForValue().get(key);
        //如果锁过期
        if(!StringUtils.isEmpty(currentValue)
                &&Long.parseLong(currentValue) < System.currentTimeMillis()){
            //获取上一个锁的时间
            String oldValue = redisTemplate.opsForValue().getAndSet(key,value);
            if(!StringUtils.isEmpty(oldValue)
                    &&oldValue.equals(currentValue)){
                return true;
            }
        }

        //加锁失败
        return false;
    }

    /**
     * 解锁
     * @param key
     * @param value
     */
    public void unlock(String key,String value){
        try {
            String currentValue = redisTemplate.opsForValue().get(key);
            if(!StringUtils.isEmpty(currentValue)
                    && currentValue.equals(value)){
                //从redis中删除key
                redisTemplate.opsForValue().getOperations().delete(key);
            }
        }catch (Exception e){
            log.error(" [redis分布式锁] 解锁异常，e={}",e);
        }
    }
}
