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
     *{
     * comCode: "",
     * num: "YT5800956776422",
     * auto: [
     * {
     * comCode: "yuantong",
     * lengthPre: 15,
     * name: "圆通速递"
     * }
     * ]
     * }
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
         * comCode: "yuanton
         * lengthPre:
         * name: "圆通
         */

        private String comCode;
        private int lengthPre;
        private String name;


        public String getComCode() {
            return comCode;
        }

        public void setComCode(String comCode) {
            this.comCode = comCode;
        }

        public int getLengthPre() {
            return lengthPre;
        }

        public void setLengthPre(int lengthPre) {
            this.lengthPre = lengthPre;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
