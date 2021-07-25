/**
 * Created by Vimal on July-2021.
 */
package com.vimal.funnycam.models;

import java.io.Serializable;

public class JsonSticker implements Serializable {

    private String name;
    private Integer image;
    private String path;

    public JsonSticker(String name, Integer image, String path) {
        this.name = name;
        this.image = image;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


}



