package com.yueding.puzzle.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.yueding.puzzle.Adapter.GridViewAdapter;
import com.yueding.puzzle.Constant.Cont;
import com.yueding.puzzle.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private GridView gridView;
    private TextView selectLv;
    private TextView textLv;
    private int[] bitmapIds;
    private List<Bitmap> mPicList = new ArrayList<>();
    private String picPath;

    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    private Bitmap bitmap = null;
    private Uri imageUri;
    private PopupWindow windowLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = findViewById(R.id.gv_pic);
        selectLv = findViewById(R.id.selectLv);
        textLv = findViewById(R.id.lv);
        textLv.setText(Cont.TYPE + " X " + Cont.TYPE);
        initData();
        gridView.setAdapter(new GridViewAdapter(MainActivity.this, mPicList));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == bitmapIds.length - 1) {
                    showPopupWindow();
                } else {
                    Intent intent = new Intent(MainActivity.this, PuzzleActivity.class);
                    intent.putExtra("resId", bitmapIds[position]);
                    startActivity(intent);
                }
            }
        });
        selectLv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectWindow();
            }
        });
    }

    /**
     * 选择难度popWindow
     */
    private void showSelectWindow() {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.pop_lv_item, null, false);
        windowLv = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        TextView lv2 = view.findViewById(R.id.lv2);
        TextView lv3 = view.findViewById(R.id.lv3);
        TextView lv4 = view.findViewById(R.id.lv4);
        TextView lv5 = view.findViewById(R.id.lv5);
        lv2.setOnClickListener(this);
        lv3.setOnClickListener(this);
        lv4.setOnClickListener(this);
        lv5.setOnClickListener(this);
        windowLv.showAsDropDown(selectLv);
    }

    private void initData() {
        bitmapIds = new int[]{
                R.mipmap.pic1, R.mipmap.pic2, R.mipmap.pic3,
                R.mipmap.pic4, R.mipmap.pic5, R.mipmap.pic6,
                R.mipmap.pic7, R.mipmap.pic8, R.mipmap.pic9,
                R.mipmap.add
        };
        Bitmap[] bitmaps = new Bitmap[bitmapIds.length];
        for (int i = 0; i < bitmaps.length; i++) {
            bitmaps[i] = BitmapFactory.decodeResource(getResources(), bitmapIds[i]);
            mPicList.add(bitmaps[i]);
        }
    }

    /**
     * show popupWindow
     */
    private void showPopupWindow() {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.pop_item, null, false);
        final PopupWindow mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        View rootView = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
        mPopupWindow.setFocusable(true);
        mPopupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);

        TextView tv_takePhoto = view.findViewById(R.id.takePhoto);
        TextView tv_selectPhoto = view.findViewById(R.id.selectPhoto);
        tv_selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
                mPopupWindow.dismiss();
            }
        });
        tv_takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    takePhoto();
                    mPopupWindow.dismiss();
                }
            }
        });

    }

    /**
     * 从相册中选择
     */
    private void selectPhoto() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    /**
     * 使用相机拍照
     */
    private void takePhoto() {

        File outputImage = new File(getExternalCacheDir(), "takePhoto.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(MainActivity.this,
                    "com.yueding.puzzle.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    /**
     * 保存图片,必须要在onActivityResult中操作，因为在这之前并没有获取到图片
     * @param mBitmap 拍照后确认的图片
     * @return 保存路径
     */
    public String saveBitmap(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            Toast.makeText(this, "内存卡异常，请检查内存卡插入是否正确", Toast.LENGTH_SHORT).show();
            return "";
        }
        String path = System.currentTimeMillis() + ".jpg";
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PuzzlePhoto/");
        if (!f.exists()) {
            f.mkdir();
        }
        File file = new File(f, path);
        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    private String handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        return imagePath;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (bitmap != null) {
                        picPath = saveBitmap(bitmap);
                    }
                }
                Intent intent = new Intent(MainActivity.this, PuzzleActivity.class);
                intent.putExtra("picPath", picPath);
                startActivity(intent);
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    String imagePath;
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        imagePath = handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        imagePath = handleImageBeforeKitKat(data);
                    }
                    Intent intent1 = new Intent(MainActivity.this, PuzzleActivity.class);
                    intent1.putExtra("picPath", imagePath);
                    startActivity(intent1);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    Log.i("yueding", "onRequestPermissionsResult: " + "没有权限");
                }
                break;
        }
    }

    /**
     * 难度点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lv2:
                Cont.TYPE = 2;
                textLv.setText(Cont.TYPE + " X " + Cont.TYPE);
                windowLv.dismiss();
                break;
            case R.id.lv3:
                Cont.TYPE = 3;
                textLv.setText(Cont.TYPE + " X " + Cont.TYPE);
                windowLv.dismiss();
                break;
            case R.id.lv4:
                Cont.TYPE = 4;
                textLv.setText(Cont.TYPE + " X " + Cont.TYPE);
                windowLv.dismiss();
                break;
            case R.id.lv5:
                Cont.TYPE = 5;
                textLv.setText(Cont.TYPE + " X " + Cont.TYPE);
                windowLv.dismiss();
                break;
        }
    }
}
