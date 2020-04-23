package com.sjl.bookmark.entity;

import java.util.List;

/**
 * 建议快递名称查询
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressName.java
 * @time 2018/2/27 15:03
 * @copyright(C) 2018 song
 */
public class ExpressName {

    /**
     * comCode :
     * num : 479389994039
     * auto : [{"comCode":"zhongtong","id":"","noCount":14849,"noPre":"479389","startTime":""},{"comCode":"minghangkuaidi","id":"","noCount":9,"noPre":"479389","startTime":""}]
     */

    private String comCode;
    private String num;
    private List<AutoBean> auto;

    public String getComCode() {
        return comCode;
    }

    public void setComCode(String comCode) {
        this.comCode = comCode;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public List<AutoBean> getAuto() {
        return auto;
    }

    public void setAuto(List<AutoBean> auto) {
        this.auto = auto;
    }

    public static class AutoBean {
        /**
         * comCode : zhongtong
         * id :
         * noCount : 14849
         * noPre : 479389
         * startTime :
         */

        private String comCode;
        private String id;
        private int noCount;
        private String noPre;
        private String startTime;

        public String getComCode() {
            return comCode;
        }

        public void setComCode(String comCode) {
            this.comCode = comCode;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getNoCount() {
            return noCount;
        }

        public void setNoCount(int noCount) {
            this.noCount = noCount;
        }

        public String getNoPre() {
            return noPre;
        }

        public void setNoPre(String noPre) {
            this.noPre = noPre;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }
    }
}
