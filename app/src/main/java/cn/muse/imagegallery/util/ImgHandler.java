package cn.muse.imagegallery.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.muse.imagegallery.R;
import cn.muse.imagegallery.glide.GlideApp;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * author: muse
 * created on: 2018/7/16 下午1:45
 * description:
 */

public class ImgHandler {

    /**
     * 展示图片
     *
     * @param context
     * @param url
     * @param imageView
     */
    public static void showImage(Context context, String url, ImageView imageView) {
        GlideApp.with(context)
                .load(url)
                .placeholder(R.drawable.bg_default_img)
                .dontAnimate()
//                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    /**
     * 展示高斯模糊图片
     *
     * @param context
     * @param url
     * @param imageView
     * @param radius
     */
    public static void showBlurImage(Context context, String url, ImageView imageView, int radius) {
        GlideApp.with(context)
                .load(url)
                .placeholder(R.drawable.bg_default_img)
                .transform(new BlurTransformation(radius))
                .into(imageView);
    }

    /**
     * 无动画展示图片(可用于解决某些时候图片显示出现跳动与第一次不显示的问题)
     *
     * @param context
     * @param url
     * @param imageView
     */
    public static void showImageNoTrans(Context context, String url, ImageView imageView) {
        GlideApp.with(context)
                .load(url)
                .placeholder(R.drawable.bg_default_img)
                .dontAnimate()
                .into(imageView);
    }

    /**
     * 保存ImageView到系统相册
     *
     * @param context
     * @param imageView
     * @return 语义化保存结果
     */
    public static String saveImageViewToPhotoAlbum(Context context, ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        return saveBitmapToPhotoAlbum(context, ((BitmapDrawable)drawable).getBitmap());
    }

    /**
     * 保存位图到系统相册
     *
     * @param context
     * @param bitmap
     * @return 语义化保存结果
     */
    public static String saveBitmapToPhotoAlbum(Context context, Bitmap bitmap) {
        String result = "";
        if (bitmap == null) {
            return "需保存的图片不存在";
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        String filePath;
        File file;
        if (Build.BRAND.equals("Xiaomi")) { // 小米手机
            filePath = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/" + fileName;
        } else {  // Meizu 、Oppo
            filePath = Environment.getExternalStorageDirectory().getPath() + "/DCIM/" + fileName;
        }
        file = new File(filePath);

        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
                // 插入图库
                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
                result = "保存成功，当前保存路径为：" + filePath;
            }
        } catch (IOException e) {
            result = "保存失败，请稍后再试！";
            e.printStackTrace();
        }
        // 发送广播，通知刷新图库的显示
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath)));
        return result;
    }

    /**
     * bitmap 转换为字节数组(暂时用于微信缩略图转换)
     *
     * @param bitmap
     * @param needRecycle
     * @return
     */
    public static byte[] getBitmapBytes(final Bitmap bitmap, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bitmap.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
