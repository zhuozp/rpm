package com.gibbon.rpm.demo.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gibbon.rpm.demo.R;

public class ViewPagerTabGroupView extends FrameLayout {

	private LinearLayout tabGroup;

	private ViewPager viewPager;

	private ViewPager.OnPageChangeListener onPageChangeListenerProxy;
	
	private int currentTabPosition = 0;
	

	public ViewPagerTabGroupView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public ViewPagerTabGroupView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ViewPagerTabGroupView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		tabGroup = new LinearLayout(context);
		tabGroup.setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(tabGroup, layoutParams);
	}

	public void setViewPager(ViewPager viewPager) {
		this.viewPager = viewPager;
		viewPager.setOnPageChangeListener(new OnViewPageChangeListener());
		tabGroup.removeAllViews();
		currentTabPosition = viewPager.getCurrentItem();
		setup();
	}

	private void setup() {
		PagerAdapter adapter = viewPager.getAdapter();
		if (adapter instanceof ITabable) {
			ITabable tabable = (ITabable) adapter;
			createTabs(tabable);
		} else {
			throw new IllegalArgumentException("The PageAdapter should be implment ITabable.");
		}
	}

	private void createTabs(ITabable tabable) {
		
		int size = tabable.getCount();
		Context context = getContext();
		
		android.widget.LinearLayout.LayoutParams tabParams = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
		
		OnTabClickListener onTabClickListener = new OnTabClickListener();
		
		for (int i = 0; i < size; i++) {
			TabView tabView = new TabView(context, tabable, i);
			tabGroup.addView(tabView, tabParams);
			tabView.setTag(i);
			tabView.setOnClickListener(onTabClickListener);
		}
		
		setCurrentPosition(currentTabPosition);
		
		
		
	}

	public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
		this.onPageChangeListenerProxy = listener;
	}
	
	private void setCurrentPosition(int position) {
		tabGroup.getChildAt(currentTabPosition).setSelected(false);
		this.currentTabPosition = position;
		tabGroup.getChildAt(currentTabPosition).setSelected(true);
	}

	private class OnTabClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			
			int position = (Integer) v.getTag();
			if (position == currentTabPosition) {
				return;
			}
			viewPager.setCurrentItem(position, false);
			
		}
		
	};
	
	
	private class OnViewPageChangeListener implements ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			if (onPageChangeListenerProxy != null) {
				onPageChangeListenerProxy.onPageScrollStateChanged(arg0);
			}

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			if (onPageChangeListenerProxy != null) {
				onPageChangeListenerProxy.onPageScrolled(arg0, arg1, arg2);
			}

		}

		@Override
		public void onPageSelected(int arg0) {
			if (onPageChangeListenerProxy != null) {
				onPageChangeListenerProxy.onPageSelected(arg0);
			}

			setCurrentPosition(arg0);
		}

	}

	public static class TabView extends FrameLayout {
		
		public TabView(Context context, ITabable tabable, int position) {
			this(context, tabable.getPageIcon(position), tabable.getPageTitle(position));
		}

		public TabView(Context context, Drawable icon, CharSequence label) {
			super(context);
			View view = LayoutInflater.from(context).inflate(R.layout.item_tab_view, this, false);
			((ImageView) view.findViewById(R.id.icon)).setImageDrawable(icon);
			((TextView) view.findViewById(R.id.label)).setText(label);
			addView(view);
		}

	}

	public interface ITabable {
		public Drawable getPageIcon(int position);

		public CharSequence getPageTitle(int position);
		
		public int getCount();
	}

	public void setRedPointAtPosition(int position, boolean show) {
		if (position >= 0 && tabGroup != null
				&& position < tabGroup.getChildCount()) {
			View view = tabGroup.getChildAt(position);
			if (show) {
				view.findViewById(R.id.red).setVisibility(View.VISIBLE);
			} else {
				view.findViewById(R.id.red).setVisibility(View.GONE);
			}
		}
	}

}