/**
 * Created by Vimal on July-2021.
 */
package com.vimal.funnycam.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.vimal.funnycam.R;
import com.vimal.funnycam.models.JsonSticker;
import com.vimal.funnycam.opengl.CameraRender;
import com.vimal.funnycam.opengl.sticker.GlassesStickerRender;
import com.vimal.funnycam.opengl.sticker.MoustacheStickerRender;
import com.vimal.funnycam.opengl.sticker.StickerRender;
import com.vimal.funnycam.utils.GlassJson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.MyView> {

    Context context;
    public static ArrayList<JsonSticker> list;
    protected final CameraRender cameraRender;

    public class MyView extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView stickerIV;
        RelativeLayout mainlay;

        public MyView(View view) {
            super(view);

            mainlay = view.findViewById(R.id.mainlay);
            textView = view.findViewById(R.id.textView);
            stickerIV = view.findViewById(R.id.stickerIV);
        }
    }


    public StickerAdapter(Context context, ArrayList<JsonSticker> list, CameraRender cameraRender) {
        this.context = context;
        this.list = list;
        this.cameraRender = cameraRender;

    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new MyView(itemView);
    }


    @Override
    public void onBindViewHolder(final MyView holder, final int position) {

        Glide.with(context).load(list.get(position).getImage()).placeholder(R.drawable.ic_launcher_background).into(holder.stickerIV);
        holder.textView.setText(list.get(position).getName());

        holder.mainlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StickerRender stickerRendervml = null;
                File file = new File(Environment.getExternalStorageDirectory() + "/vimal_ar_stickers/" + list.get(position).getPath() + "/data.json");

                    List<GlassJson> vml = arrayreturn(context, Environment.getExternalStorageDirectory() + "/vimal_ar_stickers/" + list.get(position).getPath() + "/data.json");
                    Log.e("vml", vml + " json");
                    if (vml.get(0).getType().equals("GlasseSticker")) {
                        Log.e("vml", Environment.getExternalStorageDirectory() + "/vimal_ar_stickers/" + list.get(position).getPath() + "/image.png");
                        stickerRendervml = new GlassesStickerRender(context, Environment.getExternalStorageDirectory() + "/vimal_ar_stickers/" + list.get(position).getPath() + "/image.png");
                    } else {
                        stickerRendervml = new MoustacheStickerRender(context, Environment.getExternalStorageDirectory() + "/vimal_ar_stickers/" + "/" + list.get(position).getPath() + "/image.png");
                    }

//                cameraRender.drawSticker(stickerRendervml);
                cameraRender.selectSticker(position, Environment.getExternalStorageDirectory() + "/vimal_ar_stickers/" + list.get(position).getPath() + "/data.json");

            }
        });
    }

    public List<GlassJson> arrayreturn(Context context, String filename) {
//        String jsonFileString = Utils.getJsonFromAssets(context, filename);
        String jsonFileString = readjson(filename);
        Gson gson = new Gson();
        List<GlassJson> postsList = Arrays.asList(gson.fromJson(jsonFileString, GlassJson.class));

        return postsList;
    }

    public String readjson(String path) {

        File yourFile = new File(path);
        FileInputStream stream = null;
        String jString = "";
        try {
            stream = new FileInputStream(yourFile);
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            /* Instead of using default, pass in a decoder. */
            jString = Charset.defaultCharset().decode(bb).toString();
        } catch (FileNotFoundException e) {
            jString = "";
            e.printStackTrace();
        } catch (IOException e) {
            jString = "";
            e.printStackTrace();
        }
        return jString;
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


    @Override
    public int getItemCount() {
        return list.size();
    }
}
