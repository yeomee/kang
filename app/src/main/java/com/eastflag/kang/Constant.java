package com.eastflag.kang;

/**
 * Created by eastflag on 2015-12-22.
 */
public final class Constant {
    public static final int AJAX_TIMEOUT = 1000 * 5; //20초 타임아웃
    public static final String TEMP_PHOTO_FILE = "temp.jpg";       // 임시 저장파일

    public static final String HOST = "http://api.strongkang.com:1337";

    public static final String API_INTRO= "/intro.asp";
    public static final String API_010 = "/moim/moim.asp";
    public static final String API_MOIM_ADD = "/moim/moim_i.asp"; //모임등록(관리자)
    public static final String API_MOIM_MODIFY = "/moim/moim_u.asp"; //모임 수정(관리자)
    public static final String API_MOIM_DELETE = "/moim/moim_d.asp"; //모임 삭제(관리자)
    public static final String API_110 = "/member/member.asp";
    public static final String API_111 = "/member/member_i.asp"; //회원등록
    public static final String API_112 = "/member/member_u.asp"; //회원수정
    public static final String API_200_SCENE = "/scname/moim_i.asp"; //화면 호출 : 모임 등록
    public static final String API_201_SCENE = "/scname/moim_u.asp"; //화면 호출 : 모임 수정
    public static final String API_111_REG_SCENE = "/scname/member_i.asp"; //화면 호출 : 회원 등록
    public static final String API_111_MOD_SCENE = "/scname/member_u.asp"; //화면 호출 : 회원 수정

    public static final String API_SERVER_WORKING = "/server_working.asp";

    public static final String API_020 = "/member/passno_c.asp"; //비번 확인

    public static final String API_MOIM_DELETE_LIST_FOR_MEMBER = "/scname/moim_mb_d.asp"; //모임삭제 리스트보기(회원용)
    public static final String API_MOIM_DELETE_FOR_MEMBER = "/moim/moim_mb_d.asp";
}
