package com.yueding.puzzle.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.yueding.puzzle.Utils.ScreenUtil;

import java.util.List;

/**
 * Created by yueding on 2017/11/24.
 */

public class GridViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<Bitmap> picList;

    public GridViewAdapter(Context mContext, List<Bitmap> picList) {
        this.mContext = mContext;
        this.picList = picList;
    }

    @Override
    public int getCount() {
        return picList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView view;
        int density = (int) ScreenUtil.getDeviceDensity(mContext);
        if (convertView == null) {
            view = new ImageView(mContext);
            view.setLayoutParams(new GridView.LayoutParams(120 * density, 160 * density));
            view.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            view = (ImageView) convertView;
        }
        view.setImageBitmap(picList.get(position));
        return view;
    }

}
