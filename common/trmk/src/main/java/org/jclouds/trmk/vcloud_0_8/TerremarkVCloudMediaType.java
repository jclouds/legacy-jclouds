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
package org.jclouds.trmk.vcloud_0_8;

import javax.ws.rs.core.MediaType;

/**
 * Resource Types used in Terremark VCloud
 * 
 * @see MediaType
 */
public interface TerremarkVCloudMediaType {
   /**
    * "application/vnd.vmware.vcloud.error+xml"
    */
   public static final String ERROR_XML = "application/vnd.vmware.vcloud.error+xml";

   /**
    * "application/vnd.vmware.vcloud.error+xml"
    */
   public static final MediaType ERROR_XML_TYPE = new MediaType("application", "vnd.vmware.vcloud.error+xml");

   /**
    * "application/vnd.vmware.vcloud.vcloud+xml"
    */
   public static final String VCLOUD_XML = "application/vnd.vmware.vcloud.vcloud+xml";

   /**
    * "application/vnd.vmware.vcloud.vcloud+xml"
    */
   public static final MediaType VCLOUD_XML_TYPE = new MediaType("application", "vnd.vmware.vcloud.vcloud+xml");
   /**
    * "application/vnd.vmware.vcloud.org+xml"
    */
   public static final String ORG_XML = "application/vnd.vmware.vcloud.org+xml";
   /**
    * "application/vnd.vmware.vcloud.org+xml"
    */
   public static final MediaType ORG_XML_TYPE = new MediaType("application", "vnd.vmware.vcloud.org+xml");

   /**
    * "application/vnd.vmware.vcloud.vdc+xml"
    */
   public static final String VDC_XML = "application/vnd.vmware.vcloud.vdc+xml";
   /**
    * "application/vnd.vmware.vcloud.vdc+xml"
    */
   public static final MediaType VDC_XML_TYPE = new MediaType("application", "vnd.vmware.vcloud.vdc+xml");

   /**
    * "application/vnd.vmware.vcloud.catalog+xml"
    */
   public static final String CATALOG_XML = "application/vnd.vmware.vcloud.catalog+xml";
   /**
    * "application/vnd.vmware.vcloud.catalog+xml"
    */
   public static final MediaType CATALOG_XML_TYPE = new MediaType("application", "vnd.vmware.vcloud.catalog+xml");

   /**
    * "application/vnd.vmware.vcloud.tasksList+xml"
    */
   public static final String TASKSLIST_XML = "application/vnd.vmware.vcloud.tasksList+xml";
   /**
    * "application/vnd.vmware.vcloud.tasksList+xml"
    */
   public static final MediaType TASKSLIST_XML_TYPE = new MediaType("application", "vnd.vmware.vcloud.tasksList+xml");

   /**
    * "application/vnd.vmware.vcloud.catalogItem+xml"
    */
   public static final String CATALOGITEM_XML = "application/vnd.vmware.vcloud.catalogItem+xml";
   /**
    * "application/vnd.vmware.vcloud.catalogItem+xml"
    */
   public static final MediaType CATALOGITEM_XML_TYPE = new MediaType("application",
         "vnd.vmware.vcloud.catalogItem+xml");
   /**
    * "application/vnd.vmware.vcloud.networkConnectionSection+xml"
    */
   public static final String NETWORKCONNECTIONSECTION_XML = "application/vnd.vmware.vcloud.networkConnectionSection+xml";
   /**
    * "application/vnd.vmware.vcloud.networkConnectionSection+xml"
    */
   public static final MediaType NETWORKCONNECTIONSECTION_XML_TYPE = new MediaType("application",
         "vnd.vmware.vcloud.networkConnectionSection+xml");
   /**
    * "application/vnd.vmware.vcloud.virtualHardwareSection+xml"
    */
   public static final String VIRTUALHARDWARESECTION_XML = "application/vnd.vmware.vcloud.virtualHardwareSection+xml";
   /**
    * "application/vnd.vmware.vcloud.virtualHardwareSection+xml"
    */
   public static final MediaType VIRTUALHARDWARESECTION_XML_TYPE = new MediaType("application",
         "vnd.vmware.vcloud.virtualHardwareSection+xml");
   /**
    * "application/vnd.vmware.vcloud.guestCustomizationSection+xml"
    */
   public static final String GUESTCUSTOMIZATIONSECTION_XML = "application/vnd.vmware.vcloud.guestCustomizationSection+xml";
   /**
    * "application/vnd.vmware.vcloud.guestCustomizationSection+xml"
    */
   public static final MediaType GUESTCUSTOMIZATIONSECTION_XML_TYPE = new MediaType("application",
         "vnd.vmware.vcloud.guestCustomizationSection+xml");

   /**
    * "application/vnd.vmware.vcloud.networkSection+xml"
    */
   public static final String NETWORKSECTION_XML = "application/vnd.vmware.vcloud.networkSection+xml";
   /**
    * "application/vnd.vmware.vcloud.networkSection+xml"
    */
   public static final MediaType NETWORKSECTION_XML_TYPE = new MediaType("application",
         "vnd.vmware.vcloud.networkSection+xml");

   /**
    * "application/vnd.vmware.vcloud.task+xml"
    */
   public static final String TASK_XML = "application/vnd.vmware.vcloud.task+xml";
   /**
    * "application/vnd.vmware.vcloud.task+xml"
    */
   public static final MediaType TASK_XML_TYPE = new MediaType("application", "vnd.vmware.vcloud.task+xml");

   /**
    * "application/vnd.vmware.vcloud.undeployVAppParams+xml"
    */
   public static final String UNDEPLOYVAPPPARAMS_XML = "application/vnd.vmware.vcloud.undeployVAppParams+xml";
   /**
    * "application/vnd.vmware.vcloud.undeployVAppParams+xml"
    */
   public static final MediaType UNDEPLOYVAPPPARAMS_XML_TYPE = new MediaType("application",
         "vnd.vmware.vcloud.undeployVAppParams+xml");

   /**
    * "application/vnd.vmware.vcloud.deployVAppParams+xml"
    */
   public static final String DEPLOYVAPPPARAMS_XML = "application/vnd.vmware.vcloud.deployVAppParams+xml";
   /**
    * "application/vnd.vmware.vcloud.deployVAppParams+xml"
    */
   public static final MediaType DEPLOYVAPPPARAMS_XML_TYPE = new MediaType("application",
         "vnd.vmware.vcloud.deployVAppParams+xml");

   /**
    * "application/vnd.vmware.vcloud.vApp+xml"
    */
   public static final String VAPP_XML = "application/vnd.vmware.vcloud.vApp+xml";
   /**
    * "application/vnd.vmware.vcloud.vApp+xml"
    */
   public static final MediaType VAPP_XML_TYPE = new MediaType("application", "vnd.vmware.vcloud.vApp+xml");

   /**
    * "application/vnd.vmware.vcloud.vm+xml"
    */
   public static final String VM_XML = "application/vnd.vmware.vcloud.vm+xml";
   /**
    * "application/vnd.vmware.vcloud.vm+xml"
    */
   public static final MediaType VM_XML_TYPE = new MediaType("application", "vnd.vmware.vcloud.vm+xml");

   /**
    * "application/vnd.vmware.vcloud.vAppTemplate+xml"
    */
   public static final String VAPPTEMPLATE_XML = "application/vnd.vmware.vcloud.vAppTemplate+xml";
   /**
    * "application/vnd.vmware.vcloud.vAppTemplate+xml"
    */
   public static final MediaType VAPPTEMPLATE_XML_TYPE = new MediaType("application",
         "vnd.vmware.vcloud.vAppTemplate+xml");
   /**
    * "application/vnd.vmware.vcloud.network+xml"
    */
   public static final String NETWORK_XML = "application/vnd.vmware.vcloud.network+xml";
   /**
    * "application/vnd.vmware.vcloud.network+xml"
    */
   public static final MediaType NETWORK_XML_TYPE = new MediaType("application", "vnd.vmware.vcloud.network+xml");

   /**
    * "application/vnd.vmware.vcloud.rasdItem+xml"
    */
   public static final String RASDITEM_XML = "application/vnd.vmware.vcloud.rasdItem+xml";
   /**
    * "application/vnd.vmware.vcloud.rasdItem+xml"
    */
   public static final MediaType RASDITEM_XML_TYPE = new MediaType("application", "vnd.vmware.vcloud.rasdItem+xml");

   /**
    * "application/vnd.vmware.vcloud.organizationList+xml"
    */
   public static final String ORGLIST_XML = "application/vnd.vmware.vcloud.orgList+xml";

   /**
    * "application/vnd.vmware.vcloud.organizationList+xml"
    */
   public static final MediaType ORGLIST_XML_TYPE = new MediaType("application",
         "vnd.vmware.vcloud.orgList+xml");

   /**
    * "application/vnd.tmrk.vCloud.publicIp+xml"
    */
   public static final String PUBLICIP_XML = "application/vnd.tmrk.vCloud.publicIp+xml";

   /**
    * "application/vnd.tmrk.vCloud.publicIp+xml"
    */
   public static final MediaType PUBLICIP_XML_TYPE = new MediaType("application", "vnd.tmrk.vCloud.publicIp+xml");

   /**
    * "application/vnd.tmrk.vCloud.publicIpsList+xml"
    */
   public static final String PUBLICIPSLIST_XML = "application/vnd.tmrk.vCloud.publicIpsList+xml";

   /**
    * "application/vnd.tmrk.vCloud.publicIpsList+xml"
    */
   public static final MediaType PUBLICIPSLIST_XML_TYPE = new MediaType("application",
         "vnd.tmrk.vCloud.publicIpsList+xml");

   /**
    * "application/vnd.tmrk.vCloud.internetService+xml"
    */
   public static final String INTERNETSERVICE_XML = "application/vnd.tmrk.vCloud.internetService+xml";

   /**
    * "application/vnd.tmrk.vCloud.internetService+xml"
    */
   public static final MediaType INTERNETSERVICE_XML_TYPE = new MediaType("application",
         "vnd.tmrk.vCloud.internetService+xml");

   /**
    * "application/vnd.tmrk.vCloud.internetServicesList+xml"
    */
   public static final String INTERNETSERVICESLIST_XML = "application/vnd.tmrk.vCloud.internetServicesList+xml";

   /**
    * "application/vnd.tmrk.vCloud.internetServicesList+xml"
    */
   public static final MediaType INTERNETSERVICESLIST_XML_TYPE = new MediaType("application",
         "vnd.tmrk.vCloud.internetServicesList+xml");

   /**
    * "application/vnd.tmrk.vCloud.nodeService+xml"
    */
   public static final String NODESERVICE_XML = "application/vnd.tmrk.vCloud.nodeService+xml";

   /**
    * "application/vnd.tmrk.vCloud.nodeService+xml"
    */
   public static final MediaType NODESERVICE_XML_TYPE = new MediaType("application", "vnd.tmrk.vCloud.nodeService+xml");

   /**
    * "application/vnd.tmrk.vCloud.catalogItemCustomizationParameters+xml"
    */
   public static final String CATALOGITEMCUSTOMIZATIONPARAMETERS_XML = "application/vnd.tmrk.vCloud.catalogItemCustomizationParameters+xml";

   /**
    * "application/vnd.tmrk.vCloud.catalogItemCustomizationParameters+xml"
    */
   public static final MediaType CATALOGITEMCUSTOMIZATIONPARAMETERS_XML_TYPE = new MediaType("application",
         "vnd.tmrk.vCloud.catalogItemCustomizationParameters+xml");
}
