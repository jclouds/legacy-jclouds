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

package org.jclouds.vcloud;

import javax.ws.rs.core.MediaType;

/**
 * Resource Types used in VCloud express
 * 
 * @see MediaType
 */
public interface VCloudMediaType {
   /**
    * "application/vnd.vmware.vcloud.organizationList+xml"
    */
   public final static String VCLOUD_XML = "application/vnd.vmware.vcloud.vcloud+xml";

   /**
    * "application/vnd.vmware.vcloud.organizationList+xml"
    */
   public final static MediaType VCLOUD_XML_TYPE = new MediaType("application",
            "vnd.vmware.vcloud.vcloud+xml");
   /**
    * "application/vnd.vmware.vcloud.organizationList+xml"
    */
   public final static String ORGLIST_XML = "application/vnd.vmware.vcloud.organizationList+xml";

   /**
    * "application/vnd.vmware.vcloud.organizationList+xml"
    */
   public final static MediaType ORGLIST_XML_TYPE = new MediaType("application",
            "vnd.vmware.vcloud.organizationList+xml");
   /**
    * "application/vnd.vmware.vcloud.org+xml"
    */
   public final static String ORG_XML = "application/vnd.vmware.vcloud.org+xml";
   /**
    * "application/vnd.vmware.vcloud.org+xml"
    */
   public final static MediaType ORG_XML_TYPE = new MediaType("application",
            "vnd.vmware.vcloud.org+xml");

   /**
    * "application/vnd.vmware.vcloud.vdc+xml"
    */
   public final static String VDC_XML = "application/vnd.vmware.vcloud.vdc+xml";
   /**
    * "application/vnd.vmware.vcloud.vdc+xml"
    */
   public final static MediaType VDC_XML_TYPE = new MediaType("application",
            "vnd.vmware.vcloud.vdc+xml");

   /**
    * "application/vnd.vmware.vcloud.catalog+xml"
    */
   public final static String CATALOG_XML = "application/vnd.vmware.vcloud.catalog+xml";
   /**
    * "application/vnd.vmware.vcloud.catalog+xml"
    */
   public final static MediaType CATALOG_XML_TYPE = new MediaType("application",
            "vnd.vmware.vcloud.catalog+xml");

   /**
    * "application/vnd.vmware.vcloud.tasksList+xml"
    */
   public final static String TASKSLIST_XML = "application/vnd.vmware.vcloud.tasksList+xml";
   /**
    * "application/vnd.vmware.vcloud.tasksList+xml"
    */
   public final static MediaType TASKSLIST_XML_TYPE = new MediaType("application",
            "vnd.vmware.vcloud.tasksList+xml");

   /**
    * "application/vnd.vmware.vcloud.catalogItem+xml"
    */
   public final static String CATALOGITEM_XML = "application/vnd.vmware.vcloud.catalogItem+xml";
   /**
    * "application/vnd.vmware.vcloud.catalogItem+xml"
    */
   public final static MediaType CATALOGITEM_XML_TYPE = new MediaType("application",
            "vnd.vmware.vcloud.catalogItem+xml");

   /**
    * "application/vnd.vmware.vcloud.task+xml"
    */
   public final static String TASK_XML = "application/vnd.vmware.vcloud.task+xml";
   /**
    * "application/vnd.vmware.vcloud.task+xml"
    */
   public final static MediaType TASK_XML_TYPE = new MediaType("application",
            "vnd.vmware.vcloud.task+xml");

   /**
    * "application/vnd.vmware.vcloud.vApp+xml"
    */
   public final static String VAPP_XML = "application/vnd.vmware.vcloud.vApp+xml";
   /**
    * "application/vnd.vmware.vcloud.vApp+xml"
    */
   public final static MediaType VAPP_XML_TYPE = new MediaType("application",
            "vnd.vmware.vcloud.vApp+xml");

   /**
    * "application/vnd.vmware.vcloud.vAppTemplate+xml"
    */
   public final static String VAPPTEMPLATE_XML = "application/vnd.vmware.vcloud.vAppTemplate+xml";
   /**
    * "application/vnd.vmware.vcloud.vAppTemplate+xml"
    */
   public final static MediaType VAPPTEMPLATE_XML_TYPE = new MediaType("application",
            "vnd.vmware.vcloud.vAppTemplate+xml");

   /**
    * "application/vnd.vmware.vcloud.network+xml"
    */
   public final static String NETWORK_XML = "application/vnd.vmware.vcloud.network+xml";
   /**
    * "application/vnd.vmware.vcloud.network+xml"
    */
   public final static MediaType NETWORK_XML_TYPE = new MediaType("application",
            "vnd.vmware.vcloud.network+xml");

}
