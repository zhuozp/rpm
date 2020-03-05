package com.gibbon.rpm.model;

import java.util.HashSet;

/**
 * 红点相关管理类
 *
 * @author zhuozhipeng
 */
public class RedPointData {
    // 红点对应id名称
    public String id;
    // 是否需要展示红点数字
    public boolean isShowNum;
    // 红点展示数字
    public int num = -1;
    // 父页面红点id名称
    public String parentRedPointId;
    // 子页面的红点id名称集合
    public HashSet<String> subPageRedPointIds;
    // 关联红点id名称集合
    public HashSet<String> relativeRedPointIds;
    // 同级红点数组指针
    public String[] sameClassRedPointIdsArrays;
    // 所在数组索引
    public int index = -1;
    // 本质能展示与否，与配置文件相关
    public boolean canShow;
    // 实际展示与否, 涉及红点展示个数策略影响
    public boolean reallyShow;
    // 消息类型
    public int crashType;
}
