package com.jacklee.tuckshopstudent;

import java.math.BigInteger;
import java.security.MessageDigest;

public class Account {
    private int UserID;
    public String Username;
    public String Fullname;
    private String Password;

    public int getUserID() {
        return UserID;
    }

    public void setPassword(String password) {
        try {

            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] messageDigest = md.digest(password.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            Password = hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getPassword() {
        return Password;
    }
}
