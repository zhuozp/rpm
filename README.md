#### 背景
日常需求中经常需要通过红点来加强某个功能、某个页面或某个入口的注意点。若你的应用中涉及tab 红点展示策略，红点数字展示、一级页面红点和二级页面红点展示策略、以及某个页面红点与另一个或另几个关联的红点展示策略等等，rpm红点管理框架基本做到满足红点的管理策略。rpm提供了默认的红点保存策略，默认保存在sp文件中，还提供了内存保存策略，接入者按需使用或者自定义保存策略，同样，rpm框架提供了红点展示策略，默认是同级页面所有红点都可以展示，接入者可以按需调整同一时间最多可以展示多少个红点。支持注解和方法调用两种方式提供更加灵活的使用。

#### 示例
![示例](https://github.com/zhuozp/RPM/blob/master/images/device-2020-03-05-231839.gif)

#### 接入步骤：
1. gradle配置

1.1 在root gradle中的repositories末尾添加远程仓库地址
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
1.2 添加rpm依赖
```
dependencies {
	        implementation 'com.github.zhuozp:rpm:v1.0.0'
	}
```

2. 定义Service
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

3. 初始化

3.1. 使用注解的形式初始化
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

3.2. 直接调用方法的形式初始化
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

4. 观察红点更新
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

5. 操作红点状态
```
      RedPointData redPointData = rpm.getRedPointData(id);
      if (redPointData != null) {
            // 有操作，保存红点展示与否
            rpm.saveRedPointsChange(parentId, false);
      }
```

#### 欢迎交流
部分红点场景可能还不能一一兼顾到，欢迎PR或者提出需求
