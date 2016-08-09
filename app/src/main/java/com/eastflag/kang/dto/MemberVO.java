package com.eastflag.kang.dto;

/**
 * Created by oyg on 2016-01-07.
 */
public class MemberVO {
    private String pn; // 폰넘버
    private String paid; // 폰 안드로이드 아이디
    private String m_id; // 모임 id
    private String mb_id; // 회원 id
    private String mb_name; // 회원 이름
    private String my_position; // 회원 직위
    private String mb_pn; // 회원 폰넘버
    private String mb_add; // 회원 주소
    private String mb_enter_ymd; // 회원 가입일
    private String mb_action; // 회원 활동내역
    private String admin_yn;

    public String getPn() {
        return pn;
    }

    public String getMb_id() {
        return mb_id;
    }

    public void setMb_id(String mb_id) {
        this.mb_id = mb_id;
    }

    public String getAdmin_yn() {
        return admin_yn;
    }

    public void setAdmin_yn(String admin_yn) {
        this.admin_yn = admin_yn;
    }

    public void setPn(String pn) {
        this.pn = pn;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getM_id() {
        return m_id;
    }

    public void setM_id(String m_id) {
        this.m_id = m_id;
    }

    public String getMb_name() {
        return mb_name;
    }

    public void setMb_name(String mb_name) {
        this.mb_name = mb_name;
    }

    public String getMy_position() {
        return my_position;
    }

    public void setMy_position(String my_position) {
        this.my_position = my_position;
    }

    public String getMb_pn() {
        return mb_pn;
    }

    public void setMb_pn(String mb_pn) {
        this.mb_pn = mb_pn;
    }

    public String getMb_add() {
        return mb_add;
    }

    public void setMb_add(String mb_add) {
        this.mb_add = mb_add;
    }

    public String getMb_enter_ymd() {
        return mb_enter_ymd;
    }

    public void setMb_enter_ymd(String enter_ymd) {
        this.mb_enter_ymd = enter_ymd;
    }

    public String getMb_action() {
        return mb_action;
    }

    public void setMb_action(String mb_action) {
        this.mb_action = mb_action;
    }
}
