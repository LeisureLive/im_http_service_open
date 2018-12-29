package com.qunar.qchat.model.result;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * @auth dongzd.zhang
 * @Date 2018/11/1 16:16
 */
public class GetMucVcardResult {

    private String domain;
    private List<MucInfo> mucs;


    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<MucInfo> getMucs() {
        return mucs;
    }

    public void setMucs(List<MucInfo> mucs) {
        this.mucs = mucs;
    }

    @Override
    public String toString() {
        return "GetMucVcardResult{" +
                "domain='" + domain + '\'' +
                ", mucs=" + mucs +
                '}';
    }

    public static class MucInfo {
        @JsonProperty("MN")
        private String MN;
        @JsonProperty("SN")
        private String SN;
        @JsonProperty("MD")
        private String MD;
        @JsonProperty("MT")
        private String MT;
        @JsonProperty("MP")
        private String MP;
        @JsonProperty("VS")
        private String VS;
        @JsonIgnore
        public String getMN() {
            return MN;
        }

        @JsonIgnore
        public void setMN(String MN) {
            this.MN = MN;
        }

        @JsonIgnore
        public String getSN() {
            return SN;
        }

        @JsonIgnore
        public void setSN(String SN) {
            this.SN = SN;
        }

        @JsonIgnore
        public String getMD() {
            return MD;
        }

        public void setMD(String MD) {
            this.MD = MD;
        }

        @JsonIgnore
        public String getMT() {
            return MT;
        }

        @JsonIgnore
        public void
        setMT(String MT) {
            this.MT = MT;
        }
        @JsonIgnore
        public String getMP() {
            return MP;
        }
        @JsonIgnore
        public void setMP(String MP) {
            this.MP = MP;
        }
        @JsonIgnore
        public String getVS() {
            return VS;
        }
        @JsonIgnore
        public void setVS(String VS) {
            this.VS = VS;
        }

        @Override
        public String toString() {
            return "MucInfo{" +
                    "MN='" + MN + '\'' +
                    ", SN='" + SN + '\'' +
                    ", MD='" + MD + '\'' +
                    ", MT='" + MT + '\'' +
                    ", MP='" + MP + '\'' +
                    ", VS='" + VS + '\'' +
                    '}';
        }
    }
}
