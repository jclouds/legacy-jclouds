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
package org.jclouds.trmk.ecloud;

import javax.ws.rs.core.MediaType;

import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType;

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

   /**
    * "application/vnd.tmrk.ecloud.keysList+xml"
    */
   public final static String KEYSLIST_XML = "application/vnd.tmrk.ecloud.keysList+xml";

   /**
    * "application/vnd.tmrk.ecloud.keysList+xml"
    */
   public final static MediaType KEYSLIST_XML_TYPE = new MediaType("application", "vnd.tmrk.ecloud.keysList+xml");
   /**
    * "application/vnd.tmrk.ecloud.tagsList+xml"
    */
   public final static String TAGSLISTLIST_XML = "application/vnd.tmrk.ecloud.tagsList+xml";

   /**
    * "application/vnd.tmrk.ecloud.tagsList+xml"
    */
   public final static MediaType TAGSLISTLIST_XML_TYPE = new MediaType("application", "vnd.tmrk.ecloud.tagsList+xml");
   /**
    * "application/vnd.tmrk.ecloud.VAppCatalogList+xml"
    */
   public final static String VAPPCATALOGLIST_XML = "application/vnd.tmrk.ecloud.VAppCatalogList+xml";

   /**
    * "application/vnd.tmrk.ecloud.VAppCatalogList+xml"
    */
   public final static MediaType VAPPCATALOGLIST_XML_TYPE = new MediaType("application",
         "vnd.tmrk.ecloud.VAppCatalogList+xml");

   /**
    * "application/vnd.tmrk.ecloud.dataCentersList+xml"
    */
   public final static String DATACENTERSLIST_XML = "application/vnd.tmrk.ecloud.dataCentersList+xml";

   /**
    * "application/vnd.tmrk.ecloud.dataCentersList+xml"
    */
   public final static MediaType DATACENTERSLIST_XML_TYPE = new MediaType("application",
         "vnd.tmrk.ecloud.dataCentersList+xml");

   /**
    * "application/vnd.tmrk.ecloud.ipAddressList+xml"
    */
   public final static String IPADDRESS_LIST_XML = "application/vnd.tmrk.ecloud.ipAddressList+xml";

   /**
    * "application/vnd.tmrk.ecloud.ipAddressList+xml"
    */
   public final static MediaType IPADDRESSES_LIST_XML_TYPE = new MediaType("application",
         "vnd.tmrk.ecloud.ipAddressList+xml");

   /**
    * "application/vnd.tmrk.ecloud.vApp+xml"
    */
   public final static String VAPPEXTINFO_XML = "application/vnd.tmrk.ecloud.vApp+xml";

   /**
    * "application/vnd.tmrk.ecloud.vApp+xml"
    */
   public final static MediaType VAPPEXTINFO_XML_TYPE = new MediaType("application", "vnd.tmrk.ecloud.vApp+xml");
}
