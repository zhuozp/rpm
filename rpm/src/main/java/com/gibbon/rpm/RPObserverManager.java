package com.gibbon.rpm;

import android.content.Context;

import com.gibbon.rpm.adapter.IRedPointStrategyAdapter;
import com.gibbon.rpm.adapter.ISaveAdapter;
import com.gibbon.rpm.model.IRedPointsObserver;
import com.gibbon.rpm.model.RedPointData;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author zhipeng.zhuo
 * @date 2020-03-03
 */
public final class RPObserverManager {

    // 红点列表
    Map<String, RedPointData> redPointMaps = new HashMap<>();

    // 观察者集合
    Map<String, SoftReference<IRedPointsObserver>> redPointsObservers = new HashMap<>();

    // 每组红点的各个id都会有对应的SaveAdapter配套
    Map<String, ISaveAdapter> saveAdapters = new HashMap<>();

    // 每组红点的各个id都会有对应的RedPointStrategyAdapter配套
    Map<String, IRedPointStrategyAdapter> redPointStrategyAdapters = new HashMap<>();

    void putRedPointData(String redPointId, RedPointData data) {
        redPointMaps.put(redPointId, data);
    }

    RedPointData getRedPointData(String redPointId) {
        return redPointMaps.get(redPointId);
    }

    void registerRedPointObserver(String redPointId, IRedPointsObserver observer) {
        redPointsObservers.put(redPointId, new SoftReference<>(observer));
    }

    void unregisterRedPointObserver(String redPointId) {
        redPointsObservers.remove(redPointId);
        // 同时去掉
        redPointMaps.remove(redPointId);
        saveAdapters.remove(redPointId);
        redPointStrategyAdapters.remove(redPointId);
    }

    void putSaveAdapter(String redPointId, ISaveAdapter saveAdapter) {
        saveAdapters.put(redPointId, saveAdapter);
    }

    ISaveAdapter getSaveAdapter(String redPointId) {
        return saveAdapters.get(redPointId);
    }

    void putRedPointStrategy(String redPointId, IRedPointStrategyAdapter redPointStrategyAdapter) {
        redPointStrategyAdapters.put(redPointId, redPointStrategyAdapter);
    }

    IRedPointStrategyAdapter getRedPointStrategyAdapter(String redPointId) {
        return redPointStrategyAdapters.get(redPointId);
    }



    /**
     * 所有红点状态改变需要调用改方法，需要处理同级红点能否展示问题
     * 红点状态改变,包括保存配置文件，父红点，自己以及关联红点的状态
     *
     * @param context
     * @param redPointId
     * @param flag
     */
    public void saveRedPointsChange(Context context, String redPointId, boolean flag) {
        saveRedPointsChange(context, redPointId, flag, true);
    }

    private void saveRedPointsChange(Context context, String redPointId, boolean flag, boolean needProcessRelativeRP) {
        RedPointData data = redPointMaps.get(redPointId);
        if (data == null) {
            return;
        }

        data.canShow = flag;

        /**
         * 1. 这里递归关联节点的时候，可能因为还没create()，不存在相关的adapter，
         * 2. 父红点节点有自己的保存策略, 根据实际场景，一般父红点的保存策略是已经生成的
         * 3. 同级红点相关策略是一样的
         * 4. 存在关联红点的情况下，关联红点的id要不一样，否则无法纳入观察模式中
         **/
        ISaveAdapter selfAdapter = saveAdapters.get(redPointId);

        if (selfAdapter != null) {
            selfAdapter.save(context, redPointId, flag);
        }

        // 处理父红点, 展示状态不一样的情况下才进行处理
        RedPointData parentData = redPointMaps.get(data.parentRedPointId);
        if (parentData != null) {
            if (parentData.subPageRedPointIds == null) {
                parentData.subPageRedPointIds = new HashSet<>();
            }

            if (flag) {
                parentData.subPageRedPointIds.add(redPointId);
            } else {
                parentData.subPageRedPointIds.remove(redPointId);
            }


//            ISaveAdapter parentAdapter = saveAdapters.get(data.parentRedPointId);
//            if (flag) {
//                parentData.canShow = true;
//
//                if (parentAdapter != null) {
//                    parentAdapter.save(context, data.parentRedPointId, true);
//                }
//
//            } else {
//                if (parentData.subPageRedPointIds.size() <= 0) {
//                    parentData.canShow = false;
//
//                    if (parentAdapter != null) {
//                        parentAdapter.save(context, data.parentRedPointId, false);
//                    }
//                }
//            }

            boolean parentRedPointCanShow = parentData.canShow;
            if (parentData.subPageRedPointIds == null || parentData.subPageRedPointIds.isEmpty()) {
                parentRedPointCanShow = false;
            } else {
                for (String subPageRedPointId : parentData.subPageRedPointIds) {
                    RedPointData subPageRedPoint = getRedPointData(subPageRedPointId);
                    if (subPageRedPoint != null && subPageRedPoint.canShow) {
                        parentRedPointCanShow = true;
                        break;
                    }
                }
            }

            saveRedPointsChange(context, data.parentRedPointId, parentRedPointCanShow, true);

            // 处理父亲红点同级红点展现问题
//            if (parentData.sameClassRedPointIdsArrays != null && parentData.sameClassRedPointIdsArrays.length > 0) {
//                processSameClassRedPoints(redPointStrategyAdapters.get(data.parentRedPointId), parentData.sameClassRedPointIdsArrays, parentData.index);
//            } else {
//                parentData.reallyShow = parentData.canShow;
//            }
//            //TODO, 父红点notify
//            SoftReference<IRedPointsObserver> parentObserver = redPointsObservers.get(data.parentRedPointId);
//            if (parentObserver != null && parentObserver.get() != null) {
//                parentObserver.get().notifyRedPointChange(parentData.reallyShow,
//                        parentData.isShowNum ? parentData.subPageRedPointIds.size() : -1);
//            }

        }

        // 关联红点notify
        if (needProcessRelativeRP && data.relativeRedPointIds != null && !data.relativeRedPointIds.isEmpty()) {
            RedPointData relativeData = null;
            for (String relativeRedPointId : data.relativeRedPointIds) {
                relativeData = redPointMaps.get(relativeRedPointId);

                // 不一致的情况才进行处理
                if (relativeData != null && relativeData.reallyShow != flag) {
                    saveRedPointsChange(context, relativeRedPointId, flag, false);
                }
            }
        }

        // 自身同级红点notifyChange
        if (data.sameClassRedPointIdsArrays != null && data.sameClassRedPointIdsArrays.length > 0) {
            processSameClassRedPoints(redPointStrategyAdapters.get(data.id), data.sameClassRedPointIdsArrays, data.index);
        } else {
            data.reallyShow = data.canShow;
        }

        //自身红点notifyChange
        SoftReference<IRedPointsObserver> selfObserver = redPointsObservers.get(data.id);
        if (selfObserver != null && selfObserver.get() != null) {
            selfObserver.get().notifyRedPointChange(data.id, data.reallyShow, data.isShowNum ? (data.num > 1 ? data.num : data.subPageRedPointIds != null ? data.subPageRedPointIds.size() : -1) : -1);
        }
    }

    /**
     * 处理同级红点
     *
     * @param arrays
     * @param index  if index < 0, then just init, else need process
     */
    protected void processSameClassRedPoints(IRedPointStrategyAdapter redPointStrategyAdapter, String[] arrays, int index) {
        if (arrays == null || arrays.length <= 0) {
            return;
        }

        int maxCount = redPointStrategyAdapter.sameMaxRedPointShow();
        if (maxCount <= IRedPointStrategyAdapter.NOT_NEED_MAX ) {
            maxCount = arrays.length;
        }

        int count = 0;

        RedPointData data = null;
        for (int i = 0; i < arrays.length; i++) {
            data = getRedPointData(arrays[i]);
            if (data != null && data.canShow && count < maxCount) {
                count++;
                data.reallyShow = true;
            } else if (data != null) {
                data.reallyShow = false;
            }

            SoftReference<IRedPointsObserver> observer = redPointsObservers.get(arrays[i]);
            if (observer != null && observer.get() != null && data != null) {
                observer.get().notifyRedPointChange(data.id, data.reallyShow, data.isShowNum ? (data.num > 1 ? data.num : data.subPageRedPointIds != null ? data.subPageRedPointIds.size() : -1) : -1);
            }
        }
//        if (index < 0) {
//            for (int i = 0; i < arrays.length; i++) {
//                data = getRedPointData(arrays[i]);
//                if (data != null && data.canShow && count < maxCount) {
//                    count++;
//                    data.reallyShow = true;
//                    if (count == maxCount) {
//                        break;
//                    }
//                } else if (data != null) {
//                    data.reallyShow = false;
//                }
//
//                SoftReference<IRedPointsObserver> observer = redPointsObservers.get(arrays[i]);
//                if (observer != null && observer.get() != null && data != null) {
//                    observer.get().notifyRedPointChange(data.id, data.reallyShow, data.isShowNum ? (data.num > 1 ? data.num : data.subPageRedPointIds != null ? data.subPageRedPointIds.size() : -1) : -1);
//                }
//            }
//        }
//        else {
//            for (int i = 0; i < index; i++) {
//                data = getRedPointData(arrays[i]);
//                if (data.reallyShow) {
//                    count++;
//                }
//            }
//            // 如果改变，通知同级可以展示的改变
//            if (count < maxCount) {
//                for (int i = index; i < arrays.length; i++) {
//                   data = getRedPointData(arrays[i]);
//                    if (data != null && data.canShow && count < maxCount) {
//                        count++;
//                        data.reallyShow = true;
//                    } else if (data != null) {
//                        data.reallyShow = false;
//                    }
//                    SoftReference<IRedPointsObserver> observer = redPointsObservers.get(arrays[i]);
//                    if (observer != null && observer.get() != null && data != null) {
//                        observer.get().notifyRedPointChange(data.id, data.reallyShow, data.isShowNum ? (data.num > 1 ? data.num : data.subPageRedPointIds != null ? data.subPageRedPointIds.size() : -1) : -1);
//                    }
//                }
//            } else {
//                for (int i = index; i < arrays.length; i++) {
//                    data = getRedPointData(arrays[i]);
//                    if (data != null) {
//                        data.reallyShow = false;
//                    }
//                    SoftReference<IRedPointsObserver> observer = redPointsObservers.get(arrays[i]);
//                    if (observer != null && observer.get() != null && data != null) {
//                        observer.get().notifyRedPointChange(data.id, data.reallyShow, data.isShowNum ? (data.num > 1 ? data.num : data.subPageRedPointIds != null ? data.subPageRedPointIds.size() : -1) : -1);
//                    }
//                }
//            }
//        }
    }

    void onDestroy(String[] arrays) {
        if (arrays != null) {
            for (int i = 0; i < arrays.length; i++) {
                unregisterRedPointObserver(arrays[i]);
            }
        }
    }

    void onDestroy(String redPointId) {
        unregisterRedPointObserver(redPointId);
    }

    void clear() {
        redPointsObservers.clear();
        redPointsObservers = null;
        redPointMaps.clear();
        redPointMaps = null;
        saveAdapters.clear();
        saveAdapters = null;
        redPointStrategyAdapters.clear();
        redPointStrategyAdapters.clear();
        InnerRPObserverManager.sInstance = null;
    }

    IRedPointsObserver getRedPointsObserver(String key) {
        SoftReference<IRedPointsObserver> softReference = redPointsObservers.get(key);
        if (softReference != null && softReference.get() != null) {
            return softReference.get();
        }

        return null;
    }

    static RPObserverManager getInstance() {
        return InnerRPObserverManager.sInstance;
    }

    private RPObserverManager() {

    }

    private static class InnerRPObserverManager {
        private static RPObserverManager sInstance = new RPObserverManager();
    }
}
