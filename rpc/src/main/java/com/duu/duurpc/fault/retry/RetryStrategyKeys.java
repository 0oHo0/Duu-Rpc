package com.duu.duurpc.fault.retry;

/**
 * @description:
 * @author: duu
 * @date: 2024/3/27 21:17
 **/
public interface RetryStrategyKeys {

    /**
     * 不重试
     */
    String NO = "no";

    /**
     * 固定时间间隔
     */
    String FIXED_INTERVAL = "fixedInterval";

}