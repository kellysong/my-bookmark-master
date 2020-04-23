package com.sjl.bookmark.entity.zhuishu;

import com.sjl.bookmark.entity.zhuishu.table.CollectBook;

import java.util.List;

/**
 * 收藏的书籍（书架显示的书）传输dto
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CollectBookDto.java
 * @time 2018/12/1 19:50
 * @copyright(C) 2018 song
 */
public class CollectBookDto extends BaseZhuiShu {
    private List<CollectBook> books;

    public List<CollectBook> getBooks() {
        return books;
    }

    public void setBooks(List<CollectBook> books) {
        this.books = books;
    }
}
