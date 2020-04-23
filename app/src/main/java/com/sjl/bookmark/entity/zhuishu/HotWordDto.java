package com.sjl.bookmark.entity.zhuishu;

import java.util.List;

/**
 * 书籍搜索热词传输dto
 */
public class HotWordDto extends BaseZhuiShu {


    private List<String> hotWords;

    public List<String> getHotWords() {
        return hotWords;
    }

    public void setHotWords(List<String> hotWords) {
        this.hotWords = hotWords;
    }
}
