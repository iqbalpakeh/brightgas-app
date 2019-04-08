package com.pertamina.brightgasse;

public class User {

    public static final int ROLE_ID_ADM = 1;
    public static final int ROLE_ID_SPV = 2;
    public static final int ROLE_ID_MO = 3;
    public static final int ROLE_ID_HO = 4;
    public static final int ROLE_ID_CLIENT = 5;

    public static String id;
    public static String username;
    public static int role_id;
    public static String role_name;
    public static String token;
    public static String image_url;

    public static void clear(){
        username = null;
        role_id = -1;
        role_name = null;
        token = null;
        image_url = null;
    }

}
