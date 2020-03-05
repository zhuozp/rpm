package com.gibbon.rpm.adapter;

/**
 * @author zhipeng.zhuo
 * @date 2020-02-29
 */
public class DefaultRedPointStrategyAdapter implements IRedPointStrategyAdapter {

    // 同个界面允许同时展示的红点个数
    public static final int MAX_SAME_SHOW_COUNT = 2;

    @Override
    public int sameMaxRedPointShow() {
        return MAX_SAME_SHOW_COUNT;
    }
}
