package com.gibbon.rpm.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.gibbon.rpm.RPM;
import com.gibbon.rpm.adapter.IRedPointStrategyAdapter;
import com.gibbon.rpm.demo.redpoint.RedPointConstance;
import com.gibbon.rpm.demo.service.RedPointService;
import com.gibbon.rpm.demo.widget.TabsAdapter;
import com.gibbon.rpm.demo.widget.ViewPagerTabGroupView;
import com.gibbon.rpm.model.IRedPointsObserver;
import com.gibbon.rpm.model.RedPointData;

public class MainActivity extends FragmentActivity implements IRedPointsObserver {
    private RPM rpm;
    private String[] arrays = new String[]{RedPointConstance.ID_HOME, RedPointConstance.ID_VIDEO, RedPointConstance.ID_ME};
    ViewPagerTabGroupView tabGroupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = findViewById(R.id.viewPage);
        tabGroupView = findViewById(R.id.tabGroup);
        TabsAdapter tabsAdapter = new TabsAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(tabsAdapter);
        tabGroupView.setViewPager(viewPager);
        tabGroupView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String parentId = null;
                switch (position) {
                    case 0:
                        parentId = RedPointConstance.ID_HOME;
                        break;
                    case 1:
                        parentId = RedPointConstance.ID_VIDEO;
                        break;
                    case 2:
//                        parentId = RedPointConstance.ID_ME;
                        break;
                }

                RedPointData redPointData = rpm.getRedPointData(parentId);
                if (redPointData != null) {
                    // 有操作，保存红点展示与否
                    rpm.saveRedPointsChange(parentId, false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        init();
    }

    private void init() {
        // 生成RPM实例
        rpm = new RPM.Builder(this)
                .addRedPointStragyAdapter(new IRedPointStrategyAdapter() {
                    @Override
                    public int sameMaxRedPointShow() {
                        return IRedPointStrategyAdapter.NOT_NEED_MAX;
                    }
                })
                .build();
        // 得到注解类实例
        RedPointService redPointService = rpm.create(RedPointService.class);
        // 注册红点观察者
        for (String id : arrays) {
            rpm.register(id, this);
        }

        // 初始化红点
        redPointService.initHomeActivityRedPoints(new boolean[]{true, true, true}, true);
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
            tabGroupView.setRedPointAtPosition(index, show);
        }
    }
}
