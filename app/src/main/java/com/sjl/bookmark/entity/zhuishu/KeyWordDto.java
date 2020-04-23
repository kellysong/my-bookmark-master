package com.sjl.bookmark.entity.zhuishu;

import java.util.List;

/**
 *自动搜索关键字传输dto
 */

public class KeyWordDto extends BaseZhuiShu {

    private List<String> keywords;

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
