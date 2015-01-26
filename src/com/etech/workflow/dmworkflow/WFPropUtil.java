package com.etech.workflow.dmworkflow;

import java.util.ResourceBundle;

public class WFPropUtil {
	public static final String FROM_ADDR="FromAddr";
    public static final String EMAIL_SERVER="EmailServer";
    public static final String EMAIL_SERVER_PORT="EmailServerPort";

    private WFPropUtil() {
    }
    
    private static ResourceBundle _rb=null;
    
    static {
        _rb=ResourceBundle.getBundle("workflow");
    }
    
    public static String getString(String key) {
        return _rb.getString(key);
    }    

}
