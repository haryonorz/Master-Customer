package com.example.mastercustomer.utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.mastercustomer.R;

import java.io.IOException;

public class ImageUtils {

    public static void setImage(Activity activity, Context context, View loadingView, ImageView photoImageView, String photoOutlet){
        RequestOptions requestOptions = new RequestOptions().error(R.drawable.no_image);
        if(photoOutlet != null){
            if(!activity.isFinishing())
                Glide.with(context).load("http://116.197.133.126/evamobile/images/outlet" +
                        photoOutlet).apply(requestOptions).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target,
                                                boolean isFirstResource) {
                        if (loadingView != null)
                            loadingView.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                   DataSource dataSource, boolean isFirstResource) {
                        if (loadingView != null)
                            loadingView.setVisibility(View.GONE);
                        return false;
                    }
                }).into(photoImageView);
        } else {
            photoImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.no_image));
        }
    }

    public static Bitmap rotateImage(String pathFile){
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathFile, bounds);

        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(pathFile, opts);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(pathFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) :  ExifInterface.ORIENTATION_NORMAL;

        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        return Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
    }
}
