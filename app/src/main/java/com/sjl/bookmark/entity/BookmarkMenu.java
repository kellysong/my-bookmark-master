package com.sjl.bookmark.entity;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookmarkMenu
 * @time 2022/12/2 17:19
 * @copyright(C) 2022 song
 */
public class BookmarkMenu {
    private String name;
    private String sourceFile;

    public BookmarkMenu(String name, String sourceFile) {
        this.name = name;
        this.sourceFile = sourceFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }
}
