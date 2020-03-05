#### 介绍
红点管理框架、提供基本的红点控件，基本的红点管理策略、以及红点读取和保存，用户开发的时候可适配自定义红点控件、以及管理和读取红点数据策略

![示例](https://github.com/zhuozp/RPM/blob/master/images/device-2020-03-05-231839.gif)

#### 用法如下：

1. 定义Service
```
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
```

2. 初始化

2.1. 使用注解的形式初始化
```
   // 生成RPM实例
   rpm = new RPM.Builder(this)
                .build();
   // 得到注解类实例
   RedPointService redPointService = rpm.create(RedPointService.class);   
   
   // 注册红点观察者
   for (String id : arrays) {
       rpm.register(id, this);
   }  
   
   // 初始化红点
   redPointService.initHomeActivityRedPoints(new boolean[]{true, true, true}, true);
   
```

2.2. 直接调用方法的形式初始化
```
   // 生成RPM实例
   rpm = new RPM.Builder(this)
                .build();
   
   // 注册红点观察者
   for (String id : arrays) {
       rpm.register(id, this);
   }  
   
   // 初始化红点
   rpm.initRedPoints(new int[]{xxx, xxx, xxx}, null, null, new boolean[]{true, true, true});
```

3. 观察红点更新
```
   @Override
    public void notifyRedPointChange(String redPointId, boolean show, int showNum) {
        // 红点展示view的更新
        int index = -1;
        for (int i = 0; i < arrays.length; i++) {
            if (arrays[i].equals(redPointId)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            tabGroupView.setRedPointAtPosition(index, show);
        }
    }
```

4. 操作红点状态
```
      RedPointData redPointData = rpm.getRedPointData(id);
      if (redPointData != null) {
            // 有操作，保存红点展示与否
            rpm.saveRedPointsChange(parentId, false);
      }
```

#### 欢迎交流
部分红点场景可能还不能一一兼顾到，欢迎PR或者提出需求
