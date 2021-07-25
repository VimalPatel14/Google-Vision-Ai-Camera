/**
 * Created by Vimal on July-2021.
 */
package com.vimal.funnycam.opengl.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.vimal.funnycam.opengl.utils.TextureHelper;

public class GlassesRender extends StickerRender {

    private final Context context;
    Bitmap bitmap;

    public GlassesRender(Context context, Bitmap bitmap) {
        super(context);
        this.context = context;
        this.bitmap = bitmap;
    }

    public void bindTexture() {
        if (bitmap!=null){
            Log.e("vml","bmp");
            int textureId = TextureHelper.loadTexture(bitmap);
            bindTexture(textureId);
        }else {
            Log.e("vml","null_bmp");
        }

    }


}
