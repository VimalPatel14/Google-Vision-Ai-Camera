/**
 * Created by Vimal on July-2021.
 */
package com.vimal.funnycam.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.vimal.funnycam.R;
import com.vimal.funnycam.activity.MainActivity;
import com.vimal.funnycam.opengl.CameraRender;

import java.util.Arrays;
import java.util.List;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> implements View.OnClickListener {

    protected final CameraRender cameraRender;
    private Context context;
    private List<String> listName;
    protected List<Bitmap> iconList;
    protected MainActivity.Callback callback;

    public ListAdapter(Context context, CameraRender cameraRender, MainActivity.Callback callback, int arrayId) {
        super();
        this.context = context;
        this.cameraRender = cameraRender;
        this.callback = callback;
        String[] stringArray = context.getResources().getStringArray(arrayId);
        listName = Arrays.asList(stringArray);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ((TextView) holder.view.findViewById(R.id.textView)).setText(listName.get(position));
        ((ImageView) holder.view.findViewById(R.id.stickerIV)).setImageBitmap(iconList.get(position));
        holder.view.setTag(position);
    }

    @Override
    public int getItemCount() {
        return listName.size();
    }

    @Override
    public void onClick(View v) {
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        View view;

        ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }
    }
}
