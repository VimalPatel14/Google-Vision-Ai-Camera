/**
 * Created by Vimal on July-2021.
 */
package com.vimal.funnycam.main;

import android.content.Context;
import android.view.View;


import com.vimal.funnycam.R;
import com.vimal.funnycam.activity.MainActivity;
import com.vimal.funnycam.opengl.CameraRender;

import java.util.LinkedList;

public class FilterListAdapter extends ListAdapter {

    public FilterListAdapter(Context context, CameraRender cameraRender, MainActivity.Callback callback) {
        super(context, cameraRender, callback, R.array.filter);
        iconList = new LinkedList<>();
        iconList.add(null);
        iconList.add(null);
        iconList.add(null);
        iconList.add(null);
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        cameraRender.selectFilter(position);
        callback.listVanish();
    }
}
