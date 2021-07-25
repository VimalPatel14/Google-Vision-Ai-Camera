/**
 * Created by Vimal on July-2021.
 */
package com.vimal.funnycam.utils;

public class GlassJson {

    private String type;
    private String path;
    private String factor;
    private String height;
    private String width;



    public GlassJson(String type, String path, String factor, String height, String width) {
        super();
        this.type = type;
        this.path = path;
        this.factor = factor;
        this.height = height;
        this.width = width;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFactor() {
        return factor;
    }

    public void setFactor(String factor) {
        this.factor = factor;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

}