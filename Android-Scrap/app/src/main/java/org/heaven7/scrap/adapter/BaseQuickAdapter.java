/*
 * Copyright (C) 2015
 *            heaven7(donshine723@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.heaven7.scrap.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import org.heaven7.scrap.core.ViewHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstraction class of a BaseAdapter in which you only need to provide the
 * convert() implementation.<br/>
 * Using the provided BaseAdapterHelper, your code is minimalist.
 * 
 * @param <T>
 *            The type of the items in the list.
 */
public abstract class BaseQuickAdapter<T, H extends BaseAdapterHelper> extends
		BaseAdapter {

	protected int layoutResId;
	protected final List<T> data;
	protected boolean displayIndeterminateProgress = false;


	/**
	 * Same as QuickAdapter#QuickAdapter(Context,int) but with some
	 * initialization data.
	 * 
	 * @param layoutResId
	 *            The layout resource id of each item.
	 * @param data
	 *            A new list is created out of this one to avoid mutable list
	 */
	public BaseQuickAdapter(int layoutResId, List<T> data) {
		this.data = data == null ? new ArrayList<T>() : new ArrayList<T>(data);
		this.layoutResId = layoutResId;
	}
	public BaseQuickAdapter(int layoutResId) {
		this(layoutResId,null);
	}

	protected MultiItemTypeSupport<T> mMultiItemSupport;

	public BaseQuickAdapter(ArrayList<T> data,
			MultiItemTypeSupport<T> multiItemSupport) {
		this.mMultiItemSupport = multiItemSupport;
		this.data = data == null ? new ArrayList<T>() : new ArrayList<T>(data);
	}

	@Override
	public int getCount() {
		int extra = displayIndeterminateProgress ? 1 : 0;
		return data.size() + extra;
	}

	@Override
	public T getItem(int position) {
		if (position >= data.size())
			return null;
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		if (mMultiItemSupport != null)
			return mMultiItemSupport.getViewTypeCount() + 1;
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		if (displayIndeterminateProgress) {
			if (mMultiItemSupport != null)
				return position >= data.size() ? 0 : mMultiItemSupport
						.getItemViewType(position, data.get(position));
		} else {
			if (mMultiItemSupport != null)
				return mMultiItemSupport.getItemViewType(position,
						data.get(position));
		}
		// if no data. return 0
		return position >= data.size() ? 0 : 1;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (getItemViewType(position) == 0) {
			return createIndeterminateProgressView(convertView, parent);
		}
		final H helper = getAdapterHelper(position, convertView, parent);
		T item = getItem(position);
		helper.setAssociatedObject(item);
		convert(parent.getContext(),position,helper.getViewHelper(), item);
		return helper.getViewHelper().getRootView();

	}

	protected static View createIndeterminateProgressView(View convertView,
			ViewGroup parent) {
		if (convertView == null) {
			Context context = parent.getContext();
			FrameLayout container = new FrameLayout(context);
			container.setForegroundGravity(Gravity.CENTER);
			ProgressBar progress = new ProgressBar(context);
			container.addView(progress);
			convertView = container;
		}
		return convertView;
	}

	@Override
	public boolean isEnabled(int position) {
		return position < data.size();
	}

	public void add(T elem) {
		data.add(elem);
		notifyDataSetChanged();
	}

	public void addAll(List<T> elem) {
		data.addAll(elem);
		notifyDataSetChanged();
	}

	public void set(T oldElem, T newElem) {
		set(data.indexOf(oldElem), newElem);
	}

	public void set(int index, T elem) {
		data.set(index, elem);
		notifyDataSetChanged();
	}

	public void remove(T elem) {
		data.remove(elem);
		notifyDataSetChanged();
	}

	public void remove(int index) {
		data.remove(index);
		notifyDataSetChanged();
	}

	public void replaceAll(List<T> elem) {
		data.clear();
		data.addAll(elem);
		notifyDataSetChanged();
	}

	public boolean contains(T elem) {
		return data.contains(elem);
	}

	/** Clear data list */
	public void clear() {
		data.clear();
		notifyDataSetChanged();
	}

	public void showIndeterminateProgress(boolean display) {
		if (display == displayIndeterminateProgress)
			return;
		displayIndeterminateProgress = display;
		notifyDataSetChanged();
	}


	/**
	 * You can override this method to use a custom BaseAdapterHelper in order
	 * to fit your needs
	 * 
	 * @param position
	 *            The position of the item within the adapter's data set of the
	 *            item whose view we want.
	 * @param convertView
	 *            The old view to reuse, if possible. Note: You should check
	 *            that this view is non-null and of an appropriate type before
	 *            using. If it is not possible to convert this view to display
	 *            the correct data, this method can create a new view.
	 *            Heterogeneous lists can specify their number of view types, so
	 *            that this View is always of the right type (see
	 *            {@link #getViewTypeCount()} and {@link #getItemViewType(int)}
	 *            ).
	 * @param parent
	 *            The parent that this view will eventually be attached to
	 * @return An instance of BaseAdapterHelper
	 */
	protected abstract H getAdapterHelper(int position, View convertView,
			ViewGroup parent);

	/**
	 *
	 * Implement this method and use the helper to adapt the view to the given
	 * item.
	 * @param context
	 * @param position the postion of adapterview. such as: listView
	 * @param helper the helper of view help you to fast set view's property
	 * @param item the data
	 */
	protected abstract void convert(Context context, int position, ViewHelper helper,
									T item);

}
