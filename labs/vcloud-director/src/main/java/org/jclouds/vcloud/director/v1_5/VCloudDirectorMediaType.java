/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.vcloud.director.v1_5;

/**
 * Resource Types used in VCloud.
 * 
 * The object type, specified as a MIME content type, of the object that the link references. This
 * attribute is present only for links to objects. It is not present for links to actions.
 * 
 * @see javax.ws.rs.core.MediaType;
 */
public class VCloudDirectorMediaType {

   public static final String ANY = "*/*";

   public static final String SESSION = "application/vnd.vmware.vcloud.session+xml";

   public static final String ERROR = "application/vnd.vmware.vcloud.error+xml";

   public static final String ORG_LIST = "application/vnd.vmware.vcloud.orgList+xml";

   public static final String METADATA = "application/vnd.vmware.vcloud.metadata+xml";

   public static final String METADATA_ENTRY = "*/*"; // TODO

   public static final String ORG = "application/vnd.vmware.vcloud.org+xml";

   public static final String TASKS_LIST = "application/vnd.vmware.vcloud.tasksList+xml";

   public static final String TASK = "application/vnd.vmware.vcloud.task+xml";

   public static final String ORG_NETWORK = "application/vnd.vmware.vcloud.orgNetwork+xml";

   public static final String CATALOG = "application/vnd.vmware.vcloud.catalog+xml";

   public static final String CATALOG_ITEM = "application/vnd.vmware.vcloud.catalogItem+xml";

   public static final String CATALOG_ITEMS = "application/vnd.vmware.vcloud.catalogItems+xml";

   public static final String CATALOGS_LIST = "application/vnd.vmware.vcloud.catalogsList+xml";

   public static final String PROPERTY = "application/vnd.vmware.vcloud.property+xml";

}
