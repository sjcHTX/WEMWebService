package com.etech.workflow.common;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vignette.as.client.common.ref.ManagedObjectVCMRef;
import com.vignette.as.client.exception.ApplicationException;
import com.vignette.as.client.exception.ValidationException;
import com.vignette.as.client.javabean.AttributedObject;
import com.vignette.as.client.javabean.ContentInstance;
import com.vignette.as.client.javabean.ManagedObject;

/**
 * Utility class for VCM content related operations
 * 
 *
 */
public class VCMContentUtil {
	
	private static Logger logger = Logger.getLogger(VCMContentUtil.class) ;
	
	private static String lineBreak = System.getProperty("line.separator");
	
	/**
	 * Check if the any items associated to the quick links. 
	 * @param ci
	 * @return
	 */
	public static List<ManagedObject> getReferencedItems(ContentInstance ci, String relationName, String attributeName){
		
		
		List<ManagedObject> list = new ArrayList<ManagedObject>();
		if(logger.isDebugEnabled()) {
			logger.debug("checkReferencedItems ci " + ci);
			System.out.println("checkReferencedItems ci " + ci);
		}
		
		if(ci == null)
			return list;
				
		try {
			//Get the package content relation
			AttributedObject[] relatedLinks = ci.getRelations(relationName);
			if(relatedLinks != null){
				int len = relatedLinks.length;
				
				//Go through the relation, if there is any unapproved content, return.
				for(int i = 0; i < len; i ++){
					
					Object obj = relatedLinks[i].getAttributeValue(attributeName);
					if(obj != null){
						ManagedObject mo = ManagedObject.findByContentManagementId(new ManagedObjectVCMRef(obj.toString()));
						if(mo != null){
							if(!mo.getManagedObjectStatus().isApproved()){
								list.add(mo);
							}
						}
					}
					
				}

			}
			
		} catch (ApplicationException e) {
			//logger.error(e.getMessage());
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (ValidationException e) {
			//logger.error(e.getMessage());
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 
		
		return list;
	}
	
	
	/**
	 * Retrieve all associated items
	 * @param vcmId
	 * @return
	 */
	public static List<ManagedObject> getAllReferenceItems(ContentInstance ci){
		
		if(ci == null){
			logger.error("getAllAssociatedItems(ci) ci is null" );
			System.out.println("getAllAssociatedItems(ci) ci is null" );
			return null;
		}
		
    	List<ManagedObject> allitems = new ArrayList<ManagedObject>();
    	
    	//check for itself
		if(!ci.getManagedObjectStatus().isApproved())
			allitems.add(ci);
		
		//check for quicklinks
		List<ManagedObject> list = getReferencedItems(ci, Constants.RELATION_RELATED_QUICKLINKS, Constants.ATTR_RELATED_QUICKLINKS_REFERENCE);
		if(list.size() >0)
			allitems.addAll(list);
		
		//check for footnotes
		list = getReferencedItems(ci, Constants.RELATION_RELATED_FOOTNOTES, Constants.ATTR_RELATED_FOOTNOTES_REFERENCE);
		if(list.size() >0)
			allitems.addAll(list);
		
		//check for teasers
		list = getReferencedItems(ci, Constants.RELATION_RELATED_TEASERS, Constants.ATTR_RELATED_TEASERS_REFERENCE);
		if(list.size() >0)
			allitems.addAll(list);
		
    	return allitems;
    }
	
	/**
	 * Retrieve all associated items
	 * @param vcmId
	 * @return
	 */
	public static String checkRelatedContent(ContentInstance ci){
		if(ci == null){
			logger.error("checkContent is null" );
			System.out.println("checkContent is null" );
			return null;
		}
		
		StringBuffer sb = new StringBuffer("");
		
		List<ManagedObject> items = getAllReferenceItems(ci);
		if(items != null && items.size() >0){
			int size = items.size();
			for(int i = 0; i < size; i++){
				ManagedObject mo = items.get(i);
				try {
					sb.append("Item " + (i+1) + " ( ID: " + mo.getContentManagementId().getId() 
							+ ", Name: " + mo.getName() + ")" + lineBreak);
					
				} catch (ApplicationException e) {
					//logger.error(e.getMessage());
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
				
			}
		}		
		
    	return sb.toString();
    }

}
