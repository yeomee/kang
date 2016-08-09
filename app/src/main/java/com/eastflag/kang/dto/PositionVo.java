package com.eastflag.kang.dto;

/**
 * Created by oyg on 2016-01-16.
 */
public class PositionVo {
    private String po_cd; // 직책 Code
    private String po_name; // 직책 이름

    public String getPo_cd() {
        return po_cd;
    }

    public void setPo_cd(String po_cd) {
        this.po_cd = po_cd;
    }

    public String getPo_name() {
        return po_name;
    }

    public void setPo_name(String po_name) {
        this.po_name = po_name;
    }

    @Override
    public String toString() {
        return po_name;
    }
}
