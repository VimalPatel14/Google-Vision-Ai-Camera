/**
 * Created by Vimal on July-2021.
 */
package com.vimal.funnycam.opengl;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import com.google.gson.Gson;
import com.vimal.funnycam.activity.MainActivity;
import com.vimal.funnycam.camera.Camera;
import com.vimal.funnycam.models.JsonSticker;
import com.vimal.funnycam.opengl.filter.AsciiFilterRender;
import com.vimal.funnycam.opengl.filter.FilterRender;
import com.vimal.funnycam.opengl.filter.GrayFilterRender;
import com.vimal.funnycam.opengl.filter.OriginalFilterRender;
import com.vimal.funnycam.opengl.filter.ReliefFilterRender;
import com.vimal.funnycam.opengl.sticker.GlassesStickerRender;
import com.vimal.funnycam.opengl.sticker.MoustacheStickerRender;
import com.vimal.funnycam.opengl.sticker.StickerRender;
import com.vimal.funnycam.opengl.utils.TextureHelper;
import com.vimal.funnycam.utils.GlassJson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.orthoM;
import static com.vimal.funnycam.camera.Camera.RAW_HEIGHT;
import static com.vimal.funnycam.camera.Camera.RAW_WIDTH;


public class CameraRender implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private static final String TAG = "CameraRender";

    private final Context context;
    private final MainActivity.Callback callback;
    private int imageId;
    private Texture texture;
    private volatile FilterRender filterRender;
    private volatile StickerRender stickerRender = null;
    private SurfaceTexture cameraTexture;
    private GLSurfaceView glSV;
    public static ArrayList<StickerRender> stickerRenderList = new ArrayList<>();
    private List<FilterRender> filterRenderList = new LinkedList<>();
    int sticker_selected_pos = 0;

    private volatile Camera camera;
    private final float[] projectionMatrix = new float[16];

    private volatile Bitmap bitmap;
    private volatile boolean isTakePicture = false;
    private int previewWidth;
    private int previewHeight;
    String assetsname = "";
    ArrayList<JsonSticker> list;

    public CameraRender(Context context, GLSurfaceView glSV, MainActivity.Callback callback, ArrayList<JsonSticker> list) {
        this.context = context;
        this.glSV = glSV;
        this.callback = callback;
        this.list = list;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        Log.e("cam_vml", "onSurfaceCreated");

        filterRenderList.add(new OriginalFilterRender(context));
        filterRenderList.add(new GrayFilterRender(context));
        filterRenderList.add(new ReliefFilterRender(context));
        filterRenderList.add(new AsciiFilterRender(context));

        CreateArray(context);
        glClearColor(0f, 0f, 0f, 1f);

        texture = new Texture();
        filterRender = filterRenderList.get(0);
//        stickerRender = stickerRenderList.get(0);
//        sticker_selected_pos = 0;
        imageId = TextureHelper.genTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);

        cameraTexture = new SurfaceTexture(imageId);
        cameraTexture.setOnFrameAvailableListener(this);

        camera = new Camera(context);
        camera.setSurfaceTexture(cameraTexture);
        camera.openCamera();
    }

    public void CreateArray(Context context) {
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                stickerRenderList.add(null);
            } else {
                StickerRender stickerRender = null;
                File file = new File(Environment.getExternalStorageDirectory() + "/vimal_ar_stickers/" + list.get(i).getPath() + "/data.json");
                if (file.exists()) {
                    List<GlassJson> vml = arrayreturn(context, Environment.getExternalStorageDirectory() + "/vimal_ar_stickers/" + list.get(i).getPath() + "/data.json");
                    if (vml.get(0).getType().equals("GlasseSticker")) {
                        stickerRender = new GlassesStickerRender(context, Environment.getExternalStorageDirectory() + "/vimal_ar_stickers/" + list.get(i).getPath() + "/image.png");
                    } else {
                        stickerRender = new MoustacheStickerRender(context, Environment.getExternalStorageDirectory() + "/vimal_ar_stickers/" + "/" + list.get(i).getPath() + "/image.png");
                    }
                    stickerRenderList.add(stickerRender);
                } else {
                    stickerRenderList.add(null);
                }


            }

        }
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


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        this.previewWidth = width;
        this.previewHeight = height;
        glViewport(0, 0, width, height);

        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;
        if (width > height) {
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        glClear(GL_COLOR_BUFFER_BIT);
        FilterRender filterRender = this.filterRender;
        StickerRender stickerRender = this.stickerRender;
        drawImage(filterRender);

        synchronized (camera.LOCK) {
            if (camera.getFaces() == null) {
                try {
                    camera.LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            drawSticker(stickerRender);
            camera.cleanFaces();
        }

        cameraTexture.updateTexImage();

        if (isTakePicture) {
            bitmap = createBitmapFromGLSurface(0, 0, previewWidth, previewHeight);
            callback.setImageView(bitmap);
            camera.close();
            isTakePicture = false;
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        glSV.requestRender();
    }

    public void selectFilter(int position) {
        filterRender = filterRenderList.get(position);
    }

    public void selectSticker(int position, String assetsname) {
        sticker_selected_pos = position;
        this.assetsname = assetsname;
//        StickerRender chk = stickerRenderList.get(position);
        Log.e("vml", position + " pos");
        if (stickerRenderList.get(position) != null) {
            Log.e("vml", "StickerRender_yes" + stickerRenderList.get(position));
            stickerRender = stickerRenderList.get(position);
        } else {
            Log.e("vml", "StickerRender_null " + position);
            StickerRender stickerRendervml = null;
            File file = new File(Environment.getExternalStorageDirectory() + "/vimal_ar_stickers/" + list.get(position).getPath() + "/data.json");
            if (file.exists()) {
                List<GlassJson> vml = arrayreturn(context, Environment.getExternalStorageDirectory() + "/vimal_ar_stickers/" + list.get(position).getPath() + "/data.json");
                Log.e("vml", vml + " json");
                if (vml.get(0).getType().equals("GlasseSticker")) {
                    Log.e("vml", Environment.getExternalStorageDirectory() + "/vimal_ar_stickers/" + list.get(position).getPath() + "/image.png");
                    stickerRendervml = new GlassesStickerRender(context, Environment.getExternalStorageDirectory() + "/vimal_ar_stickers/" + list.get(position).getPath() + "/image.png");
                } else {
                    stickerRendervml = new MoustacheStickerRender(context, Environment.getExternalStorageDirectory() + "/vimal_ar_stickers/" + "/" + list.get(position).getPath() + "/image.png");
                }

                drawSticker(stickerRendervml);

                Log.e("vml", "Set_Stickers");
//                }else {
//                    Log.e("vml", "instanceof_Not_Found");
//                }

            } else {
                Log.e("vml", "File_Not_Found");
            }
            Log.e("vml", "Update_In_Array");
//            stickerRenderList.set(position, stickerRender);
        }
    }

    public void changCamera() {
        camera.changeCamera();
    }

    public void openCamera() {
        camera.openCamera();
    }

    public void takePicture() {
        isTakePicture = true;
    }

    public void savePicture() {
        saveBitmap(bitmap);
    }

    private void saveBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            callback.showMessage("Picture is empty");
            return;
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Me" + File.separator + System.currentTimeMillis() + "image.jpg");
        try {
            boolean fileCreateSuccessful = file.createNewFile();
            if (!fileCreateSuccessful) {
                callback.showMessage("Picture creation failed");
                return;
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes.toByteArray());
            fileOutputStream.flush();
            fileOutputStream.close();
            bytes.flush();
            bytes.close();
            bitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap createBitmapFromGLSurface(int x, int y, int w, int h) {
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);
        try {
            GLES20.glReadPixels(x, y, w, h, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            e.printStackTrace();
            return null;
        }
        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }

    private void drawImage(FilterRender filterRender) {

        filterRender.useProgram();
        filterRender.bindTexture(imageId, projectionMatrix);
        filterRender.changeCameraDirection(camera);
        texture.bindData(this.filterRender);
        texture.draw();
    }

    public List<GlassJson> arrayreturn(String filename) {
//        String jsonFileString = Utils.getJsonFromAssets(context, filename);

        String jsonFileString = readjson(filename);

        Gson gson = new Gson();
        List<GlassJson> postsList = Arrays.asList(gson.fromJson(jsonFileString, GlassJson.class));

        return postsList;
    }

    public void drawSticker(StickerRender stickerRender) {

        if (stickerRender == null) {
            return;
        }

        SparseArray<Face> faces = camera.getFaces();
        if (faces == null || faces.size() == 0) {
            Log.e("vml", "faces_null_return");
            return;
        }

        PointF faceCenter = null;
        PointF leftEye = null;
        PointF rightEye = null;
        PointF noseBase = null;
        float[] rotationZMatrix = new float[16];
        float[] scratch = new float[16];
        float faceWidth;
        float faceHeight;

        Face face = faces.get(faces.keyAt(0));
        if (face.getIsSmilingProbability() > 0.7f) {
//            callback.takePicture();
        }

        int cameraId = camera.getCameraId();
        int cameraState = cameraId == Camera.BACK ? 1 : -1;

        List<Landmark> landmarks = face.getLandmarks();
        for (Landmark landmark : landmarks) {
            switch (landmark.getType()) {
                case Landmark.LEFT_EYE:
                    leftEye = rawPointToRealPoint(landmark.getPosition());
                    break;
                case Landmark.RIGHT_EYE:
                    rightEye = rawPointToRealPoint(landmark.getPosition());
                    break;
                case Landmark.NOSE_BASE:
                    noseBase = rawPointToRealPoint(landmark.getPosition());
                    break;
            }
        }
        faceCenter = rawPointToRealPoint(face.getPosition());
        faceWidth = face.getWidth() / RAW_HEIGHT;
        faceHeight = face.getHeight() / RAW_HEIGHT;
        faceCenter.x = faceCenter.x - faceWidth;
        faceCenter.y = faceCenter.y - faceHeight;

        if (assetsname.equals("")) {

        } else {

            List<GlassJson> postsList = arrayreturn(assetsname);

            if (postsList.get(0).getType().equals("GlasseSticker")) {

                if (leftEye != null && rightEye != null && stickerRender instanceof GlassesStickerRender) {
                    float centerX = 0, centerY = 0;
                    float xRadius = 0, yRadius = 0;

                    xRadius = faceWidth * Float.parseFloat(postsList.get(0).getFactor());
                    yRadius = xRadius * Float.parseFloat(postsList.get(0).getHeight()) / Float.parseFloat(postsList.get(0).getWidth());
                    centerX = (leftEye.x + rightEye.x) / 2.0f;
                    centerY = (leftEye.y + rightEye.y) / 2.0f;

                    Matrix.setIdentityM(rotationZMatrix, 0);
                    Matrix.translateM(rotationZMatrix, 0, centerX, centerY, 0);
                    Matrix.rotateM(rotationZMatrix, 0, face.getEulerZ(), 0, 0, cameraState);
                    Matrix.translateM(rotationZMatrix, 0, -centerX, -centerY, 0);
                    Matrix.multiplyMM(scratch, 0, projectionMatrix, 0, rotationZMatrix, 0);

                    stickerRender.setPosition(new float[]{centerX, centerY}, xRadius, yRadius);
                }


            } else {

                if (noseBase != null && stickerRender instanceof MoustacheStickerRender) {
                    float centerX = 0, centerY = 0;
                    float xRadius = 0, yRadius = 0;

                    xRadius = faceWidth;
                    yRadius = xRadius * Float.parseFloat(postsList.get(0).getHeight()) / Float.parseFloat(postsList.get(0).getWidth());
                    centerX = noseBase.x;
                    centerY = noseBase.y;

                    Matrix.setIdentityM(rotationZMatrix, 0);
                    Matrix.translateM(rotationZMatrix, 0, centerX, centerY, 0);
                    Matrix.rotateM(rotationZMatrix, 0, face.getEulerZ(), 0, 0, cameraState);
                    Matrix.translateM(rotationZMatrix, 0, -centerX, -centerY, 0);
                    Matrix.multiplyMM(scratch, 0, projectionMatrix, 0, rotationZMatrix, 0);

                    stickerRender.setPosition(new float[]{centerX, centerY}, xRadius, yRadius);
                }
            }

            stickerRender.useProgram();
            stickerRender.setMatrix(scratch);
            stickerRender.bindTexture();

            texture.bindData(stickerRender);
            texture.draw();
        }

//        if (leftEye != null && rightEye != null && stickerRender instanceof GlassesStickerRender) {
//            float centerX = 0, centerY = 0;
//            float xRadius = 0, yRadius = 0;
//            if (sticker_selected_pos == 1) {
//
//                String jsonFileString = Utils.getJsonFromAssets(context, "glass/data.json");
//                Gson gson = new Gson();
//                List<GlassJson> postsList = Arrays.asList(gson.fromJson(jsonFileString, GlassJson.class));
//
//                xRadius = faceWidth * Float.parseFloat(postsList.get(0).getFactor());
//                yRadius = xRadius * Float.parseFloat(postsList.get(0).getHeight()) / Float.parseFloat(postsList.get(0).getWidth());
//                centerX = (leftEye.x + rightEye.x) / 2.0f;
//                centerY = (leftEye.y + rightEye.y) / 2.0f;
//            } else if (sticker_selected_pos == 2) {
//
//                String jsonFileString = Utils.getJsonFromAssets(context, "glass_2/data.json");
//                Gson gson = new Gson();
//                List<GlassJson> postsList = Arrays.asList(gson.fromJson(jsonFileString, GlassJson.class));
//
//                xRadius = faceWidth * Float.parseFloat(postsList.get(0).getFactor());
//                yRadius = xRadius * Float.parseFloat(postsList.get(0).getHeight()) / Float.parseFloat(postsList.get(0).getWidth());
//                centerX = (leftEye.x + rightEye.x) / 2.0f;
//                centerY = (leftEye.y + rightEye.y) / 2.0f;
//
//            } else if (sticker_selected_pos == 3) {
//
//                String jsonFileString = Utils.getJsonFromAssets(context, "glass_3/data.json");
//                Gson gson = new Gson();
//                List<GlassJson> postsList = Arrays.asList(gson.fromJson(jsonFileString, GlassJson.class));
//
//                xRadius = faceWidth * Float.parseFloat(postsList.get(0).getFactor());
//                yRadius = xRadius * Float.parseFloat(postsList.get(0).getHeight()) / Float.parseFloat(postsList.get(0).getWidth());
//                centerX = (leftEye.x + rightEye.x) / 2.0f;
//                centerY = (leftEye.y + rightEye.y) / 2.0f;
//
//            } else if (sticker_selected_pos == 4) {
//
//                String jsonFileString = Utils.getJsonFromAssets(context, "glass_4/data.json");
//                Gson gson = new Gson();
//                List<GlassJson> postsList = Arrays.asList(gson.fromJson(jsonFileString, GlassJson.class));
//
//                xRadius = faceWidth * Float.parseFloat(postsList.get(0).getFactor());
//                yRadius = xRadius * Float.parseFloat(postsList.get(0).getHeight()) / Float.parseFloat(postsList.get(0).getWidth());
//                centerX = (leftEye.x + rightEye.x) / 2.0f;
//                centerY = (leftEye.y + rightEye.y) / 2.0f;
//
//            } else if (sticker_selected_pos == 5) {
//
//                String jsonFileString = Utils.getJsonFromAssets(context, "glass_5/data.json");
//                Gson gson = new Gson();
//                List<GlassJson> postsList = Arrays.asList(gson.fromJson(jsonFileString, GlassJson.class));
//
//                xRadius = faceWidth * Float.parseFloat(postsList.get(0).getFactor());
//                yRadius = xRadius * Float.parseFloat(postsList.get(0).getHeight()) / Float.parseFloat(postsList.get(0).getWidth());
//                centerX = (leftEye.x + rightEye.x) / 2.0f;
//                centerY = (leftEye.y + rightEye.y) / 2.0f;
//
//            } else if (sticker_selected_pos == 7) {
//                xRadius = faceWidth * 1.01f;
//                yRadius = xRadius * 635 / 506;
//                centerX = (leftEye.x + rightEye.x) / 2.0f;
//                centerY = (leftEye.y + rightEye.y) / 2.0f;
//            } else if (sticker_selected_pos == 8) {
//                xRadius = faceWidth * 1.21f;
//                yRadius = xRadius * 609 / 529;
//                centerX = (leftEye.x + rightEye.x) / 2.0f;
//                centerY = (leftEye.y + rightEye.y) / 2.0f;
//            } else if (sticker_selected_pos == 9) {
//                xRadius = faceWidth * 1.83f;
//                yRadius = xRadius * 729 / 784;
//                centerX = (leftEye.x + rightEye.x) / 2.0f;
//                centerY = (leftEye.y + rightEye.y) / 2.0f;
//            } else if (sticker_selected_pos == 10) {
//                xRadius = faceWidth * 0.95f;
//                yRadius = xRadius * 500 / 500;
//                centerX = (leftEye.x + rightEye.x) / 2.0f;
//                centerY = (leftEye.y + rightEye.y) / 2.0f;
//            } else if (sticker_selected_pos == 11) {
//                xRadius = faceWidth * 1.3f;
//                yRadius = xRadius * 1.5f;
//                centerX = (leftEye.x + rightEye.x) / 2.5f;
//                centerY = (leftEye.y + rightEye.y) / 2.5f;
//            }
//            if (sticker_selected_pos == 12) {
//                xRadius = faceWidth * 1.5f;
//                yRadius = xRadius * 256 / 256;
//                centerX = (leftEye.x + rightEye.x) / 2.0f;
//                centerY = (leftEye.y + rightEye.y) / 2.0f;
//            } else if (sticker_selected_pos == 13) {
//                xRadius = faceWidth * 1.5f;
//                yRadius = xRadius * 256 / 256;
//                centerX = (leftEye.x + rightEye.x) / 2.0f;
//                centerY = (leftEye.y + rightEye.y) / 2.0f;
//            } else if (sticker_selected_pos == 14) {
//                xRadius = faceWidth * 1.5f;
//                yRadius = xRadius * 256 / 256;
//                centerX = (leftEye.x + rightEye.x) / 2.0f;
//                centerY = (leftEye.y + rightEye.y) / 2.0f;
//            } else if (sticker_selected_pos == 15) {
//                xRadius = faceWidth * 1.05f;
//                yRadius = xRadius * 540 / 540;
//                centerX = (leftEye.x + rightEye.x) / 2.0f;
//                centerY = (leftEye.y + rightEye.y) / 2.0f;
//            } else if (sticker_selected_pos == 16) {
//                xRadius = faceWidth * 0.93f;
//                yRadius = xRadius * 300 / 540;
//                centerX = (leftEye.x + rightEye.x) / 2.0f;
//                centerY = (leftEye.y + rightEye.y) / 2.2f;
//            } else if (sticker_selected_pos == 17) {
//                xRadius = faceWidth * 2.2f;
//                yRadius = xRadius * 200 / 200;
//                centerX = (leftEye.x + rightEye.x) / 2.0f;
//                centerY = (leftEye.y + rightEye.y) / 2.0f;
//            } else if (sticker_selected_pos == 18) {
//                xRadius = faceWidth * 1.8f;
//                yRadius = xRadius * 200 / 200;
//                centerX = (leftEye.x + rightEye.x) / 2.0f;
//                centerY = (leftEye.y + rightEye.y) / 2.0f;
//            } else if (sticker_selected_pos == 19) {
//                xRadius = faceWidth * 1.0f;
//                yRadius = xRadius * 200 / 200;
//                centerX = (leftEye.x + rightEye.x) / 2.0f;
//                centerY = (leftEye.y + rightEye.y) / 2.0f;
//            } else if (sticker_selected_pos == 20) {
//                xRadius = faceWidth * 1.3f;
//                yRadius = xRadius * 200 / 200;
//                centerX = (leftEye.x + rightEye.x) / 2.0f;
//                centerY = (leftEye.y + rightEye.y) / 2.0f;
//            } else if (sticker_selected_pos == 21) {
//                xRadius = faceWidth * 1.11f;
//                yRadius = xRadius * 200 / 200;
//                centerX = (leftEye.x + rightEye.x) / 2.0f;
//                centerY = (leftEye.y + rightEye.y) / 2.0f;
//            }
//
//
//            Matrix.setIdentityM(rotationZMatrix, 0);
//            Matrix.translateM(rotationZMatrix, 0, centerX, centerY, 0);
//            Matrix.rotateM(rotationZMatrix, 0, face.getEulerZ(), 0, 0, cameraState);
//            Matrix.translateM(rotationZMatrix, 0, -centerX, -centerY, 0);
//            Matrix.multiplyMM(scratch, 0, projectionMatrix, 0, rotationZMatrix, 0);
//
//            stickerRender.setPosition(new float[]{centerX, centerY}, xRadius, yRadius);
//        }
//
//        if (noseBase != null && stickerRender instanceof MoustacheStickerRender) {
//            float centerX = 0, centerY = 0;
//            float xRadius = 0, yRadius = 0;
//            if (sticker_selected_pos == 6) {
//                xRadius = faceWidth;
//                yRadius = xRadius * 250 / 492;
//                centerX = noseBase.x;
//                centerY = noseBase.y;
//            }
//
//
//            Matrix.setIdentityM(rotationZMatrix, 0);
//            Matrix.translateM(rotationZMatrix, 0, centerX, centerY, 0);
//            Matrix.rotateM(rotationZMatrix, 0, face.getEulerZ(), 0, 0, cameraState);
//            Matrix.translateM(rotationZMatrix, 0, -centerX, -centerY, 0);
//            Matrix.multiplyMM(scratch, 0, projectionMatrix, 0, rotationZMatrix, 0);
//
//            stickerRender.setPosition(new float[]{centerX, centerY}, xRadius, yRadius);
//        }
//
//        stickerRender.useProgram();
//        stickerRender.setMatrix(scratch);
//        stickerRender.bindTexture();
//
//        texture.bindData(stickerRender);
//        texture.draw();

    }

    int cnt = 0;


    private PointF rawPointToRealPoint(PointF rawPoint) {
        int cameraId = camera.getCameraId();
        PointF result = null;
        if (cameraId == Camera.FRONT) {
            result = new PointF(
                    1 - rawPoint.x / RAW_HEIGHT * 2.0f,
                    1.0f * RAW_WIDTH / RAW_HEIGHT - rawPoint.y / RAW_WIDTH * 2.0f * RAW_WIDTH / RAW_HEIGHT
            );
        } else if (cameraId == Camera.BACK) {
            result = new PointF(
                    rawPoint.x / RAW_HEIGHT * 2.0f - 1,
                    1.0f * RAW_WIDTH / RAW_HEIGHT - rawPoint.y / RAW_WIDTH * 2.0f * RAW_WIDTH / RAW_HEIGHT
            );
        }
        return result;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }


}