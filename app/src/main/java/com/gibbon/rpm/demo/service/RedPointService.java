package com.gibbon.rpm.demo.service;

import com.gibbon.rpm.annotation.ParentRedPoint;
import com.gibbon.rpm.annotation.RedPoint;
import com.gibbon.rpm.annotation.RedPointCanShowArrays;
import com.gibbon.rpm.annotation.RedPointNumArrays;
import com.gibbon.rpm.annotation.RedPointShowNum;
import com.gibbon.rpm.annotation.RelativeRedPoint;
import com.gibbon.rpm.demo.redpoint.RedPointConstance;

/**
 * @author zhipeng.zhuo
 * @date 2020-03-05
 */
public interface RedPointService {


    @RedPoint({
            RedPointConstance.ID_HOME,
            RedPointConstance.ID_VIDEO,
            RedPointConstance.ID_ME
    })
    void initHomeActivityRedPoints(@RedPointCanShowArrays boolean[] defaultShowArrays, @RedPointShowNum(RedPointConstance.ID_ME) boolean showNum);

    @RedPoint({
            RedPointConstance.ID_PACKAGE,
            RedPointConstance.ID_CARD,
            RedPointConstance.ID_SETTING
    })
    @ParentRedPoint(RedPointConstance.ID_ME)
    // 此处关联红点[key.value]格式的value一定是数组中的，key不在数组中，否则不生效
    @RelativeRedPoint({
            RedPointConstance.ID_ME_ACCOUNT + ":" + RedPointConstance.ID_SETTING,
    })
    void initMeRedPoints(@RedPointNumArrays int[] numArrays, @RedPointCanShowArrays boolean[] defaultShowArrays);
}
