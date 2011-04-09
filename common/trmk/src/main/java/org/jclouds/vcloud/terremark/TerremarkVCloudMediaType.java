/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.terremark;

import javax.ws.rs.core.MediaType;

import org.jclouds.vcloud.VCloudExpressMediaType;

/**
 * Resource Types used in Terremark VCloud
 * 
 * @see MediaType
 */
public interface TerremarkVCloudMediaType extends VCloudExpressMediaType {
   

   /**
    * "application/vnd.tmrk.vCloud.publicIp+xml"
    */
   public final static String PUBLICIP_XML = "application/vnd.tmrk.vCloud.publicIp+xml";

   /**
    * "application/vnd.tmrk.vCloud.publicIp+xml"
    */
   public final static MediaType PUBLICIP_XML_TYPE = new MediaType("application",
            "vnd.tmrk.vCloud.publicIp+xml");

   /**
    * "application/vnd.tmrk.vCloud.publicIpsList+xml"
    */
   public final static String PUBLICIPSLIST_XML = "application/vnd.tmrk.vCloud.publicIpsList+xml";

   /**
    * "application/vnd.tmrk.vCloud.publicIpsList+xml"
    */
   public final static MediaType PUBLICIPSLIST_XML_TYPE = new MediaType("application",
            "vnd.tmrk.vCloud.publicIpsList+xml");

   /**
    * "application/vnd.tmrk.vCloud.internetService+xml"
    */
   public final static String INTERNETSERVICE_XML = "application/vnd.tmrk.vCloud.internetService+xml";

   /**
    * "application/vnd.tmrk.vCloud.internetService+xml"
    */
   public final static MediaType INTERNETSERVICE_XML_TYPE = new MediaType("application",
            "vnd.tmrk.vCloud.internetService+xml");

   /**
    * "application/vnd.tmrk.vCloud.internetServicesList+xml"
    */
   public final static String INTERNETSERVICESLIST_XML = "application/vnd.tmrk.vCloud.internetServicesList+xml";

   /**
    * "application/vnd.tmrk.vCloud.internetServicesList+xml"
    */
   public final static MediaType INTERNETSERVICESLIST_XML_TYPE = new MediaType("application",
            "vnd.tmrk.vCloud.internetServicesList+xml");

   /**
    * "application/vnd.tmrk.vCloud.nodeService+xml"
    */
   public final static String NODESERVICE_XML = "application/vnd.tmrk.vCloud.nodeService+xml";

   /**
    * "application/vnd.tmrk.vCloud.nodeService+xml"
    */
   public final static MediaType NODESERVICE_XML_TYPE = new MediaType("application",
            "vnd.tmrk.vCloud.nodeService+xml");

   /**
    * "application/vnd.tmrk.vCloud.catalogItemCustomizationParameters+xml"
    */
   public final static String CATALOGITEMCUSTOMIZATIONPARAMETERS_XML = "application/vnd.tmrk.vCloud.catalogItemCustomizationParameters+xml";

   /**
    * "application/vnd.tmrk.vCloud.catalogItemCustomizationParameters+xml"
    */
   public final static MediaType CATALOGITEMCUSTOMIZATIONPARAMETERS_XML_TYPE = new MediaType(
            "application", "vnd.tmrk.vCloud.catalogItemCustomizationParameters+xml");
}
