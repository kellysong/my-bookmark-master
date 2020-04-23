package com.sjl.bookmark.entity;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressDetail2.java
 * @time 2019/4/14 10:14
 * @copyright(C) 2019 song
 */
public class ExpressDetail2 {

    /**
     * status : 0
     * msg : ok
     * result : {"number":"780098068058","type":"zto","list":[{"time":"2018-03-09 11:59:26","status":"【石家庄市】 快件已在 【长安三部】 签收,签收人: 本人, 感谢使用中通快递,期待再次为您服务!"},{"time":"2018-03-09 09:03:10","status":"【石家庄市】快件已到达【长安三部】（0311-85344265）,业务员 容晓光（13081105270）正在第1次派件"},{"time":"2018-03-08 23:43:44","status":"【石家庄市】 快件离开 【石家庄】 发往 【长安三部】"},{"time":"2018-03-08 21:00:44","status":"【石家庄市】 快件到达 【石家庄】"},{"time":"2018-03-07 01:38:45","status":"【广州市】 快件离开 【广州中心】 发往 【石家庄】"},{"time":"2018-03-07 01:36:53","status":"【广州市】 快件到达 【广州中心】"},{"time":"2018-03-07 00:40:57","status":"【广州市】 快件离开 【广州花都】 发往 【石家庄中转】"},{"time":"2018-03-07 00:01:55","status":"【广州市】 【广州花都】（020-37738523） 的 马溪 （18998345739） 已揽收"}],"deliverystatus":"3","issign":"1","expName":"中通快递","expSite":"www.zto.com","expPhone":"95311","courier":"容晓光","courierPhone":"13081105270"}
     */

    private String status;
    private String msg;
    private ResultBean result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * number : 780098068058
         * type : zto
         * list : [{"time":"2018-03-09 11:59:26","status":"【石家庄市】 快件已在 【长安三部】 签收,签收人: 本人, 感谢使用中通快递,期待再次为您服务!"},{"time":"2018-03-09 09:03:10","status":"【石家庄市】快件已到达【长安三部】（0311-85344265）,业务员 容晓光（13081105270）正在第1次派件"},{"time":"2018-03-08 23:43:44","status":"【石家庄市】 快件离开 【石家庄】 发往 【长安三部】"},{"time":"2018-03-08 21:00:44","status":"【石家庄市】 快件到达 【石家庄】"},{"time":"2018-03-07 01:38:45","status":"【广州市】 快件离开 【广州中心】 发往 【石家庄】"},{"time":"2018-03-07 01:36:53","status":"【广州市】 快件到达 【广州中心】"},{"time":"2018-03-07 00:40:57","status":"【广州市】 快件离开 【广州花都】 发往 【石家庄中转】"},{"time":"2018-03-07 00:01:55","status":"【广州市】 【广州花都】（020-37738523） 的 马溪 （18998345739） 已揽收"}]
         * deliverystatus : 3
         * issign : 1
         * expName : 中通快递
         * expSite : www.zto.com
         * expPhone : 95311
         * courier : 容晓光
         * courierPhone : 13081105270
         */

        private String number;
        private String type;
        private String deliverystatus;
        private String issign;
        private String expName;
        private String expSite;
        private String expPhone;
        private String courier;
        private String courierPhone;
        private List<ListBean> list;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDeliverystatus() {
            return deliverystatus;
        }

        public void setDeliverystatus(String deliverystatus) {
            this.deliverystatus = deliverystatus;
        }

        public String getIssign() {
            return issign;
        }

        public void setIssign(String issign) {
            this.issign = issign;
        }

        public String getExpName() {
            return expName;
        }

        public void setExpName(String expName) {
            this.expName = expName;
        }

        public String getExpSite() {
            return expSite;
        }

        public void setExpSite(String expSite) {
            this.expSite = expSite;
        }

        public String getExpPhone() {
            return expPhone;
        }

        public void setExpPhone(String expPhone) {
            this.expPhone = expPhone;
        }

        public String getCourier() {
            return courier;
        }

        public void setCourier(String courier) {
            this.courier = courier;
        }

        public String getCourierPhone() {
            return courierPhone;
        }

        public void setCourierPhone(String courierPhone) {
            this.courierPhone = courierPhone;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * time : 2018-03-09 11:59:26
             * status : 【石家庄市】 快件已在 【长安三部】 签收,签收人: 本人, 感谢使用中通快递,期待再次为您服务!
             */

            private String time;
            private String status;

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }
        }
    }
}
