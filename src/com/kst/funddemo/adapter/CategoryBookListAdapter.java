package com.kst.funddemo.adapter;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.kst.funddemo.R;
import com.kst.funddemo.model.json.CategoryBookBean;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryBookListAdapter extends BaseAdapter {

	private Context mContext;

	private List<CategoryBookBean> lists = new ArrayList<CategoryBookBean>();

	// 默认类型全部（0）
	private String mSortType = "0";

	public void setSortType(String type) {
		mSortType = type;
	}

	public void reloadList(List<CategoryBookBean> list) {
		if (null != list) {
			lists.clear();
			lists.addAll(list);
		}
		notifyDataSetChanged();
	}

	public void appendList(List<CategoryBookBean> list) {
		if (null != list) {
			lists.addAll(list);
		}
		notifyDataSetChanged();
	}

	public CategoryBookListAdapter(Context mContext) {
		this.mContext = mContext;
	}

	public CategoryBookBean getItemData(int i) {
		return lists.get(i);
	}

	@Override
	public int getCount() {
		return lists.size();
	}

	@Override
	public Object getItem(int i) {
		return lists.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(
					R.layout.item_category_book, null);
			holder.mImageBookCover = (ImageView) view
					.findViewById(R.id.book_cover);
			holder.mTextViewBookIndex = (TextView) view
					.findViewById(R.id.book_index);
			holder.mTextViewBookName = (TextView) view
					.findViewById(R.id.book_name);
			holder.mTextViewBookAuthor = (TextView) view
					.findViewById(R.id.book_author);
			holder.mTextViewBodkIntro = (TextView) view
					.findViewById(R.id.book_intro);
			holder.mTextViewBookVotes = (TextView) view
					.findViewById(R.id.book_votes);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		CategoryBookBean bean = lists.get(i);
		holder.mTextViewBookName.setText(bean.getTitle());
		holder.mTextViewBookIndex.setText(String.valueOf(i + 1));
		holder.mTextViewBookAuthor.setText(bean.getAuthors());
		holder.mTextViewBodkIntro.setText(bean.getIntro());

		// 更具排序类型，动态显示 人气 | 字数 | null
		if (TextUtils.equals("0", mSortType)) {
			holder.mTextViewBookVotes.setText(bean.getNumOfPopularity());
			holder.mTextViewBookVotes.setVisibility(View.VISIBLE);
		} else if (TextUtils.equals("2", mSortType)) {
			holder.mTextViewBookVotes.setText(bean.getNumOfChars());
			holder.mTextViewBookVotes.setVisibility(View.VISIBLE);
		} else {
			holder.mTextViewBookVotes.setText("");
			holder.mTextViewBookVotes.setVisibility(View.INVISIBLE);
		}

		Glide.with(mContext).load(bean.getCoverImage())
				.placeholder(R.drawable.default_book_cover).centerCrop()
				.into(holder.mImageBookCover);
		return view;
	}

	private class ViewHolder {
		ImageView mImageBookCover;
		TextView mTextViewBookIndex;
		TextView mTextViewBookName;
		TextView mTextViewBookAuthor;
		TextView mTextViewBodkIntro;
		TextView mTextViewBookVotes;
	}
}
