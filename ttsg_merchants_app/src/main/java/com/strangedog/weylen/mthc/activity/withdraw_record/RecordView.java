package com.strangedog.weylen.mthc.activity.withdraw_record;

import com.strangedog.weylen.mthc.entity.WithdrawRecordEntity;

import java.util.List;

/**
 * Created by weylen on 2016-08-29.
 */
public interface RecordView {
    void onStartList();
    void onListFailure();
    void onListSuccess(List<WithdrawRecordEntity> data, boolean isComplete);
    void onLoadMoreFailure();
    void onLoadMoreSuccess(List<WithdrawRecordEntity> data, boolean isComplete);
}
