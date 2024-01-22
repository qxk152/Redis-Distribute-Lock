package com.qxk.RedisLock;

/**
 * TODO
 * <p>
 *  @author 86080
 *  @date 2024/01/22 15:07
 *  @version 1.0
 */
public interface ILock {
    boolean tryLock(long timeoutStamp);

    void unLock();
}
