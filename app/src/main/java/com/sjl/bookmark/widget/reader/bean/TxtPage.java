package com.sjl.bookmark.widget.reader.bean;

import java.util.List;

/**
 * 章节下的文本页数
 */
public class TxtPage {
    public int position;
    public String title;
    public int titleLines; //当前 lines 中为 title 的行数。
    public List<String> lines;//每行的文本
}
