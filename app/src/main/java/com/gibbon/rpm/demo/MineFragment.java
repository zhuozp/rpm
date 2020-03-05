package com.gibbon.rpm.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gibbon.rpm.RPM;
import com.gibbon.rpm.adapter.IRedPointStrategyAdapter;
import com.gibbon.rpm.demo.redpoint.RedPointConstance;
import com.gibbon.rpm.demo.service.RedPointService;
import com.gibbon.rpm.demo.widget.BadgeView;
import com.gibbon.rpm.model.IRedPointsObserver;

/**
 * @author zhipeng.zhuo
 * @date 2020-03-05
 */
public class MineFragment extends Fragment implements IRedPointsObserver, View.OnClickListener {

    private RPM rpm;
    private String[] arrays = new String[] {RedPointConstance.ID_PACKAGE, RedPointConstance.ID_CARD, RedPointConstance.ID_SETTING};
    private BadgeView[] badgeViews = new BadgeView[arrays.length];
    private BadgeView relativeBadgeView;
    private View[] content = new View[arrays.length];

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        initView(view);
        init();
        return view;
    }

    private void initView(View view) {
        badgeViews[0] = view.findViewById(R.id.packageRedPointId);
        badgeViews[1] = view.findViewById(R.id.cardRedPointId);
        badgeViews[2] = view.findViewById(R.id.settingRedPointId);
        relativeBadgeView = view.findViewById(R.id.mineRelativePoint);

        content[0] = view.findViewById(R.id.packageContent);
        content[0].setOnClickListener(this);
        content[1] = view.findViewById(R.id.cardContent);
        content[1].setOnClickListener(this);
        content[2] = view.findViewById(R.id.settingContent);
        content[2].setOnClickListener(this);

        View relativeView = view.findViewById(R.id.relativeContent);
        relativeView.setOnClickListener(this);
    }



    private void init() {
        // 生成RPM实例
        rpm = new RPM.Builder(getContext())
                .addRedPointStragyAdapter(new IRedPointStrategyAdapter() {
                    @Override
                    public int sameMaxRedPointShow() {
                        return IRedPointStrategyAdapter.NOT_NEED_MAX;
                    }
                }).build();
        // 得到注解类实例
        RedPointService redPointService = rpm.create(RedPointService.class);


        // 注册红点观察者
        for (String id : arrays) {
            rpm.register(id, this);
        }

        // 初始化红点
        redPointService.initMeRedPoints(new int[]{5, 6, 7}, new boolean[]{true, true, true});
    }

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
//            badgeViews[index].setVisibility(show ? View.VISIBLE : View.GONE);
            badgeViews[index].setText(String.valueOf(showNum));
            if (show) {
                badgeViews[index].show();
            } else {
                badgeViews[index].hide();
            }

            if (index == 2) {
                relativeBadgeView.setText(String.valueOf(showNum));
                if (show) {
                    relativeBadgeView.show();
                } else {
                    relativeBadgeView.hide();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int index = -1;
        switch (v.getId()) {
            case R.id.packageContent:
                index = 0;
                break;
            case R.id.cardContent:
                index = 1;
                break;
            case R.id.settingContent:
            case R.id.relativeContent:
                index = 2;
                break;
            default:
                break;
        }

        rpm.saveRedPointsChange(arrays[index], false);
    }
}

