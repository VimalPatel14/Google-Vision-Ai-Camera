/**
 * Created by Vimal on July-2021.
 */
package com.vimal.funnycam.opengl.sticker;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.vimal.funnycam.opengl.utils.TextureHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class GlassesStickerRender extends StickerRender {

    private Context context;
    String stickername;

    public GlassesStickerRender(Context context, String stickername) {
        super(context);
        this.context = context;
        this.stickername = stickername;
    }

    public void bindTexture() {
        int textureId = TextureHelper.loadTexture(getBitmapFromPath(stickername));
        bindTexture(textureId);
    }

    public Bitmap getBitmapFromAssets(String fileName) {
        AssetManager assetManager = context.getAssets();

        InputStream istr = null;
        try {
            istr = assetManager.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        try {
            istr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public Bitmap getBitmapFromPath(String fileName) {

        Bitmap bitmap = null;

        File imgFile = new File(fileName);
        if (imgFile.exists()) {
            bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        return bitmap;
    }

}
