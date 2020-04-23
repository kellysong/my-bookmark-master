package com.sjl.bookmark.widget.reader;


import com.sjl.bookmark.app.AppConstant;
import com.sjl.core.util.file.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * 处理书籍的工具类
 */

public class BookManager {
    //采用自己的格式去设置文件，防止文件被系统文件查询到
    public static final String SUFFIX_NB = ".nb";
    public static final String SUFFIX_TXT = ".txt";

    private String chapterName;
    private String bookId;
    private long chapterLen;
    private long position;
    private Map<String, Cache> cacheMap = new HashMap<>();
    private static volatile BookManager sInstance;

    public static BookManager getInstance(){
        if (sInstance == null){
            synchronized (BookManager.class){
                if (sInstance == null){
                    sInstance = new BookManager();
                }
            }
        }
        return sInstance;
    }

    public boolean openChapter(String bookId, String chapterName){
        return openChapter(bookId,chapterName,0);
    }

    public boolean openChapter(String bookId, String chapterName, long position){
        //如果文件不存在，则打开失败
        File file = new File(AppConstant.BOOK_CACHE_PATH + bookId
                + File.separator + chapterName + SUFFIX_NB);
        if (!file.exists()){
            return false;
        }
        this.bookId = bookId;
        this.chapterName = chapterName;
        this.position = position;
        createCache();
        return true;
    }

    private void createCache(){
        //创建Cache
        if (!cacheMap.containsKey(chapterName)){
            Cache cache = new Cache();
            File file = getBookFile(bookId, chapterName);
            //TODO:数据加载默认utf-8(以后会增加判断),FileUtils采用Reader获取数据的，可能用byte会更好一点
            char[] array = getFileContent(file).toCharArray();
            WeakReference<char[]> charReference = new WeakReference<char[]>(array);
            cache.size = array.length;
            cache.data = charReference;
            cacheMap.put(chapterName, cache);

            chapterLen = cache.size;
        }
        else {
            chapterLen = cacheMap.get(chapterName).getSize();
        }
    }

    public void setPosition(long position){
        this.position = position;
    }

    public long getPosition(){
        return position;
    }

    /**
     * 本来是获取File的内容的。但是为了解决文本缩进、换行的问题
     * 这个方法就是专门用来获取书籍的...
     *
     * 应该放在BookRepository中。。。
     * @param file
     * @return
     */
    public static String getFileContent(File file){
        Reader reader = null;
        String str = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            while ((str = br.readLine()) != null){
                //过滤空语句
                if (!str.equals("")){
                    //由于sb会自动过滤\n,所以需要加上去
                    sb.append("    "+str+"\n");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }


    //获取上一段
    public String getPrevPara(){
        //首先判断是否Position已经达到起始位置，已经越界
        if (position < 0){
            return null;
        }

        //初始化从后向前获取的起始点,终止点,文本
        int end = (int)position;
        int begin = end;
        char[] array = getContent();

        while (begin >= 0) { //判断指针是否达到章节的起始位置
            char character = array[begin]; //获取当前指针下的字符

            //判断当前字符是否为换行，如果为换行，就代表获取到了一个段落，并退出。
            //有可能发生初始指针指的就是换行符的情况。
            if ((character+"").equals("\n") && begin != end) {
                position = begin;
                //当当前指针指向换行符的时候向后退一步
                begin++;
                break;
            }
            //向前进一步
            begin--;
        }
        //最后end获取到段落的起始点，begin是段落的终止点。

        //当越界的时候，保证begin在章节内
        if (begin < 0){
            begin = 0;//在章节内
            position = -1; //越界
        }
        int size = end+1 - begin;
        return new String(array,begin,size);
    }

    //获取下一段
    public String getNextPara(){
        //首先判断是否Position已经达到终点位置
        if (position >= chapterLen){
            return null;
        }

        //初始化起始点，终止点。
        int begin = (int)position;
        int end = begin;
        char[] array = getContent();

        while (end < chapterLen) { //判断指针是否在章节的末尾位置
            char character = array[end]; //获取当前指针下的字符
            //判断当前字符是否为换行，如果为换行，就代表获取到了一个段落，并退出。
            //有可能发生初始指针指的就是换行符的情况。
            //这里当遇到\n的时候，不需要回退
            if ((character+"").equals("\n") && begin != end){
                ++end;//指向下一字段
                position = end;
                break;
            }
            //指向下一字段
            end++;
        }
        //所要获取的字段的长度
        int size = end - begin;
        return new String(array,begin,size);
    }

    //获取章节的内容
    public char[] getContent() {
        if (cacheMap.size() == 0){
            return new char[1];
        }
        char[] block = cacheMap.get(chapterName).getData().get();
        if (block == null) {
            File file = getBookFile(bookId, chapterName);
            block = getFileContent(file).toCharArray();
            Cache cache = cacheMap.get(chapterName);
            cache.data = new WeakReference<char[]>(block);
        }
        return block;
    }

    public long getChapterLen(){
        return chapterLen;
    }

    public void clear(){
        cacheMap.clear();
        position = 0;
        chapterLen = 0;
    }

    /**
     * 创建或获取存储文件
     * @param folderName
     * @param fileName
     * @return
     */
    public static File getBookFile(String folderName, String fileName){
        return FileUtils.getFile(AppConstant.BOOK_CACHE_PATH + folderName
                + File.separator + fileName + SUFFIX_NB);
    }

    public static long getBookSize(String folderName){
        return FileUtils.getDirSize(FileUtils
                .getFolder(AppConstant.BOOK_CACHE_PATH + folderName));
    }

    /**
     * 根据文件名判断是否被缓存过 (因为可能数据库显示被缓存过，但是文件中却没有的情况，所以需要根据文件判断是否被缓存
     * 过)
     * @param folderName : bookId
     * @param fileName: chapterName
     * @return
     */
    public static boolean isChapterCached(String folderName, String fileName){
        File file = new File(AppConstant.BOOK_CACHE_PATH + folderName
                + File.separator + fileName + SUFFIX_NB);
        return file.exists();
    }


    /**
     * 缓存章节
     *
     * @param folderName 文件目录
     * @param fileName 文件名
     * @param content 内容
     */
    public void saveChapterInfo(String folderName, String fileName, String content) {
        File file = BookManager.getBookFile(folderName, fileName);
        //获取流并存储
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public class Cache {
        private long size;
        private WeakReference<char[]> data;

        public WeakReference<char[]> getData() {
            return data;
        }

        public void setData(WeakReference<char[]> data) {
            this.data = data;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }
    }
}
