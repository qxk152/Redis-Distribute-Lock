package com.qxk;

import com.qxk.RedisLock.SimpleRedisLock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * TODO
 * <p>
 *  @author 86080
 *  @date 2024/01/22 15:54
 *  @version 1.0
 */
@SpringBootTest
public class LockTest {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Test
    public void testSimpleLock(){




        Thread t1 = new Thread(()->{
            SimpleRedisLock test = new SimpleRedisLock(stringRedisTemplate,"test");
            while(!test.tryLock(10)){

                try {
                    System.out.println("t1获取锁失败");
                    Thread.sleep(1000 * 5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
            try {
                long id = Thread.currentThread().getId();
                System.out.println("thread t1 获取锁 线程id = " + id);
            }finally {
                try {
                    Thread.sleep(1000 * 5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                test.unLock();

            }


        });

        Thread t2 = new Thread(()->{
            SimpleRedisLock test = new SimpleRedisLock(stringRedisTemplate,"test");
            while(!test.tryLock(10)){

                try {
                    System.out.println("t2获取锁失败");
                    Thread.sleep(1000 * 5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
            try {
                long id = Thread.currentThread().getId();
                System.out.println("thread t2 获取锁 线程id = " + id);
            }finally {

                test.unLock();

            }

        });

        try {


            t1.start();
            t2.start();
            Thread.sleep(1000 * 60 *60);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
