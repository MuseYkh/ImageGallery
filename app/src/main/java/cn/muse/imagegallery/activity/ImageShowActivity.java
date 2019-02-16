package cn.muse.imagegallery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.muse.imagegallery.R;
import cn.muse.imagegallery.photoview.PhotoView;
import cn.muse.imagegallery.util.ImgHandler;


/**
 * author: muse
 * created on: 2019/2/15 下午3:07
 * description:
 */
public class ImageShowActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private List<PhotoView> photoViewList;
    private List<String> imgPathList;
    private int selectIndex;

    private ViewPager vpPhotoPager;
    private TextView tvNumIndicator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_show);

        initData();
        initView();
    }

    private void initData() {
        photoViewList = new ArrayList<>();
        Intent intent = getIntent();
        imgPathList = intent.getStringArrayListExtra("imgPathList");
        if (imgPathList == null) {
            imgPathList = new ArrayList<>();
        }
        selectIndex = intent.getIntExtra("selectIndex", 0);
    }

    private void initView() {
        vpPhotoPager = (ViewPager) findViewById(R.id.vp_photo_pager);
        tvNumIndicator = (TextView) findViewById(R.id.tv_num_indicator);

        // 填充photoView
        for (int i = 0; i < imgPathList.size(); i++) {
            PhotoView photoView = new PhotoView(this);
            photoViewList.add(photoView);
            ImgHandler.showImageNoTrans(this, imgPathList.get(i), photoView);
        }
        initViewPager();
        tvNumIndicator.setText((selectIndex + 1) + "/" + imgPathList.size());
    }

    private void initViewPager() {
        PhotoPagerAdapter photoPagerAdapter = new PhotoPagerAdapter();
        vpPhotoPager.addOnPageChangeListener(this);
        vpPhotoPager.setAdapter(photoPagerAdapter);
        vpPhotoPager.setFocusable(true);
        vpPhotoPager.setCurrentItem(selectIndex);
    }

    private void saveImage(final ImageView view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("是否保存当前图片")
                .setPositiveButton("是", (dialog, which) -> {
                    // 此处要先获取写入权限
                    String result = ImgHandler.saveImageViewToPhotoAlbum(ImageShowActivity.this, view);
                    Toast.makeText(ImageShowActivity.this, result, Toast.LENGTH_SHORT).show();
                });
        dialogBuilder.create().show();
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int position) {
        selectIndex = position;
        tvNumIndicator.setText((selectIndex + 1) + "/" + imgPathList.size());
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    private class PhotoPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return photoViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView view = photoViewList.get(position);
            view.setOnClickListener(v -> {
                finish();
            });
            view.setOnLongClickListener(v -> {
                saveImage(view);
                return true;
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
