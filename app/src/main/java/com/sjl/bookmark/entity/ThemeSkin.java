package com.sjl.bookmark.entity;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ThemeSkin.java
 * @time 2018/12/29 22:14
 * @copyright(C) 2018 song
 */
public class ThemeSkin {
    private int skinIndex;
    private String skinTitle;
    private String skinFileName;
    private String skinColor;

    public ThemeSkin(int skinIndex, String skinTitle,String skinFileName, String skinColor) {
        this.skinIndex = skinIndex;
        this.skinTitle = skinTitle;
        this.skinFileName = skinFileName;
        this.skinColor = skinColor;
    }

    public String getSkinFileName() {
        return skinFileName;
    }

    public void setSkinFileName(String skinFileName) {
        this.skinFileName = skinFileName;
    }

    public int getSkinIndex() {
        return skinIndex;
    }

    public void setSkinIndex(int skinIndex) {
        this.skinIndex = skinIndex;
    }

    public String getSkinTitle() {
        return skinTitle;
    }

    public void setSkinTitle(String skinTitle) {
        this.skinTitle = skinTitle;
    }

    public String getSkinColor() {
        return skinColor;
    }

    public void setSkinColor(String skinColor) {
        this.skinColor = skinColor;
    }
}
