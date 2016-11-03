package com.sysu.sjk.view.fragment;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.sysu.sjk.base.BaseFragment;
import com.sysu.sjk.cache.ImageCache;
import com.sysu.sjk.utils.Logger;
import com.sysu.sjk.view.R;

/**
 * Created by sjk on 16-11-3.
 */
public class PictureDetailFragment extends BaseFragment {

    public static final String PICTURE_KEY = "picture_key";

    Toolbar toolbar;
    ImageView imageView;
    AlphaAnimation alphaAnimation;

    @Override
    public void onRestoreView(Bundle savedInstanceState) {
        Logger.log("Call onRestoreView(Bundle savedInstanceState)");
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_picture_detail;
    }

    @Override
    public void initView() {
        toolbar = (Toolbar) mView.findViewById(R.id.picture_detail_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        imageView = (ImageView) mView.findViewById(R.id.picture_detail_image_view);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        Bundle bundle = getActivity().getIntent().getExtras();
        String pictureUrl = bundle.getString(PICTURE_KEY);
        Logger.log("picture url: " + pictureUrl);

        alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(1000);
        imageView.setAnimation(alphaAnimation); // put the animation into the image view

        ImageCache.getInstance(getContext()).getImage(pictureUrl, new ImageCache.ImageCallback() {
            @Override
            public void onImageGet(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
                alphaAnimation.start();
            }
        });
    }

    @Override
    public void initListener() {

    }
}
