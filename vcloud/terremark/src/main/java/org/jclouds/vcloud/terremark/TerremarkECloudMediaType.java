package org.jclouds.vcloud.terremark;

import javax.ws.rs.core.MediaType;

/**
 * Resource Types used in Terremark eCloud
 * 
 * @see MediaType
 */
public interface TerremarkECloudMediaType extends TerremarkVCloudMediaType {
   /**
    * "application/vnd.tmrk.ecloud.publicIp+xml"
    */
   public final static String PUBLICIP_XML = "application/vnd.tmrk.ecloud.publicIp+xml";

   /**
    * "application/vnd.tmrk.ecloud.publicIp+xml"
    */
   public final static MediaType PUBLICIP_XML_TYPE = new MediaType("application", "vnd.tmrk.ecloud.publicIp+xml");
  
   /**
    * "application/vnd.tmrk.ecloud.internetService+xml"
    */
   public final static String INTERNETSERVICE_XML = "application/vnd.tmrk.ecloud.internetService+xml";

   /**
    * "application/vnd.tmrk.ecloud.internetService+xml"
    */
   public final static MediaType INTERNETSERVICE_XML_TYPE = new MediaType("application",
         "vnd.tmrk.ecloud.internetService+xml");
   /**
    * "application/vnd.tmrk.ecloud.internetServicesList+xml"
    */
   public final static String INTERNETSERVICESLIST_XML = "application/vnd.tmrk.ecloud.internetServicesList+xml";

   /**
    * "application/vnd.tmrk.ecloud.internetServicesList+xml"
    */
   public final static MediaType INTERNETSERVICESLIST_XML_TYPE = new MediaType("application",
         "vnd.tmrk.ecloud.internetServicesList+xml");

}
