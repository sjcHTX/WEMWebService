package com.etech.workflow.dmworkflow;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;

import com.vignette.as.client.common.ref.ManagedObjectVCMRef;
import com.vignette.as.client.ejb.IManagedObjectOps;
import com.vignette.as.client.ejb.IWorkflowMgmtOps;
import com.vignette.as.client.exception.ApplicationException;
import com.vignette.as.client.exception.ValidationException;
import com.vignette.as.client.javabean.ContentInstance;
import com.vignette.as.client.javabean.ContentManagementOps;
import com.vignette.as.client.javabean.ManagedObject;
import com.vignette.as.client.javabean.OpsFactory;
import com.vignette.as.common.util.AsCMSConstants;
import com.vignette.as.config.ConfigUtil;
import com.vignette.authz.client.AuthzException;
import com.vignette.cms.client.common.CMSException;
import com.vignette.cms.client.common.ObjectId;
import com.vignette.cms.client.common.WfId;
import com.vignette.cms.client.common.WfPayloadBundle;
import com.vignette.cms.client.common.WfPayloadBundleFlags;
import com.vignette.cms.client.javabean.CMS;
import com.vignette.cms.client.javabean.WfOps;
import com.vignette.cms.client.javabean.WfPayload;
import com.vignette.cms.client.javabean.WfProcess;
import com.vignette.cms.server.wf.WfEngine;
import com.vignette.config.client.common.ConfigException;
import com.vignette.util.EmailMsg;

/**
 * 
 * Utility class for workflow services
 *
 */
public class WorkflowUtil {
    
    private static final Logger logger = Logger.getLogger(WorkflowUtil.class);   
    
  
    /**
     * Retrieve payload object by payload id.
     * @param payloadId
     * @return
     */
    public static WfPayload getPayloadObj(String payloadId) {
        logger.info("getPayloadObj(id): entering");
        System.out.println("getPayloadObj(id): entering");
        WfPayload payload = null;
        try {
            CMS cms = ConfigUtil.getCMS();
            if (cms==null) {
                logger.debug("cms is null");
                System.out.println("cms is null");
            } else {
                logger.debug("cms is not null");
                System.out.println("cms is not null");
            }
            logger.debug("payloadId=" + payloadId);
            System.out.println("payloadId=" + payloadId);
            payload = (WfPayload)cms.findObjectById(new ObjectId(payloadId));
        } catch (Exception e) {
            logger.error("getPayloadObj(id): CAUGHT",e);
            System.out.println("getPayloadObj(id): CAUGHT - " + e);
        }
        logger.info("getPayloadObj(id): exiting");
        System.out.println("getPayloadObj(id): exiting");
        return payload;
    }
    
    /**
     * Retrieve managed objects from workflow payload
     * @param payloadId
     * @return
     */
    public static List<String> getManagedObjectIdsFromPayload(String payloadId) {
        logger.info("getManagedObjectIdsFromPayload(id): entering");
        System.out.println("getManagedObjectIdsFromPayload(id): entering");
        
        List<String> idList = new ArrayList<String>();
        try {
        	//get the payload object
            WfPayload p = getPayloadObj(payloadId);
            if (p!=null) {
                Map map = p.getContent(AsCMSConstants.PROJECT_PATH_DELIMITER);
                Set set = map.keySet();
                Iterator it = set.iterator();
                //Go through the collection to get the vcm ids.
                while (it.hasNext()) {
                    String managementId = it.next()+"";
                    idList.add(managementId);
                }
            }
        } catch (Exception e) {
            logger.error("getManagedObjectIdsFromPayload(id): CAUGHT",e);
            System.out.println("getManagedObjectIdsFromPayload(id): CAUGHT - " + e);
        }
        logger.info("getManagedObjectIdsFromPayload(id): exiting");
        System.out.println("getManagedObjectIdsFromPayload(id): exiting");
        return idList;
    }
    
    /**
     * 
     * @param wfProcess
     * @return
     * @throws ConfigException
     * @throws CMSException
     * @throws RemoteException
     */
    public static WfId getWfIdForProcess(WfProcess wfProcess) throws ConfigException, CMSException, RemoteException {
        logger.info("getWfIdForProcess(wfProcess): entering");
        System.out.println("getWfIdForProcess(wfProcess): entering");
        WfOps wfOps = ConfigUtil.getWfOps();
        WfId wfId = new WfId();
        wfId.setId(wfOps.getProcess(wfProcess.getId()).getManager().getDefinitionId().getId());
        logger.info("getWfIdForProcess(wfProcess): exiting");
        System.out.println("getWfIdForProcess(wfProcess): exiting");
        return wfId;
    }
    
    
    /**
     * Returns the first ContentInstance found in given payload
     */
    public static ContentInstance getFirstPayloadContentInstance(String payloadId) throws ApplicationException,ValidationException {
        logger.info("getFirstPayloadContentInstance(payload): entering");
        System.out.println("getFirstPayloadContentInstance(payload): entering");
        ContentInstance ci=null;
        
        //Get a list of vcm ids from payload
        List vcmIds = getManagedObjectIdsFromPayload(payloadId);
        Iterator it = vcmIds.iterator();
        
        //Go through the list and return the first content instance.
        while (it.hasNext()) {
            String vcmId=(String)it.next();
            ManagedObject mo = ManagedObject.findByContentManagementId(new ManagedObjectVCMRef(vcmId));
            if (mo instanceof ContentInstance) {
                ci=(ContentInstance)mo;
                break;
            }
        }
        logger.info("getFirstPayloadContentInstance(payload): exiting");
        System.out.println("getFirstPayloadContentInstance(payload): exiting");
        return ci;
    }
    
    /**
     * Returns the first ManagedObject found in given payload
     */
    public static ManagedObject getFirstPayloadManagedObject(String payloadId) throws ApplicationException,ValidationException {
        logger.info("getFirstPayloadManagedObject(payload): entering");
        System.out.println("getFirstPayloadManagedObject(payload): entering");
        
        List vcmIds = getManagedObjectIdsFromPayload(payloadId);
        
        //return the first object
        ManagedObject mo = ManagedObject.findByContentManagementId(new ManagedObjectVCMRef((String)vcmIds.get(0)));
        logger.info("getFirstPayloadManagedObject(payload): exiting");
        System.out.println("getFirstPayloadManagedObject(payload): exiting");
        return mo;
    }    

    /**
	 * Returns the WfProcess for the given payload
	 * @param payloadId
	 * @return
	 * @throws CMSException
	 * @throws RemoteException
	 * @throws ConfigException
	 */
	public static WfProcess getProcess(String payloadId) throws CMSException, RemoteException, ConfigException {
		WfPayload payload=getPayloadObj(payloadId);
		WfPayloadBundleFlags flags=new WfPayloadBundleFlags();
		flags.setProcessId();
		WfPayloadBundle bundle=(WfPayloadBundle)payload.getBundle(flags);
		WfId processId=bundle.getProcessId();
		WfOps wfOps = ConfigUtil.getWfOps();
		return wfOps.getProcess(processId);				
	}
	
	/**
	 * Returns username or originator (requestor) of workflow process of the payload.
	 * @param payloadId
	 * @return
	 * @throws CMSException
	 * @throws RemoteException
	 * @throws ConfigException
	 */
	public static String getOriginator(String payloadId) throws CMSException, RemoteException, ConfigException {
		return getProcess(payloadId).getRequester().getName();
	}
	
	/**
	 * Sends email using the WF engine configuration.  
	 * This sends an email with a MIMETYPE of Text only.
	 * If you want to send an HTML email use other method.
	 * @param to - can be email addr, or VCM username
	 * @param from
	 * @param subject
	 * @param body	
	 * @throws ConfigException 
	 * @throws RemoteException 
	 * @throws CMSException 
	 */
	public static void sendEmail(String to, String subject,String body) throws ConfigException, CMSException, RemoteException {
		//for some reason, the following API doesn't work
		//String from = WfEngine.getMailFrom();
		
		String from = WFPropUtil.getString(WFPropUtil.FROM_ADDR);
		
		ConfigUtil.getWfOps().email(to, from, subject, body, null, null);
	}
	
	/**
	 * 
	 * @param to
	 * @param from
	 * @param subject
	 * @param body
	 * @param cc
	 * @param bcc
	 * @throws MessagingException
	 * @throws CMSException
	 * @throws AuthzException
	 * @throws ConfigException
	 */
	public static void sendHTMLEmail(String to, String subject,String body) throws MessagingException, CMSException, AuthzException, ConfigException {
		
		String[] toArray=convertRecipients(to);
		
        String mailhost = WfEngine.getMailHost();
        int mailport = WfEngine.getMailPort();
        String from = WfEngine.getMailFrom();
        
        EmailMsg msg = new EmailMsg(toArray, from, subject, body);
	    msg.setServer(mailhost);
	    msg.setPort(mailport);
	    msg.setEncoding("utf-8");
        msg.sendHtml();
	}
	
	/**
	 * Returns emails for a given VCM username.
	 * @param user
	 * @return
	 * @throws AuthzException
	 * @throws ConfigException
	 */
	public static String[] getEmailForUser(String user) throws AuthzException, ConfigException {
		return ConfigUtil.getAuthzOps().getEmailForUser(user);
	}
	
	/**
	 * Converts a comma delimited string of username or email address and converts
	 * into an array of email addresses.
	 * @param recipients
	 * @return
	 * @throws AuthzException
	 * @throws ConfigException
	 */
	public static String[] convertRecipients(String recipients) throws AuthzException, ConfigException {
		ArrayList<String> userList=new ArrayList<String>();
		if (recipients==null || "".equals(recipients)) {
			
		} else {
			String[] tempArray=recipients.split(",");
			if (tempArray==null || tempArray.length<1) {
				
			} else {
				for (int i=0;i<tempArray.length;i++) {
					String auser=tempArray[i];
					if (auser.indexOf("@")>0) {
						//is email
						userList.add(auser);
					} else {
						String[] emailArray=getEmailForUser(auser);
						if (emailArray==null || emailArray.length<1) {
						} else {
							for (int y=0;y<emailArray.length;y++) {
								userList.add(emailArray[y]);
							}							
						}
					}
				}
			}//endif(tempArray==null)
			
		}//endif(user==null)
		
		String[] userArray=new String[userList.size()];
		userArray=(String[])userList.toArray(userArray);
		return userArray;		
	}
	
	
	@SuppressWarnings("deprecation")
	public static void createVersion(String payloadId, String label, String comment) throws RemoteException {
        
        try {
        	System.out.println("workflowutil.createVersion(): entering");	
              WfPayload p = getPayloadObj(payloadId);
              System.out.println("workflowutil.createVersion(): 1");
              if (p!=null) {
            	  System.out.println("workflowutil.createVersion(): 2");
                  Map map = p.getContent(AsCMSConstants.PROJECT_PATH_DELIMITER);
                  System.out.println("workflowutil.createVersion(): 3");
                  Set set = map.keySet();
                  System.out.println("workflowutil.createVersion(): 4");
                  Iterator I = set.iterator();
                  System.out.println("workflowutil.createVersion(): 5");
                  IWorkflowMgmtOps  wf = (IWorkflowMgmtOps) OpsFactory.create(IWorkflowMgmtOps.class);
                  System.out.println("workflowutil.createVersion(): 6");
                  IManagedObjectOps moOps = (IManagedObjectOps) OpsFactory.create(IManagedObjectOps.class);
                  System.out.println("workflowutil.createVersion(): 7");
                  //Go through the collection to get the vcm ids.
                  while (I.hasNext()) {
                	  System.out.println("workflowutil.createVersion(): 8");
                      String comment2 = "Version created by System - Published by " + comment;
                      System.out.println("workflowutil.createVersion(): 9");
                      String managementId = I.next().toString();
                      System.out.println("workflowutil.createVersion(): 10");
                      ManagedObjectVCMRef vcmRef = new ManagedObjectVCMRef(managementId);
                      System.out.println("workflowutil.createVersion(): 11");
          
                      int newVersionNumber = ContentManagementOps.createVersion(vcmRef, comment2);
                      System.out.println("workflowutil.createVersion(): 12");
          
                      if (label.equals("")) {
                    	  System.out.println("workflowutil.createVersion(): 13");
                            ManagedObject mo = vcmRef.retrieveManagedObject();
                            System.out.println("workflowutil.createVersion(): 14");

                            if (mo instanceof ContentInstance) {
                            	System.out.println("workflowutil.createVersion(): 15");
                                  ContentInstance ci = (ContentInstance) mo;
                                  System.out.println("workflowutil.createVersion(): 16");
                
                                  String title = (String) ci.getAttributeValue("TITLE");
                                  System.out.println("workflowutil.createVersion(): 17");
                                  label = title + " - Version " + String.valueOf(newVersionNumber);
                                  System.out.println("workflowutil.createVersion(): 18");
                
                                  ContentManagementOps.addLabelToVersion(vcmRef, newVersionNumber, label);
                                  System.out.println("workflowutil.createVersion(): 19");
                            }
                      } else {
                    	  System.out.println("workflowutil.createVersion(): 20");
                            ContentManagementOps.addLabelToVersion(vcmRef, newVersionNumber, label);
                            System.out.println("workflowutil.createVersion(): 22");
                      }
                  }
             }
          } catch (Exception e) {
              logger.error("createVersion: CAUGHT",e);
              System.out.println("createVersion: CAUGHT - " + e);
          }
        
  }	
}
