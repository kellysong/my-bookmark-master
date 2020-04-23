package com.sjl.bookmark.dao.util;

import android.content.Context;
import android.text.TextUtils;

import com.sjl.bookmark.dao.impl.BookmarkService;
import com.sjl.bookmark.entity.table.Bookmark;
import com.sjl.core.util.log.LogUtils;
import com.sjl.core.util.SerializeUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 书签解析类
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookmarkParse.java
 * @time 2018/2/7 14:13
 * @copyright(C) 2018 song
 */
public class BookmarkParse {


    /**
     * 读取书签文件
     *
     * @param context
     * @param fileName
     * @throws IOException
     */
    public void readBookmarkHtml(Context context, String fileName) throws IOException {
        InputStream inputStream = context.getAssets().open(fileName);
        String str = inputStreamToStr(inputStream);
        Document doc = Jsoup.parse(str);
        List<Bookmark> bookmarkList = new ArrayList<Bookmark>();
        parseBookmarkHtml(bookmarkList, doc);
        LogUtils.i("解析结果集合大小：" + bookmarkList.size());
        SerializeUtils.serialize("bookmark", bookmarkList);

        BookmarkService bookmarkService = BookmarkService.getInstance(context);
        bookmarkService.saveBookmarkLists(bookmarkList);
    }

    /**
     * 更新书签
     *
     * @param context 上下文
     * @param file    书签文件
     * @return
     * @throws IOException
     */
    public boolean updateBookmark(Context context, File file) throws IOException {
        try {
            InputStream inputStream = new FileInputStream(file);
            String str = inputStreamToStr(inputStream);
            Document doc = Jsoup.parse(str);
            List<Bookmark> bookmarkList = new ArrayList<Bookmark>();
            parseBookmarkHtml(bookmarkList, doc);
            LogUtils.i("本次更新书签集合大小：" + bookmarkList.size());
            BookmarkService bookmarkService = BookmarkService.getInstance(context);
            bookmarkService.deleteAllBookmark();
            bookmarkService.saveBookmarkLists(bookmarkList);
            inputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 解析google浏览器书签算法
     *
     * @param bookmarkList
     * @param doc
     */
    private void parseBookmarkHtml(List<Bookmark> bookmarkList, Document doc) {
        Elements dls = doc.select("DL");
        int dlSize = dls.size();
        LogUtils.i("dls节点个数：" + dlSize);//89
        Elements h3s = doc.select("DT").select("H3");
        int h3Size = h3s.size();
        LogUtils.i("h3节点个数：" + h3Size);//88
        Bookmark bookmark = null;
        if (dlSize > 0) {
            dls.remove(dls.get(0));
            dlSize = dls.size();
            //DT->H3和DL同级
            if (h3Size == dlSize) {

                for (int i = 1; i < h3Size; i++) {// i= 1是为了过滤Bookmarks
                    Element element = h3s.get(i);
                    String title = element.text();
                    Element dl = dls.get(i);
                    Elements children = dl.children();
                    Elements existDl = children.select("DL");
                    if (existDl.is("DL")) {//还嵌套有dL
                        Iterator<Element> iterator = children.iterator();
                        while (iterator.hasNext()) {
                            Element next = iterator.next();
                            String tagName = next.tagName();
                            if (tagName.equals("p")) {
                                iterator.remove();
                                continue;
                            }
                            int finalSize = next.children().size();
                            if (finalSize > 1) {//不是直接书签，即书签文件夹
                                iterator.remove();
                            }
                        }
                        Elements elements = children.select("A");
                        processBookmark(title, elements, bookmark, bookmarkList);
                        continue;
                    } else {//直接书签
                        Elements filterDl = dl.select("DT > A");
                        processBookmark(title, filterDl, bookmark, bookmarkList);
                    }
                }
            } else {
                LogUtils.w("解析错误");
            }
        } else {
            LogUtils.w("没有相关数据");
        }


    }

    /**
     * 加工书签
     *
     * @param title
     * @param filterDl
     * @param bookmark
     * @param bookmarkList
     */
    private void processBookmark(String title, Elements filterDl, Bookmark bookmark, List<Bookmark> bookmarkList) {
        int filterDlSize = filterDl.size();
        if (filterDlSize == 0) {
            return;
        }
        // LogUtils.i("h3标题：" + title);
        // LogUtils.i("filterDl：" + filterDl.text());//查找某个父元素下的直接子元素
        String base64 = "";
        bookmark = new Bookmark(0, title);
        bookmarkList.add(bookmark);
        for (int j = 0; j < filterDlSize; j++) {
            Element a = filterDl.get(j);
            String text = a.text();
            String hrefAttr = a.attr("HREF");
            String iconAttr = a.attr("ICON");
            if (!TextUtils.isEmpty(iconAttr)) {
                base64 = iconAttr.split(",")[1];
            }
            bookmark = new Bookmark(title, hrefAttr, base64, text);
            bookmarkList.add(bookmark);
        }

    }

    /**
     * 输入流转字符串
     *
     * @param in 输入流
     * @return
     */
    private String inputStreamToStr(InputStream in) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bf = new BufferedReader(new InputStreamReader(in));
        String line;
        try {
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
