package com.pertamina.brightgas;

public class User {

    public static final int ROLE_ID_ADM = 1;
    public static final int ROLE_ID_SPV = 2;
    public static final int ROLE_ID_MO = 3;
    public static final int ROLE_ID_HO = 4;
    public static final int ROLE_ID_CLIENT = 5;

    public static String id;
    public static String name;
    public static String picture;

    public static void clear() {
        id = null;
        name = null;
        picture = null;
    }

}
