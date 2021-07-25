/**
 * Created by Vimal on July-2021.
 */
package com.vimal.funnycam.activity;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.vision.face.Face;
import com.vimal.funnycam.R;
import com.vimal.funnycam.adapter.StickerAdapter;
import com.vimal.funnycam.main.FilterListAdapter;
import com.vimal.funnycam.main.utils.AnimatorHelper;
import com.vimal.funnycam.main.utils.PermissionHelper;
import com.vimal.funnycam.models.JsonSticker;
import com.vimal.funnycam.opengl.CameraRender;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private CameraRender cameraRender;
    Button changeCameraBtn;
    Button stickerBtn, takePictureBtn, filterBtn;
    RecyclerView stickerRV, filterRV;
    GLSurfaceView glSV;
    ImageView imageView;
    ArrayList<JsonSticker> stickerjson;
    StickerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stickerjson = new ArrayList<JsonSticker>();
        stickerjson.add(new JsonSticker("no", null, null));
        stickerjson.add(new JsonSticker("glasses 1", R.drawable.glasses, "glass"));
        stickerjson.add(new JsonSticker("glasses 2", R.drawable.glasses_2, "glass_2"));
        stickerjson.add(new JsonSticker("glasses 3", R.drawable.glasses_3, "glass_3"));
        stickerjson.add(new JsonSticker("glasses 4", R.drawable.glasses_4, "glass_4"));
        stickerjson.add(new JsonSticker("glasses 5", R.drawable.glasses_5, "glass_5"));
        stickerjson.add(new JsonSticker("glasses 5", R.drawable.glass_6, "glass_6"));
        stickerjson.add(new JsonSticker("glasses 5", R.drawable.glass_7, "glass_7"));

        stickerjson.add(new JsonSticker("beard", R.drawable.moustache, "moustache"));
        stickerjson.add(new JsonSticker("cat", R.drawable.moustache_2, "moustache_2"));
        stickerjson.add(new JsonSticker("dog", R.drawable.moustache_3, "moustache_3"));
        stickerjson.add(new JsonSticker("hat", R.drawable.mustache_4, "moustache_4"));
        stickerjson.add(new JsonSticker("Mouse", R.drawable.mustache_5, "moustache_5"));
        stickerjson.add(new JsonSticker("Flower", R.drawable.stars1, "moustache_6"));

        stickerjson.add(new JsonSticker("Face 1", R.drawable.mask, "mask"));
        stickerjson.add(new JsonSticker("Face 2", R.drawable.mask_2, "mask_2"));
        stickerjson.add(new JsonSticker("Face 3", R.drawable.mask_3, "mask_3"));
        stickerjson.add(new JsonSticker("Face 4", R.drawable.mask_4, "mask_4"));
        stickerjson.add(new JsonSticker("Face 5", R.drawable.mask_5, "mask_5"));

        stickerjson.add(new JsonSticker("Snap 1", R.drawable.snap_1, "snap"));
        stickerjson.add(new JsonSticker("Snap 2", R.drawable.snap_2, "snap_2"));
        stickerjson.add(new JsonSticker("Snap 3", R.drawable.snap_3, "snap_3"));
        stickerjson.add(new JsonSticker("Snap 4", R.drawable.snap_4, "snap_4"));
        stickerjson.add(new JsonSticker("Snap 5", R.drawable.snap_5, "snap_5"));

//        CreateArray(MainActivity.this);

        FindViewbyid();
        initCamera();
        initList();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (filterRV.getVisibility() == View.VISIBLE) {
            AnimatorHelper.buttonEmerge(filterBtn, stickerBtn, takePictureBtn);
            AnimatorHelper.listVanish(filterRV);
        } else if (stickerRV.getVisibility() == View.VISIBLE) {
            AnimatorHelper.buttonEmerge(filterBtn, stickerBtn, takePictureBtn);
            AnimatorHelper.listVanish(stickerRV);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (PermissionHelper.checkRequest(requestCode, grantResults)) {
            initCameraRender();
        }
    }

    public interface Callback {

        void listVanish();

//        void takePicture();

        void showMessage(String info);

        void setImageView(Bitmap bitmap);
    }

    public void FindViewbyid() {

        changeCameraBtn = findViewById(R.id.changeCameraBtn);
        stickerBtn = findViewById(R.id.stickerBtn);
        takePictureBtn = findViewById(R.id.takePictureBtn);
        filterBtn = findViewById(R.id.filterBtn);
        filterRV = findViewById(R.id.filterRV);
        stickerRV = findViewById(R.id.stickRV);
        glSV = findViewById(R.id.glSV);
        imageView = findViewById(R.id.imageView);

        changeCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraRender.changCamera();
            }
        });


        takePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        stickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorHelper.buttonVanish(filterBtn, stickerBtn, takePictureBtn);
                AnimatorHelper.listEmerge(stickerRV);
            }
        });

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorHelper.buttonVanish(filterBtn, stickerBtn, takePictureBtn);
                AnimatorHelper.listEmerge(filterRV);
            }
        });
    }

    private void initCamera() {
        if (!PermissionHelper.checkPermissions(this)) {
            PermissionHelper.requestPermissions(this);
        } else {
            initCameraRender();
        }
    }

    private void initList() {
        GridLayoutManager filterGridLayoutManager = new GridLayoutManager(this, 1);
        filterGridLayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        filterRV.setLayoutManager(filterGridLayoutManager);
        filterRV.setAdapter(new FilterListAdapter(this, cameraRender, listVanish));

        GridLayoutManager stickerGridLayoutManager = new GridLayoutManager(this, 1);
        stickerGridLayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        stickerRV.setLayoutManager(stickerGridLayoutManager);
        adapter = new StickerAdapter(MainActivity.this, stickerjson, cameraRender);
        stickerRV.setAdapter(adapter);

//        stickerRV.setAdapter(new StickerListAdapter(this, cameraRender, listVanish));
    }


    private void initCameraRender() {
        glSV.setEGLContextClientVersion(2);
        cameraRender = new CameraRender(this, glSV, new Callback() {

            @Override
            public void listVanish() {

            }

//            @Override
//            public void takePicture() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        takePicture();
//                    }
//                });
//            }

            @Override
            public void showMessage(final String info) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, info, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void setImageView(final Bitmap bitmap) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                        imageView.setVisibility(View.VISIBLE);
                    }
                });
            }
        },stickerjson);
        glSV.setRenderer(cameraRender);
        glSV.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private void changeBtnStateToPicture() {
        stickerBtn.setBackgroundResource(R.drawable.ic_left_arrow);
        takePictureBtn.setBackgroundResource(R.drawable.ic_download);
        filterBtn.setBackgroundResource(R.drawable.ic_share);
        stickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setVisibility(View.INVISIBLE);
                changeBtnStateToPreview();
                cameraRender.openCamera();
            }
        });
        takePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setVisibility(View.INVISIBLE);
                changeBtnStateToPreview();
                cameraRender.savePicture();
                cameraRender.openCamera();
            }
        });
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                WeChatShareUtil.sharePhotoTo(MainActivity.this, cameraRender.getBitmap());
            }
        });
    }

    private void changeBtnStateToPreview() {
        stickerBtn.setBackgroundResource(R.drawable.ic_sentiment);
        takePictureBtn.setBackgroundResource(R.drawable.ic_ring);
        filterBtn.setBackgroundResource(R.drawable.ic_blur);
        stickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSticker();
            }
        });
        takePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilter();
            }
        });
    }

    private Callback listVanish = new Callback() {
        @Override
        public void listVanish() {
            if (filterRV.getVisibility() == View.VISIBLE) {
//                AnimatorHelper.buttonEmerge(filterBtn, stickerBtn, takePictureBtn);
//                AnimatorHelper.listVanish(filterRV);
            } else if (stickerRV.getVisibility() == View.VISIBLE) {
//                AnimatorHelper.buttonEmerge(filterBtn, stickerBtn, takePictureBtn);
//                AnimatorHelper.listVanish(stickerRV);
            }
        }

//        @Override
//        public void takePicture() {
//        }

        @Override
        public void showMessage(String info) {
        }

        @Override
        public void setImageView(Bitmap bitmap) {

        }
    };

    public void changeCamera() {
        cameraRender.changCamera();
    }

    public void takePicture() {
//        changeBtnStateToPicture();
//        cameraRender.takePicture();
    }

    public void showSticker() {
        AnimatorHelper.buttonVanish(filterBtn, stickerBtn, takePictureBtn);
        AnimatorHelper.listEmerge(stickerRV);
    }

    public void showFilter() {
        AnimatorHelper.buttonVanish(filterBtn, stickerBtn, takePictureBtn);
        AnimatorHelper.listEmerge(filterRV);
    }

    public Bitmap getBitmapFromAssets(String fileName) {
        AssetManager assetManager = getAssets();

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

}