/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
}
