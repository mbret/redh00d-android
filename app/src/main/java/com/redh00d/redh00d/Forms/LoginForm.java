package com.redh00d.redh00d.Forms;

import java.util.HashMap;

/**
 * Created by Maxime on 10/9/2014.
 */
public class LoginForm {

    public HashMap<String, String> validate(){
        HashMap result = new HashMap();

        return result;
    }

    public static boolean validateEmail(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    public static boolean validatePassword(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 3;
    }
}
