/**
 * Created by Vimal on July-2021.
 */
package com.vimal.funnycam.main;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;


import com.vimal.funnycam.R;
import com.vimal.funnycam.activity.MainActivity;
import com.vimal.funnycam.opengl.CameraRender;

import java.util.ArrayList;

public class StickerListAdapter extends ListAdapter {

    public StickerListAdapter(Context context, CameraRender cameraRender, MainActivity.Callback callback) {
        super(context, cameraRender, callback, R.array.sticker);
        iconList = new ArrayList<>();
        iconList.add(null);
        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.glasses, null));
        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.glasses_2, null));
        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.glasses_3, null));
        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.glasses_4, null));
        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.glasses_5, null));

        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.moustache, null));
        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.moustache_2, null));
        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.moustache_3, null));
        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.mustache_4, null));
        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.mustache_5, null));
        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.stars1, null));

        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.mask, null));
        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.mask_2, null));
        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.mask_3, null));
        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.mask_4, null));
        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.mask_5, null));

        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.snap_1, null));
        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.snap_2, null));
        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.snap_3, null));
        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.snap_4, null));
        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.snap_5, null));
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
//        cameraRender.selectSticker(position);
        callback.listVanish();
    }
}


