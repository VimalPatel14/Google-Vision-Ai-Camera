/**
 * Created by Vimal on July-2021.
 */
package com.vimal.funnycam.opengl;

import android.content.Context;


import com.vimal.funnycam.opengl.utils.ResourceHelper;
import com.vimal.funnycam.opengl.utils.ShaderHelper;

import static android.opengl.GLES20.glUseProgram;

public class Shader {

    protected static final String S_TEXTURE = "sTexture";

    protected static final String U_MATRIX = "uMatrix";
    protected static final String U_TEXTURE_UNIT = "uTextureUnit";

    protected static final String A_POSITION = "aPosition";
    protected static final String A_TEXTURE_COORDINATES = "aTextureCoordinates";

    protected final int program;

    protected float[] coordinates;

    protected Shader(Context context, int vertexShaderResourceId, int fragmentShaderResourceId) {
        program = ShaderHelper.buildProgram(
                ResourceHelper.readFile(context, vertexShaderResourceId),
                ResourceHelper.readFile(context, fragmentShaderResourceId)
        );
    }

    public void useProgram() {
        glUseProgram(program);
    }
    public float[] getCoordinates() {
        return coordinates;
    }
}
