package com.example.contacts_applitcation.security;

import com.example.contacts_applitcation.SpringAppContext;
import org.springframework.core.env.Environment;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 864000000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users";
    public static final String TOKEN_SECRET = "i6iinobjmn4c9aaw45jjhb0ahwov3pa80uaymhj7znxwejzcujsonayjagghht01";
    public static final String VERIFICATION_EMAIL_URL = "/users/email-verification";
    public static String getTokenSecret() {
        Environment environment = (Environment) SpringAppContext.getBean("environment");
        return  environment.getProperty("secretToken");
    }


}
