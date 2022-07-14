package com.server.authorization.application.pojo;

public final class SystemRole {

    private static final String baseUser = "BASE_USER";
    private static final String admin = "ADMIN";
    private static final String wholesaleUser = "WHOLESALE_USER";

    public static String getWholesaleUser() {
        return wholesaleUser;
    }

    public static String getAdmin() { return admin; }

    public static String getBaseUser() {
        return baseUser;
    }
}
