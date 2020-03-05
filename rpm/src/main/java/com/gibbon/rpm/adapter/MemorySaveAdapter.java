package com.gibbon.rpm.adapter;

import android.content.Context;

import java.util.HashMap;

/**
 * @author zhipeng.zhuo
 * @date 2020-03-03
 * 适用于网络请求下来的红点，每次可直接保存在内存中，退出重新进来又进行网络请求下来红点
 */
public class MemorySaveAdapter implements ISaveAdapter {

    public HashMap<String, Boolean> cache = new HashMap<>();

    @Override
    public void save(Context context, String id, boolean value) {
        cache.put(id, value);
    }

    @Override
    public boolean load(Context context, String id, boolean defaultValue) {
        Boolean result = cache.get(id);
        return result != null ? result : defaultValue;
    }
}
