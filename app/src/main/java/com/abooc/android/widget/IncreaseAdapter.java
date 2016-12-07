package com.abooc.android.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * IncreaseAdapter 支持自增长
 * 
 * @author liruiyu
 * @2014-5-21
 */
public class IncreaseAdapter<T> extends BaseAdapter {

	public interface OnLoadPositionListener {
		void onLoadPosition(int max, int position);
	}

	private List<T> array;
	private OnLoadPositionListener mOnLoadPositionListener;
	private int MaxLoadedPosition;

	public IncreaseAdapter(List<T> array) {
		if (array == null) {
			this.array = new ArrayList<T>();
		} else {
			this.array = array;
		}
	}

	public void setOnLoadPositionListener(OnLoadPositionListener l) {
		mOnLoadPositionListener = l;
	}

	public void increase(List<T> array) {
		if (this.array == null) {
			this.array = array;
		} else {
			this.array.addAll(array);
		}
		notifyDataSetChanged();
	}

	public List<T> getList() {
		return array;
	}

    public void setList(List<T> array) {
        this.array = array;
    }

	public void add(T t) {
		if (this.array == null) {
			this.array = new ArrayList<T>();
		}
		this.array.add(t);
		notifyDataSetChanged();
	}

	public void add(List<T> t) {
		if (this.array == null) {
			this.array = new ArrayList<T>();
		}
		this.array.addAll(t);
		notifyDataSetChanged();
	}

	public void set(T t, int position) {
		if (this.array != null && position >= 0 && position < getCount()) {
			this.array.set(position, t);
		}
		notifyDataSetChanged();
	}

	public void remove(T t, Comparable<T> c) {
		if (this.array != null) {
			this.array.remove(t);
			notifyDataSetChanged();
		}
	}

	public void remove(T object) {
		if (array != null) {
			array.remove(object);
			notifyDataSetChanged();
		}
	}

	public boolean contains(T t) {
		if (this.array != null) {
			return this.array.contains(t);
		}
		return false;
	}

	public void update(List<T> array) {
		this.array = array;
		notifyDataSetChanged();
		clearMaxLoadedPosition();
	}

	public void remove(int position) {
		if (this.array != null && position < array.size() && position >= 0) {
			this.array.remove(position);
			notifyDataSetChanged();
		}
	}

	public void onLoadPosition(int max, int position) {
		MaxLoadedPosition = position;
	}

	public void clearMaxLoadedPosition() {
		MaxLoadedPosition = 0;
	}

	public void clear() {
		if (this.array != null) {
			this.array.clear();
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return array == null ? 0 : array.size();
	}

	@Override
	public T getItem(int position) {
		return array == null ? null : array.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (MaxLoadedPosition < position) {
			if (mOnLoadPositionListener != null) {
				mOnLoadPositionListener.onLoadPosition(getCount(), position);
			}
			onLoadPosition(getCount(), position);
		}

		return convertView;
	}
}
