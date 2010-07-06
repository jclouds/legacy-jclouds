package org.jclouds.vcloud.terremark;

import javax.ws.rs.core.MediaType;

/**
 * Resource Types used in Terremark VCloud express
 * 
 * @see MediaType
 */
public interface TerremarkVCloudExpressMediaType extends TerremarkVCloudMediaType {
   /**
    * "application/vnd.tmrk.vcloudExpress.keysList+xml"
    */
   public final static String KEYSLIST_XML = "application/vnd.tmrk.vcloudExpress.keysList+xml";

   /**
    * "application/vnd.tmrk.vCloudExpress.keysList+xml"
    */
   public final static MediaType KEYSLIST_XML_TYPE = new MediaType("application",
            "vnd.tmrk.vcloudExpress.keysList+xml");

}
