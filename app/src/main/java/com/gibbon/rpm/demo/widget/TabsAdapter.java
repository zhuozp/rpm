package com.gibbon.rpm.demo.widget;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.gibbon.rpm.demo.HomeFragment;
import com.gibbon.rpm.demo.MineFragment;
import com.gibbon.rpm.demo.VideoFragment;

public class TabsAdapter extends FragmentPagerAdapter implements ViewPagerTabGroupView.ITabable {

	@SuppressWarnings("unchecked")
	private Class<? extends Fragment>[] clazz = new Class[] {HomeFragment.class, VideoFragment.class,  MineFragment.class};
	
	private String[] titleRess = {"首页", "视频", "我的"};
	
	private Context context;

	public TabsAdapter(Context context, FragmentManager fm) {
		super(fm);
		this.context = context;
	}

	@Override
	public Fragment getItem(int position) {
		try {
			return clazz[position].newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int getCount() {
		return clazz.length;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		if (context == null) {
			return null;
		}
		return titleRess[position];
	}

	@Override
	public Drawable getPageIcon(int position) {

		return null;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return super.isViewFromObject(view, object);
	}
	
}
