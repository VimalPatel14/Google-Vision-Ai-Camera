/**
 * Created by Vimal on July-2021.
 */
package com.vimal.funnycam.opengl;

import com.vimal.funnycam.opengl.filter.FilterRender;
import com.vimal.funnycam.opengl.sticker.StickerRender;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.vimal.funnycam.opengl.Constant.BYTES_PER_FLOAT;

public class Texture {

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private VertexArray vertexArray;

    public Texture() {
    }

    public void bindData(Shader shader){
        int arrLocXY = 0;
        int arrLocST = 0;
        if (shader instanceof FilterRender) {
            arrLocXY = ((FilterRender) shader).getPositionAttributeLocation();
            arrLocST = ((FilterRender) shader).getTextureCoordinatesAttributeLocation();

        }else if(shader instanceof StickerRender) {
            arrLocXY = ((StickerRender) shader).getPositionAttributeLocation();
            arrLocST = ((StickerRender) shader).getTextureCoordinatesAttributeLocation();
        }

        vertexArray = new VertexArray(shader.getCoordinates());
        vertexArray.setVertexAttributePointer(
                0,
                arrLocXY,
                POSITION_COMPONENT_COUNT,
                STRIDE
        );
        vertexArray.setVertexAttributePointer(
                POSITION_COMPONENT_COUNT,
                arrLocST,
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE
        );
    }

    public void draw(){
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }
}
