package com.yueding.puzzle.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by yueding on 2017/11/25.
 */

public class GridItemAdapter extends BaseAdapter {

    // 映射List
    private List<Bitmap> mBitmapItemLists;
    private Context mContext;

    public GridItemAdapter(List<Bitmap> mBitmapItemLists, Context mContext) {
        this.mBitmapItemLists = mBitmapItemLists;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mBitmapItemLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mBitmapItemLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView iv_pic_item;
        if (convertView == null) {
            iv_pic_item = new ImageView(mContext);
            /*// 设置布局 图片
            iv_pic_item.setLayoutParams(new GridView.LayoutParams(
                    mBitmapItemLists.get(position).getWidth(),
                    mBitmapItemLists.get(position).getHeight()));
            // 设置显示比例类型
            iv_pic_item.setScaleType(ImageView.ScaleType.FIT_CENTER);*/
        } else {
            iv_pic_item = (ImageView) convertView;
        }
        iv_pic_item.setImageBitmap(mBitmapItemLists.get(position));
        iv_pic_item.setAdjustViewBounds(true);
        return iv_pic_item;
    }
}
