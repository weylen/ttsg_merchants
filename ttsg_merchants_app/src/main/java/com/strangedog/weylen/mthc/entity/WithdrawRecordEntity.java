package com.strangedog.weylen.mthc.entity;

/**
 * Created by zhou on 2016/9/8.
 */
public class WithdrawRecordEntity {

    private String quota;
    private String apply;
    private String deal;
    private String status;

    public String getQuota() {
        return quota;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

    public String getApply() {
        return apply;
    }

    public void setApply(String apply) {
        this.apply = apply;
    }

    public String getDeal() {
        return deal;
    }

    public void setDeal(String deal) {
        this.deal = deal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
