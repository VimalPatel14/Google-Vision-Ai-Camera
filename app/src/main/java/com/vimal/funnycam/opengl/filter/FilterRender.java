/**
 * Created by Vimal on July-2021.
 */
package com.vimal.funnycam.opengl.filter;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;


import com.vimal.funnycam.camera.Camera;
import com.vimal.funnycam.opengl.Shader;

import static android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static com.vimal.funnycam.camera.Camera.BACK;

public class FilterRender extends Shader {

    private final int uTextureUnitLocation;
    private final int uMatrixLocation;
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;
    private float aspectRatio = 640.0f / 480.0f;

    public FilterRender(Context context, int vertexShader, int fragmentShader) {
        super(context, vertexShader, fragmentShader);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program, S_TEXTURE);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
        setFrontCoordinate();
    }

    public void bindTexture(int textureId, float[] matrix) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureId);

        glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }


    private void setFrontCoordinate() {
        coordinates = new float[]{
                //order of coordinate:X,Y,S,T
                //Triangle fan
                //???????????????????????????????????????90???
                0f, 0f, 0.5f, 0.5f,
                -1f, -aspectRatio, 0f, 1f,
                1f, -aspectRatio, 0f, 0f,
                1f, aspectRatio, 1f, 0f,
                -1f, aspectRatio, 1f, 1f,
                -1f, -aspectRatio, 0f, 1f
        };
    }

    private void setBackCoordinate() {
        coordinates = new float[]{
                //order of coordinate:X,Y,S,T
                //Triangle fan
                //???????????????????????????????????????90?????? ???????????????????????????y=x??????
                0f, 0f, 0.5f, 0.5f,
                -1f, -aspectRatio, 1f, 1f,
                1f, -aspectRatio, 1f, 0f,
                1f, aspectRatio, 0f, 0f,
                -1f, aspectRatio, 0f, 1f,
                -1f, -aspectRatio, 1f, 1f
        };
    }

    public void changeCameraDirection(Camera camera) {
        if (camera.getCameraId() == BACK) {
            setBackCoordinate();
        } else{
            setFrontCoordinate();
        }
    }
}