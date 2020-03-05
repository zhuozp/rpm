package com.gibbon.rpm.adapter;

import android.content.Context;

/**
 * @author zhipeng.zhuo
 * @date 2020-03-01
 */
public interface ISaveAdapter {

    void save(Context context, String id, boolean value);

    boolean load(Context context, String id, boolean defaultValue);
}
