package com.janseon.helper.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class BasePagerAdapter<T> extends PagerAdapter {

	protected Context mContext;
	protected LayoutInflater mInflater;
	protected ArrayList<T> modelList = new ArrayList<T>();

	public BasePagerAdapter(Context context, ArrayList<T> list) {
		super();
		setList(context, list);
	}

	public void setList(Context context, ArrayList<T> list) {
		mContext = context;
		if (context != null) {
			mInflater = LayoutInflater.from(context);
		}
		modelList = list;
	}

	@Override
	public int getCount() {
		return modelList.size();
	}

	public T getItem(int position) {
		return modelList.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		View view = (View) object;
		container.removeView(view);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

}