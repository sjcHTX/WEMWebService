package com.etech.workflow.common;

import com.vignette.as.config.ConfigUtil;
import com.vignette.authn.AuthnBundle;
import com.vignette.authn.AuthnConsts;
import com.vignette.authn.AuthnException;
import com.vignette.authn.LoginMgr;
import com.vignette.config.client.common.ConfigException;
import org.apache.log4j.Logger;

/**
 * Login to VCM programatically.
 *
 **/
public class LoginUtil {
    
	private static Logger logger = Logger.getLogger(LoginUtil.class);
    
    private static boolean loggedOn = false;
    
    /**
     * Does the programatic log in to VCM server
     * Method cannot be called in standalone EJB client mode.
     *
     * @throws AuthnException
     * @throws ConfigException
     */
    public static void loginAsAdmin() throws AuthnException, ConfigException {
        logger.info("loginAsAdmin(): entering");
        System.out.println("loginAsAdmin(): entering");
        login(getAdminUser(), getAdminPassword(), getHost(), getPort());
        logger.info("loginAsAdmin(): exiting");
        System.out.println("loginAsAdmin(): exiting");
    }
    
    public static String getHost() throws AuthnException, ConfigException {
        return ConfigUtil.getVMCComponent().getCMSComponent().getHost();
    }
    
    public static String getPort() throws AuthnException, ConfigException {
        return String.valueOf(ConfigUtil.getVMCComponent().getCMSComponent().getPort());
    }
    
    public static String getAdminUser() throws AuthnException, ConfigException {
        return ConfigUtil.getVMCComponent().getCMSComponent().getAppserverAdminName();
    }
    
    public static String getAdminPassword() throws AuthnException, ConfigException {
        return ConfigUtil.getVMCComponent().getCMSComponent().getAppserverAdminPassword();
    }
    
    public static void forceLoginAsAdmin() throws AuthnException, ConfigException {
        loggedOn=false;
        loginAsAdmin();
    }
    
    
    /**
     * Vignette V7 Login method.
     * @param username String
     * @param password String
     * @param host String
     * @param port String
     * @throws AuthnException
     */
    public static void login(String username, String password, String host, String port) throws AuthnException{
        logger.info("login(user,pass,host,port): entering");
        logger.debug("data:" + username + ", " + host + ", " + port);
        System.out.println("login(user,pass,host,port): entering");
        System.out.println("data:" + username + ", " + host + ", " + port);
        if(loggedOn) {
            logger.debug("already logged on");
            System.out.println("already logged on");
        } else {
            logger.debug("logging in");
            System.out.println("logging in");
            AuthnBundle authBundle = new AuthnBundle();
            authBundle.setFactory("weblogic.jndi.WLInitialContextFactory");
            authBundle.setAuthType(AuthnConsts.WEBLOGIC_CONTEXT);
            authBundle.setProtocol("t3");
            authBundle.setUsername(username);
            authBundle.setPassword(password);
            authBundle.setHost(host);
            authBundle.setPort(port);
            authBundle.setEnableSSL(false);
            LoginMgr loginMgr = new LoginMgr();
            loginMgr.login(authBundle);
            loggedOn = true;
            loginMgr.ensureLogin();
            logger.debug("logger info: " + loginMgr.getCurrentAuthnBundle().toString());
            System.out.println("logger info: " + loginMgr.getCurrentAuthnBundle().toString());
        }
        logger.info("login(user,pass,host,port): exiting");
        System.out.println("login(user,pass,host,port): exiting");
    }
    
}

