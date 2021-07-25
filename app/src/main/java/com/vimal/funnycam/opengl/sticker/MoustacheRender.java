/**
 * Created by Vimal on July-2021.
 */
package com.vimal.funnycam.opengl.sticker;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.vimal.funnycam.opengl.utils.TextureHelper;

import java.io.IOException;
import java.io.InputStream;

public class MoustacheRender extends StickerRender {

    private final Context context;
    Bitmap bitmap;

    public MoustacheRender(Context context, Bitmap bitmap) {
        super(context);
        this.context = context;
        this.bitmap = bitmap;
    }

    public void bindTexture() {
        int textureId = TextureHelper.loadTexture(bitmap);
        bindTexture(textureId);
    }


}
