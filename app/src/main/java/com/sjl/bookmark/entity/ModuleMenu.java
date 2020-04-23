package com.sjl.bookmark.entity;

/**
 * 工具栏模块菜单
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ModuleMenu.java
 * @time 2018/3/27 17:28
 * @copyright(C) 2018 song
 */
public class ModuleMenu {
    private String title;
    private int drawableId;
    private Class clz;
    private String webUrl;

    public ModuleMenu(String title, int drawableId, Class clz) {
        this.title = title;
        this.drawableId = drawableId;
        this.clz = clz;
    }
    public ModuleMenu(String title, int drawableId, String webUrl) {
        this.title = title;
        this.drawableId = drawableId;
        this.webUrl = webUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public Class getClz() {
        return clz;
    }

    public void setClz(Class clz) {
        this.clz = clz;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }
}
