/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
   public static final String PUBLICIP_XML = "application/vnd.tmrk.ecloud.publicIp+xml";

   /**
    * "application/vnd.tmrk.ecloud.publicIp+xml"
    */
   public static final MediaType PUBLICIP_XML_TYPE = new MediaType("application", "vnd.tmrk.ecloud.publicIp+xml");

   /**
    * "application/vnd.tmrk.ecloud.internetService+xml"
    */
   public static final String INTERNETSERVICE_XML = "application/vnd.tmrk.ecloud.internetService+xml";

   /**
    * "application/vnd.tmrk.ecloud.internetService+xml"
    */
   public static final MediaType INTERNETSERVICE_XML_TYPE = new MediaType("application",
         "vnd.tmrk.ecloud.internetService+xml");
   /**
    * "application/vnd.tmrk.ecloud.internetServicesList+xml"
    */
   public static final String INTERNETSERVICESLIST_XML = "application/vnd.tmrk.ecloud.internetServicesList+xml";

   /**
    * "application/vnd.tmrk.ecloud.internetServicesList+xml"
    */
   public static final MediaType INTERNETSERVICESLIST_XML_TYPE = new MediaType("application",
         "vnd.tmrk.ecloud.internetServicesList+xml");

   /**
    * "application/vnd.tmrk.ecloud.keysList+xml"
    */
   public static final String KEYSLIST_XML = "application/vnd.tmrk.ecloud.keysList+xml";

   /**
    * "application/vnd.tmrk.ecloud.keysList+xml"
    */
   public static final MediaType KEYSLIST_XML_TYPE = new MediaType("application", "vnd.tmrk.ecloud.keysList+xml");
   /**
    * "application/vnd.tmrk.ecloud.tagsList+xml"
    */
   public static final String TAGSLISTLIST_XML = "application/vnd.tmrk.ecloud.tagsList+xml";

   /**
    * "application/vnd.tmrk.ecloud.tagsList+xml"
    */
   public static final MediaType TAGSLISTLIST_XML_TYPE = new MediaType("application", "vnd.tmrk.ecloud.tagsList+xml");
   /**
    * "application/vnd.tmrk.ecloud.VAppCatalogList+xml"
    */
   public static final String VAPPCATALOGLIST_XML = "application/vnd.tmrk.ecloud.VAppCatalogList+xml";

   /**
    * "application/vnd.tmrk.ecloud.VAppCatalogList+xml"
    */
   public static final MediaType VAPPCATALOGLIST_XML_TYPE = new MediaType("application",
         "vnd.tmrk.ecloud.VAppCatalogList+xml");

   /**
    * "application/vnd.tmrk.ecloud.dataCentersList+xml"
    */
   public static final String DATACENTERSLIST_XML = "application/vnd.tmrk.ecloud.dataCentersList+xml";

   /**
    * "application/vnd.tmrk.ecloud.dataCentersList+xml"
    */
   public static final MediaType DATACENTERSLIST_XML_TYPE = new MediaType("application",
         "vnd.tmrk.ecloud.dataCentersList+xml");

   /**
    * "application/vnd.tmrk.ecloud.ipAddressList+xml"
    */
   public static final String IPADDRESS_LIST_XML = "application/vnd.tmrk.ecloud.ipAddressList+xml";

   /**
    * "application/vnd.tmrk.ecloud.ipAddressList+xml"
    */
   public static final MediaType IPADDRESSES_LIST_XML_TYPE = new MediaType("application",
         "vnd.tmrk.ecloud.ipAddressList+xml");

   /**
    * "application/vnd.tmrk.ecloud.vApp+xml"
    */
   public static final String VAPPEXTINFO_XML = "application/vnd.tmrk.ecloud.vApp+xml";

   /**
    * "application/vnd.tmrk.ecloud.vApp+xml"
    */
   public static final MediaType VAPPEXTINFO_XML_TYPE = new MediaType("application", "vnd.tmrk.ecloud.vApp+xml");
}
