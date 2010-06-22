package org.jclouds.vcloud.terremark;

import javax.ws.rs.core.MediaType;

import org.jclouds.vcloud.VCloudMediaType;

/**
 * Resource Types used in Terremark VCloud express
 * 
 * @see MediaType
 */
public class TerremarkVCloudMediaType extends VCloudMediaType {
   /**
    * "application/vnd.tmrk.vcloudExpress.keysList+xml"
    */
   public final static String KEYSLIST_XML = "application/vnd.tmrk.vcloudExpress.keysList+xml";

   /**
    * "application/vnd.tmrk.vCloudExpress.keysList+xml"
    */
   public final static MediaType KEYSLIST_XML_TYPE = new MediaType(
         "application", "vnd.tmrk.vcloudExpress.keysList+xml");

   /**
    * "application/vnd.tmrk.vCloud.publicIp+xml"
    */
   public final static String PUBLICIP_XML = "application/vnd.tmrk.vCloud.publicIp+xml";

   /**
    * "application/vnd.tmrk.vCloud.publicIp+xml"
    */
   public final static MediaType PUBLICIP_XML_TYPE = new MediaType(
         "application", "vnd.tmrk.vCloud.publicIp+xml");

   /**
    * "application/vnd.tmrk.vCloud.publicIpsList+xml"
    */
   public final static String PUBLICIPSLIST_XML = "application/vnd.tmrk.vCloud.publicIpsList+xml";

   /**
    * "application/vnd.tmrk.vCloud.publicIpsList+xml"
    */
   public final static MediaType PUBLICIPSLIST_XML_TYPE = new MediaType(
         "application", "vnd.tmrk.vCloud.publicIpsList+xml");

   /**
    * "application/vnd.tmrk.vCloud.internetService+xml"
    */
   public final static String INTERNETSERVICE_XML = "application/vnd.tmrk.vCloud.internetService+xml";

   /**
    * "application/vnd.tmrk.vCloud.internetService+xml"
    */
   public final static MediaType INTERNETSERVICE_XML_TYPE = new MediaType(
         "application", "vnd.tmrk.vCloud.internetService+xml");

   /**
    * "application/vnd.tmrk.vCloud.internetServicesList+xml"
    */
   public final static String INTERNETSERVICESLIST_XML = "application/vnd.tmrk.vCloud.internetServicesList+xml";

   /**
    * "application/vnd.tmrk.vCloud.internetServicesList+xml"
    */
   public final static MediaType INTERNETSERVICESLIST_XML_TYPE = new MediaType(
         "application", "vnd.tmrk.vCloud.internetServicesList+xml");

   /**
    * "application/vnd.tmrk.vCloud.nodeService+xml"
    */
   public final static String NODESERVICE_XML = "application/vnd.tmrk.vCloud.nodeService+xml";

   /**
    * "application/vnd.tmrk.vCloud.nodeService+xml"
    */
   public final static MediaType NODESERVICE_XML_TYPE = new MediaType(
         "application", "vnd.tmrk.vCloud.nodeService+xml");

   /**
    * "application/vnd.tmrk.vCloud.catalogItemCustomizationParameters+xml"
    */
   public final static String CATALOGITEMCUSTOMIZATIONPARAMETERS_XML = "application/vnd.tmrk.vCloud.catalogItemCustomizationParameters+xml";

   /**
    * "application/vnd.tmrk.vCloud.catalogItemCustomizationParameters+xml"
    */
   public final static MediaType CATALOGITEMCUSTOMIZATIONPARAMETERS_XML_TYPE = new MediaType(
         "application",
         "vnd.tmrk.vCloud.catalogItemCustomizationParameters+xml");
}
