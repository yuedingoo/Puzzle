package com.yueding.puzzle.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.yueding.puzzle.Constant.Cont;
import com.yueding.puzzle.Bean.ItemBean;
import com.yueding.puzzle.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yueding on 2017/11/25.
 */

public class ImagesUtil {

    public ItemBean itemBean;

    /**
     * 图像工具类：实现图像的分割与自适应
     */
    public void createInitBitmaps(Context context, int type, Bitmap picSelected) {
        Bitmap bitmap;
        List<Bitmap> itemBitmapList = new ArrayList<>();
        int itemWidth = picSelected.getWidth() / type;
        int itemHeight = picSelected.getHeight() / type;
        for (int i = 1; i <= type; i++) {
            for (int j = 1; j <= type; j++) {
                bitmap = Bitmap.createBitmap(picSelected,
                        (j - 1) * itemWidth, (i - 1) * itemHeight, itemWidth, itemHeight);
                itemBitmapList.add(bitmap);
                itemBean = new ItemBean((i - 1) * type + j, (i - 1) * type + j, bitmap);
                GameUtil.mItemBeans.add(itemBean);
            }
        }
        // 保存最后一个图片在拼图完成时填充
        Cont.mLastBitmap = itemBitmapList.get(type * type - 1);
        // 设置最后一个为空Item
        itemBitmapList.remove(type * type - 1);
        GameUtil.mItemBeans.remove(type * type - 1);
        Bitmap blankBitmap = BitmapFactory.decodeResource(
                context.getResources(), R.mipmap.blank);
        blankBitmap = Bitmap.createBitmap(
                blankBitmap, 0, 0, itemWidth, itemHeight);

        itemBitmapList.add(blankBitmap);
        GameUtil.mItemBeans.add(new ItemBean(type * type, 0, blankBitmap));
        GameUtil.mBlankItemBean = GameUtil.mItemBeans.get(type * type - 1);
    }

    /**
     * 处理图片 放大、缩小到合适位置
     *
     * @param newWidth  缩放后Width
     * @param newHeight 缩放后Height
     * @param bitmap    bitmap
     * @return bitmap
     */
    public Bitmap resizeBitmap(float newWidth, float newHeight, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(
                newWidth / bitmap.getWidth(),
                newHeight / bitmap.getHeight());
        return Bitmap.createBitmap(
                bitmap, 0, 0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix, true);
    }

    /**
     * 把图片切成正方形
     * @param orgBitmap
     * @return
     */
    public Bitmap squareBitmap(Bitmap orgBitmap) {
        Bitmap bitmap;
        int width = orgBitmap.getWidth();
        int height = orgBitmap.getHeight();
        int startX = 0;
        int startY = 0;
        if (width < height) {
            startY = (height - width) / 2;
            bitmap = Bitmap.createBitmap(orgBitmap, startX, startY, width, width);
        } else {
            startX = (width - height) / 2;
            bitmap = Bitmap.createBitmap(orgBitmap, startX, startY, height, height);
        }
        return bitmap;
    }
}
