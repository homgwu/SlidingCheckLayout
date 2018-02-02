package com.homg.slidingchecklayout;

/**
 * Created by homgwu on 2018/2/2.
 */

public class MainEntity {
    private boolean mIsSelect;
    public String mName;

    public MainEntity(String name) {
        mName = name;
    }

    public boolean isSelect() {
        return mIsSelect;
    }

    public void setSelect(boolean select) {
        mIsSelect = select;
    }
}
