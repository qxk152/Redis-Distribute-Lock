package com.qxk.RedisLock;

import cn.hutool.core.lang.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 * <p>
 *  @author 86080
 *  @date 2024/01/22 15:08
 *  @version 1.0
 */
public class SimpleRedisLock implements ILock{
    StringRedisTemplate stringRedisTemplate ;
    String name;
    private static final String LOCK_PREFIX = "lock:";

    //返回值类型
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;
    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }
    //线程标识前缀 因为线程id可能一样
    private static final String ID_PREFIX = UUID.randomUUID().toString(true) + "-";

    // name 锁的名字


    public SimpleRedisLock(StringRedisTemplate stringRedisTemplate, String name) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.name = name;
    }



    @Override
    public boolean tryLock(long timeoutStamp) {
        String threadTag = ID_PREFIX + Thread.currentThread().getId();
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(LOCK_PREFIX + name, threadTag, timeoutStamp, TimeUnit.SECONDS);

        return Boolean.TRUE.equals(success);
    }

    /*@Override
    public void unLock() {
        //必须是删除当前线程持有的锁 需要先检查是不是本线程持有的锁
        String id = stringRedisTemplate.opsForValue().get(LOCK_PREFIX + name);
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        if(threadId.equals(id)){
            stringRedisTemplate.delete(LOCK_PREFIX + name);
            System.out.println(threadId + "=========释放锁==========");
        }
    }*/

    @Override
    public void unLock() {
        System.out.println(ID_PREFIX + Thread.currentThread().getId() + "=========释放锁==========");
        stringRedisTemplate.execute(UNLOCK_SCRIPT, Collections.singletonList(LOCK_PREFIX + name),ID_PREFIX + Thread.currentThread().getId());
    }
}
