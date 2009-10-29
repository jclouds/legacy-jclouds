/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.vcloudx;

import javax.ws.rs.core.MediaType;

/**
 * Resource Types used in VCloud express
 * 
 * @see MediaType
 */
public class VCloudXMediaType {

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

}
