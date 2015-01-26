package com.etech.workflow.dmworkflow;

//import java.rmi.RemoteException;

import com.etech.workflow.common.VCMContentUtil;
import com.vignette.as.client.exception.ApplicationException;
import com.vignette.as.client.exception.ValidationException;
import com.vignette.as.client.javabean.ContentInstance;
import com.vignette.authz.client.AuthzException;
import com.vignette.cms.client.common.CMSException;
import com.vignette.config.client.common.ConfigException;

import org.apache.log4j.Logger;

import java.rmi.RemoteException;

/**
 * Service class, used by program task to execute the needsApproval method
 */

public class WorkflowService {
    
	private static Logger logger = Logger.getLogger(WorkflowService.class) ;
        
    /** Creates a new instance of WorkflowService */
    public WorkflowService() {
    }
    
    /**
     * Check if the any items associated to the Product and Article require approval.
     * If yes, send an email.
     * @param payloadId
     * @return
     */
    public static java.lang.String needsApproval(java.lang.String payloadId){
    	logger.info("needsApproval(payloadid): entering");
        logger.debug("payloadId:" + payloadId);
        System.out.println("needsApproval(payloadid): entering");
        System.out.println("payloadId:" + payloadId);
       
        java.lang.String items = VCMContentUtil.checkRelatedContent(getContentFromPayload(payloadId));
        
        if (items != null && !items.isEmpty()) {  
            logger.info("needsApproval sending emails with unapproved objects");
            logger.debug("The following items need approval: " + items);
            System.out.println("needsApproval sending emails with unapproved objects");
            System.out.println("The following items need approval: " + items);
            sendNotification(payloadId, items);
        }
        
        return items;
    }
    
   /**
    * Method to get emails IDs and call utility method to send emails, when payload has unapproved objects
    * @param payloadId
    * @param body
    * @return
    */
    private static java.lang.String sendNotification(java.lang.String payloadId, String relatedItems) {
    	java.lang.String rtn="";
    	
    	try {
			java.lang.String originator= WorkflowUtil.getOriginator(payloadId);
			java.lang.String[] emails = WorkflowUtil.getEmailForUser(originator);
			System.out.println("sendnotifications - originator: " + originator);
			System.out.println("sendnotifications - emails: " + emails[0]);
			
			if(emails != null){						
				java.lang.String subject = "Failed Workflow Notice";
				String body = "The following related content items needs approval: \n" + relatedItems;
				WorkflowUtil.sendEmail(emails[0], subject, body);				
			}
			
		} catch (CMSException e) {
			//logger.error(e.getMessage());
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (ConfigException e) {
			//logger.error(e.getMessage());
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (RemoteException e) {
			//logger.error(e.getMessage());
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (AuthzException e) {
			//logger.error(e.getMessage());
			System.out.println(e.getMessage());
			e.printStackTrace();
		}    	    	
    	return rtn;
    }
    
    /**
     * Retrieve the Publishing package instance from the payload
     * @param payloadId
     * @return
     */
    private static ContentInstance getContentFromPayload(java.lang.String payloadId){
    	logger.info("getContentFromPayload: entering");
        logger.debug("payloadId:" + payloadId);
        System.out.println("getContentFromPayload: entering");
        System.out.println("payloadId:" + payloadId);
        
        ContentInstance ci = null;
		try {
			ci = WorkflowUtil.getFirstPayloadContentInstance(payloadId); 
		} catch (ApplicationException e) {
			//logger.error(e.getMessage());
			System.out.println(e.getMessage());
		} catch (ValidationException e) {
			//logger.error(e.getMessage());
			System.out.println(e.getMessage());
		}
        
        return ci;
    }
 
    public void createVersion(String payloadId, String label, String comment) throws RemoteException {
    	logger.info("createVersion: entering");
        logger.debug("payloadId:" + payloadId);
        System.out.println("createVersion: entering");
        System.out.println("payloadId:" + payloadId);
       
        WorkflowUtil.createVersion(payloadId, label, comment);
        
    	logger.info("createVersion: exiting");
        System.out.println("createVersion: exiting");
    }
    
    

}
