package com.sjl.bookmark.entity;

import java.io.File;
import java.util.List;

/**
 *文件栈
 */

public class FileStack {

    private Node node = null;
    private int count = 0;

    /**
     * 入栈
     * @param fileSnapshot
     */
    public void push(FileSnapshot fileSnapshot){
        if (fileSnapshot == null) return;
        Node fileNode = new Node();
        fileNode.fileSnapshot = fileSnapshot;
        fileNode.next = node;//指向下一个节点
        node = fileNode;//当前节点
        ++count;
    }

    /**
     * 出栈
     */
    public FileSnapshot pop(){
        Node fileNode = node;
        if (fileNode == null) return null;
        FileSnapshot fileSnapshot = fileNode.fileSnapshot;
        node = fileNode.next;
        --count;
        return fileSnapshot;
    }

    public int getSize(){
        return count;
    }

    /**
     * 节点
     */
    public class Node {
        FileSnapshot fileSnapshot;
        Node next;//下一个节点
    }

    /**
     * 文件快照
     */
    public static class FileSnapshot{
        public String filePath;
        public List<File> files;
        public int scrollOffset;//滚动偏移记录
    }
}
