package com.sjl.bookmark.entity;

import java.util.List;

/**
 * 快递进度明细
 *
 *快递单当前的状态 ：　
 0：在途，即货物处于运输过程中；
 1：揽件，货物已由快递公司揽收并且产生了第一条跟踪信息；
 2：疑难，货物寄送过程出了问题；
 3：签收，收件人已签收；
 4：退签，即货物由于用户拒签、超区等原因退回，而且发件人已经签收；
 5：派件，即快递正在进行同城派件；
 6：退回，货物正处于退回发件人的途中；
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressDetail.java
 * @time 2018/2/27 14:53
 * @copyright(C) 2018 song
 */
public class ExpressDetail {

    /**
     * message : ok
     * nu : 479389994039
     * ischeck : 1
     * condition : F00
     * com : zhongtong
     * status : 200
     * state : 3
     * data : [{"time":"2018-02-25 11:47:40","ftime":"2018-02-25 11:47:40","context":"[东莞市] [东莞南城]的派件已签收 感谢使用中通快递,期待再次为您服务!","location":"东莞南城"},{"time":"2018-02-25 08:35:01","ftime":"2018-02-25 08:35:01","context":"[东莞市] [东莞南城]的李建平正在第1次派件 电话:18925422692 请保持电话畅通、耐心等待","location":"东莞南城"},{"time":"2018-02-25 07:51:31","ftime":"2018-02-25 07:51:31","context":"[东莞市] 快件到达 [东莞南城]","location":"东莞南城"},{"time":"2018-02-24 16:34:17","ftime":"2018-02-24 16:34:17","context":"[东莞市] 快件离开 [东莞东城]已发往[东莞中心]","location":"东莞东城"},{"time":"2018-02-24 14:10:32","ftime":"2018-02-24 14:10:32","context":"[东莞市] 快件到达 [东莞东城]","location":"东莞东城"},{"time":"2018-02-24 08:43:16","ftime":"2018-02-24 08:43:16","context":"[东莞市] 快件离开 [东莞中心]已发往[东莞东城]","location":"东莞中心"},{"time":"2018-02-24 07:51:18","ftime":"2018-02-24 07:51:18","context":"[东莞市] 快件到达 [东莞中心]","location":"东莞中心"},{"time":"2018-02-24 05:48:48","ftime":"2018-02-24 05:48:48","context":"[深圳市] 快件离开 [深圳中心]已发往[东莞中心]","location":"深圳中心"},{"time":"2018-02-24 05:44:41","ftime":"2018-02-24 05:44:41","context":"[深圳市] 快件到达 [深圳中心]","location":"深圳中心"},{"time":"2018-02-24 03:00:51","ftime":"2018-02-24 03:00:51","context":"[深圳市] 快件离开 [福田新福星]已发往[东莞中心]","location":"福田新福星"},{"time":"2018-02-24 02:22:35","ftime":"2018-02-24 02:22:35","context":"[深圳市] [福田新福星]的新区精仓横岗已收件 电话:13715272741","location":"福田新福星"}]
     */

    private String message;
    private String nu;// 快递单号
    private String ischeck;
    private String condition;
    private String com;//公司
    private String status;
    private String state;
    private List<DataBean> data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNu() {
        return nu;
    }

    public void setNu(String nu) {
        this.nu = nu;
    }

    public String getIscheck() {
        return ischeck;
    }

    public void setIscheck(String ischeck) {
        this.ischeck = ischeck;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * time : 2018-02-25 11:47:40
         * ftime : 2018-02-25 11:47:40
         * context : [东莞市] [东莞南城]的派件已签收 感谢使用中通快递,期待再次为您服务!
         * location : 东莞南城
         */

        private String time;
        private String ftime;
        private String context;
        private String location;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getFtime() {
            return ftime;
        }

        public void setFtime(String ftime) {
            this.ftime = ftime;
        }

        public String getContext() {
            return context;
        }

        public void setContext(String context) {
            this.context = context;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "time='" + time + '\'' +
                    ", ftime='" + ftime + '\'' +
                    ", context='" + context + '\'' +
                    ", location='" + location + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ExpressDetail{" +
                "message='" + message + '\'' +
                ", nu='" + nu + '\'' +
                ", ischeck='" + ischeck + '\'' +
                ", condition='" + condition + '\'' +
                ", com='" + com + '\'' +
                ", status='" + status + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
