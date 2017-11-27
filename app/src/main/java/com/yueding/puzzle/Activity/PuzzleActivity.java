package com.yueding.puzzle.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.yueding.puzzle.Adapter.GridItemAdapter;
import com.yueding.puzzle.Bean.ItemBean;
import com.yueding.puzzle.Constant.Cont;
import com.yueding.puzzle.R;
import com.yueding.puzzle.Utils.GameUtil;
import com.yueding.puzzle.Utils.ImagesUtil;
import com.yueding.puzzle.Utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PuzzleActivity extends AppCompatActivity {

    private GridView gridView;
    private TextView textTime;
    private TextView textStep;
    private Button buttonImg;
    private Button buttonReset;

    private Bitmap mPicSelected;
    private ImageView mImageView;

    private GridItemAdapter mAdapter;
    private boolean isSuccess = false;
    private AlertDialog dialog;

    private Timer mTimer;
    private List<Bitmap> mBitmapItemLists = new ArrayList<>();

    @SuppressLint("HandlerLeak")
    private Handler mHandle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Cont.TIMER_INDEX++;
                    textTime.setText(Cont.TIMER_INDEX + "秒");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        // 获取选择的图片
        Bitmap picSelectedTemp;
        Intent intent = getIntent();
        int resId = intent.getIntExtra("resId", 0);
        if (resId == 0) {
            String picPath = intent.getStringExtra("picPath");
            picSelectedTemp = BitmapFactory.decodeFile(picPath);
        } else {
            picSelectedTemp = BitmapFactory.decodeResource(getResources(), resId);
        }
        //处理图片
        handlerImage(picSelectedTemp);
        //显示原图
        showPicture();
        //初始化View
        initView();
        // 生成游戏数据
        generateGame();
        //点击事件，游戏
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //判断是否可移动
                if (GameUtil.isMovable(position) && !isSuccess) {
                    //交换点击的图片和空白图片
                    GameUtil.swapItems(GameUtil.mItemBeans.get(position), GameUtil.mBlankItemBean);
                    //更新数据，通知adapter更新ui
                    recreateData();
                    mAdapter.notifyDataSetChanged();
                    //更新步数
                    Cont.COUNT_INDEX++;
                    textStep.setText(Cont.COUNT_INDEX + "步");
                    //判断是否拼图成功
                    if (GameUtil.isSuccess()) {
                        //将拼图的图片完整显示
                        recreateData();
                        mBitmapItemLists.remove(Cont.TYPE * Cont.TYPE - 1);
                        mBitmapItemLists.add(Cont.mLastBitmap);
                        mAdapter.notifyDataSetChanged();
                        mTimer.cancel();
                        isSuccess = true;
                    }
                }
            }
        });
    }

    /**
     * 移动后更新adapter绑定的数据
     */
    private void recreateData() {
        mBitmapItemLists.clear();
        for (ItemBean bean : GameUtil.mItemBeans) {
            mBitmapItemLists.add(bean.getBitmap());
        }
    }

    /**
     * 生成游戏数据
     */
    private void generateGame() {
        //切图，获得正常排序的拼图数据
        new ImagesUtil().createInitBitmaps(this, Cont.TYPE, mPicSelected);
        //获得随机数据
        GameUtil.getPuzzleGenerator();
        //添加数据
        for (ItemBean temp : GameUtil.mItemBeans) {
            mBitmapItemLists.add(temp.getBitmap());
        }
        mAdapter = new GridItemAdapter(mBitmapItemLists, this);
        gridView.setAdapter(mAdapter);
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                if (!dialog.isShowing()) {
                    mHandle.sendMessage(message);
                }
            }
        }, 0, 1000);   //延迟0 ， 1000ms一周期
    }

    /**
     * 对图片处理 自适应大小
     *
     * @param bitmap bitmap
     */
    private void handlerImage(Bitmap bitmap) {
        // 将图片放大到固定尺寸
        int screenWidth = ScreenUtil.getScreenSize(this).widthPixels;
        int screenHeight = ScreenUtil.getScreenSize(this).heightPixels;
        mPicSelected = new ImagesUtil().resizeBitmap(
                screenWidth * 0.8f, screenHeight * 0.55f, bitmap);
    }

    private void initView() {
        gridView = findViewById(R.id.gv_puzzle);
        textTime = findViewById(R.id.tv_time);
        textStep = findViewById(R.id.tv_step);
        buttonImg = findViewById(R.id.bt_img);
        buttonReset = findViewById(R.id.bt_reset);


        gridView.setNumColumns(Cont.TYPE);
        textStep.setText(Cont.COUNT_INDEX + "步");
        textTime.setText(Cont.TIMER_INDEX + "秒");

        buttonImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPicture();
            }
        });
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearConfig();
                generateGame();
                recreateData();
                mAdapter.notifyDataSetChanged();
                textTime.setText(Cont.TIMER_INDEX + "秒");
                textStep.setText(Cont.COUNT_INDEX + "步");
            }
        });

    }

    /**
     * 显示图片对话框
     */
    private void showPicture() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        mImageView = new ImageView(this);
        mImageView.setImageBitmap(mPicSelected);
        mImageView.setAdjustViewBounds(true);
        dialog = builder.setView(mImageView).create();
        dialog.show();
        final Timer showTimer = new Timer();
        showTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                dialog.dismiss();
                showTimer.cancel();
            }
        }, 3000);
    }

    /**
     * 清楚本次游戏配置
     */
    private void clearConfig() {
        isSuccess = false;
        GameUtil.mItemBeans.clear();
        Cont.TIMER_INDEX = 0;
        Cont.COUNT_INDEX = 0;
        mTimer.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        clearConfig();
    }

}
