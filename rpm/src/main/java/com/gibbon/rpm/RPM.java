package com.gibbon.rpm;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.gibbon.rpm.adapter.DefaultPushAdapter;
import com.gibbon.rpm.adapter.DefaultRedPointStrategyAdapter;
import com.gibbon.rpm.adapter.DefaultSaveAdapter;
import com.gibbon.rpm.adapter.IPushAdapter;
import com.gibbon.rpm.adapter.IRedPointStrategyAdapter;
import com.gibbon.rpm.adapter.ISaveAdapter;
import com.gibbon.rpm.model.IRedPointsObserver;
import com.gibbon.rpm.model.RedPointData;
import com.gibbon.rpm.util.Utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zhipeng.zhuo
 * @date 2020-02-29
 */
public class RPM {
    private final Map<Method, ServiceMethod> serviceMethodCache = new LinkedHashMap<>();

    private Context context;
    private IPushAdapter pushAdapter;
    private IRedPointStrategyAdapter redPointStrategyAdapter;
    private ISaveAdapter saveAdapter;

    private RPM(Context context, IPushAdapter pushAdapter, IRedPointStrategyAdapter redPointStrategyAdapter, ISaveAdapter saveAdapter) {
        this.context = context;
        this.pushAdapter = pushAdapter;
        this.redPointStrategyAdapter = redPointStrategyAdapter;
        this.saveAdapter = saveAdapter;
    }

    /**
     * 可通过注解的形式进行相关红点初始化
     * */
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> service) {
        Utils.validateServiceInterface(service);

        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getDeclaringClass() == Object.class) {
                    return method.invoke(this, args);
                }
                ServiceMethod serviceMethod = loadServiceMethod(method, args);
                initRedPoints(serviceMethod);
                return serviceMethod;
            }
        });
    }

    /**
     * 可直接调用方法进行相关红点初始化
     * */
    public void initRedPoints(@NonNull String[] redPoints, String parentId) {
        this.initRedPoints(redPoints, parentId, null);
    }

    public void initRedPoints(@NonNull String[] redPoints, String parentId, HashMap<String, String> relativeRedPointIds) {
        this.initRedPoints(redPoints, parentId, relativeRedPointIds, null);
    }

    public void initRedPoints(@NonNull String[] redPoints, String parentId, HashMap<String, String> relativeRedPointIds, HashMap<String, Boolean> showNumMap) {
        this.initRedPoints(redPoints, parentId, relativeRedPointIds, showNumMap, null);
    }

    public void initRedPoints(@NonNull String[] redPoints, String parentId, HashMap<String, String> relativeRedPointIds,
                              HashMap<String, Boolean> showNumMap, boolean[] showNumsArrays) {
        this.initRedPoints(redPoints, parentId, relativeRedPointIds, showNumMap, showNumsArrays, null);
    }

    public void initRedPoints(@NonNull String[] redPoints, String parentId, HashMap<String, String> relativeRedPointIds,
                              HashMap<String, Boolean> showNumMap, boolean[] showNumsArrays, HashMap<String, Integer> numMap) {
        this.initRedPoints(redPoints, parentId, relativeRedPointIds, showNumMap, showNumsArrays, numMap, null);
    }

    public void initRedPoints(@NonNull String[] redPoints, String parentId, HashMap<String, String> relativeRedPointIds,
                              HashMap<String, Boolean> showNumMap, boolean[] showNumsArrays, HashMap<String, Integer> numMap,
                              int[] numsArrays) {
        this.initRedPoints(redPoints, parentId, relativeRedPointIds, showNumMap, showNumsArrays, numMap, numsArrays, null);
    }

    public void initRedPoints(@NonNull String[] redPoints, String parentId, HashMap<String, String> relativeRedPointIds,
                              HashMap<String, Boolean> showNumMap, boolean[] showNumsArrays, HashMap<String, Integer> numMap,
                              int[] numsArrays,  HashMap<String, Boolean> canShowMap) {
        this.initRedPoints(redPoints, parentId, relativeRedPointIds, showNumMap, showNumsArrays, numMap, numsArrays, canShowMap, null);
    }

    public void initRedPoints(@NonNull String[] redPoints, String parentId, HashMap<String, String> relativeRedPointIds,
                              HashMap<String, Boolean> showNumMap, boolean[] showNumsArrays, HashMap<String, Integer> numMap,
                              int[] numsArrays,  HashMap<String, Boolean> canShowMap, boolean[] canShowMapArrays) {
        ServiceMethod serviceMethod =
                new ServiceMethod.Builder()
                        .setRedPointIds(redPoints)
                        .setParentRedPointId(parentId)
                        .setRelativeRedPointIds(relativeRedPointIds)
                        .setShowNumMap(showNumMap)
                        .setShowNumsArrays(showNumsArrays)
                        .setNumMap(numMap)
                        .setNumsArrays(numsArrays)
                        .setCanShowMap(canShowMap)
                        .setCanShowArrays(canShowMapArrays).build();
        initRedPoints(serviceMethod);
    }

    ServiceMethod loadServiceMethod(Method method, Object[] args) {
        ServiceMethod result;
        synchronized (serviceMethodCache) {
            result = serviceMethodCache.get(method);
            if (result == null) {
                result = new ServiceMethod.Builder(method, args).build();
                serviceMethodCache.put(method, result);
            }
        }
        return result;
    }

    void initRedPoints(ServiceMethod serviceMethod) {
        String[] redPointIds = serviceMethod.getRedPoints();
        String parentPointId = TextUtils.isEmpty(serviceMethod.getParentRedPointId()) ? null : serviceMethod.getParentRedPointId();
        HashMap<String, String> relativeRedPointIds = serviceMethod.getRelativeRedPointIds();
        HashMap<String, Boolean> showNumMap = serviceMethod.getShowNumMap();
        boolean[] showNumsArrays = serviceMethod.getShowNumsArrays();
        HashMap<String, Integer> numMap = serviceMethod.getNumMap();
        int[] numsArrays = serviceMethod.getNumsArrays();
        HashMap<String, Boolean> canShowMap = serviceMethod.getCanShowMap();
        boolean[] canShowMapArrays = serviceMethod.getCanShowArrays();
        RedPointData redPointData = null;
        for (int i = 0; i < redPointIds.length; i++) {
            redPointData = new RedPointData();
            redPointData.id = redPointIds[i];
            redPointData.parentRedPointId = parentPointId;

            // 是否展示红点数字
            if (showNumsArrays != null && showNumsArrays.length > 0) {
                redPointData.isShowNum = showNumsArrays[i];
            }

            if (showNumMap != null && !showNumMap.isEmpty()) {
                Boolean showNum = showNumMap.get(redPointData.id);
                redPointData.isShowNum = showNum != null ? showNum : false;
            }

            if (numsArrays != null && numsArrays.length > 0) {
                redPointData.num = numsArrays[i];
                if (redPointData.num >= 0) {
                    redPointData.isShowNum = true;
                }
            }

            if (numMap != null && !numMap.isEmpty()) {
                Integer num = numMap.get(redPointData.id);
                redPointData.num = num != null ? num : -1;
                if (redPointData.num >= 0) {
                    redPointData.isShowNum = true;
                }
            }

            // 初始化关联节点
            if (relativeRedPointIds != null && !relativeRedPointIds.isEmpty()) {
                for (Map.Entry<String, String> entry : relativeRedPointIds.entrySet()) {
                    if (redPointData.id.equals(entry.getValue())) {
                        if (redPointData.relativeRedPointIds == null) {
                            redPointData.relativeRedPointIds = new HashSet<>();
                        }
                        redPointData.relativeRedPointIds.add(entry.getKey());
                    }
                }
            }

            // 同级红点
            redPointData.sameClassRedPointIdsArrays = redPointIds;

            // 索引
            redPointData.index = i;

            // 是否默认为可展示
            if (canShowMap != null && !canShowMap.isEmpty()) {
                Boolean canShow = canShowMap.get(redPointData.id);
                redPointData.canShow = saveAdapter.load(context, redPointData.id, canShow != null ? canShow : false);
            }

            if (canShowMapArrays != null && canShowMapArrays.length > 0) {
                redPointData.canShow = saveAdapter.load(context, redPointData.id, canShowMapArrays[i]);
            }

            RPObserverManager.getInstance().putRedPointData(redPointData.id, redPointData);
            RPObserverManager.getInstance().putRedPointStrategy(redPointData.id, redPointStrategyAdapter);
            RPObserverManager.getInstance().putSaveAdapter(redPointData.id, saveAdapter);
        }

        for (String redPointId : redPointIds) {
            redPointData = RPObserverManager.getInstance().getRedPointData(redPointId);
            RPObserverManager.getInstance().saveRedPointsChange(context, redPointId, redPointData.canShow);
        }
//        RPObserverManager.getInstance().processSameClassRedPoints(redPointStrategyAdapter, redPointIds, -1);
    }

    /**
     * 对外提供的针对红点的相关操作，展示的时候设置flag为true，不展示的设置为false
     * @param redPointId 必须对应红点的id
     * @param flag true | false
     * */
    public void saveRedPointsChange(String redPointId, boolean flag) {
        RPObserverManager.getInstance().saveRedPointsChange(context, redPointId, flag);
    }

    /**
     * 得到需要的红点数据结构，根据红点数据结构来进行相关处理
     * 如展示红点数字、如获取红点相关信息等
     * @param redPointId 必须对应红点的id
     **/
    public RedPointData getRedPointData(String redPointId) {
        return RPObserverManager.getInstance().getRedPointData(redPointId);
    }

    /**
     * 注册红点观察者
     * */
    public void register(String redPointId, IRedPointsObserver observer) {
        RPObserverManager.getInstance().registerRedPointObserver(redPointId, observer);
    }

    /**
     * 反注册红点观察者
     * */
    public void unregister(String redPointId) {
        RPObserverManager.getInstance().unregisterRedPointObserver(redPointId);
    }

    public void onDestroy(String[] arrays) {
        RPObserverManager.getInstance().onDestroy(arrays);
    }

    public void onDestroy(String redPointId) {
        RPObserverManager.getInstance().onDestroy(redPointId);
    }

    /**
     * 用户通过Builder方式进行RPM实例化
     * */
    public static final class Builder {
        private IPushAdapter pushAdapter = new DefaultPushAdapter();
        private IRedPointStrategyAdapter redPointStragyAdapter = new DefaultRedPointStrategyAdapter();
        private ISaveAdapter saveAdapter = new DefaultSaveAdapter();
        private Context context;

        public Builder(@NonNull  Context context) {
            this.context = context;
        }

        public Builder addPustAdapter(IPushAdapter pushAdapter) {
            this.pushAdapter = pushAdapter;
            return this;
        }

        public Builder addRedPointStragyAdapter(IRedPointStrategyAdapter redPointStragyAdapter) {
            this.redPointStragyAdapter = redPointStragyAdapter;
            return this;
        }

        public Builder addSaveAdapter(ISaveAdapter saveAdapter) {
            this.saveAdapter = saveAdapter;
            return this;
        }

        public RPM build() {
            return new RPM(this.context, this.pushAdapter, this.redPointStragyAdapter, this.saveAdapter);
        }
    }
}
