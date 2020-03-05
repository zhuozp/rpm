package com.gibbon.rpm.adapter;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author zhipeng.zhuo
 * @date 2020-03-01
 * 默认保存在SP文件中，根据需要可定制
 */
public class DefaultSaveAdapter implements ISaveAdapter {

    // 保存各个红点id的preference名称
    private static final String RED_POINTS_PREFERENCE = "red_points_preference";

    @Override
    public void save(Context context, String id, boolean value) {
        SharedPreferences pref = context.getSharedPreferences(RED_POINTS_PREFERENCE, Context.MODE_PRIVATE);
        if (pref != null) {
            pref.edit().putBoolean(id, value).apply();
        }
    }

    @Override
    public boolean load(Context context, String id, boolean defaultValue) {
        SharedPreferences pref = context.getSharedPreferences(RED_POINTS_PREFERENCE, Context.MODE_PRIVATE);
        if (pref != null) {
            return pref.getBoolean(id, defaultValue);
        }
        return defaultValue;
    }
}
