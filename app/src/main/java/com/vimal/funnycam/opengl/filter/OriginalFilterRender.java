/**
 * Created by Vimal on July-2021.
 */
package com.vimal.funnycam.opengl.filter;

import android.content.Context;

import com.vimal.funnycam.R;

public class OriginalFilterRender extends FilterRender{

    public OriginalFilterRender(Context context) {
        super(context, R.raw.filter_vertex_shader_ori, R.raw.filter_fragment_shader_ori);
    }
}
