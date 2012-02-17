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
 * Constants used by VCloudDirector clients
 * 
 * @author grkvlt@apache.org
 */
public class VCloudDirectorConstants {

   /** The XML namespace used by the clients. */
   public static final String VCLOUD_1_5_NS = "http://www.vmware.com/vcloud/v1.5";

   /** The property used to configure the timeout for task completion. */
   public static final String PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED = "jclouds.vcloud-director.timeout.task-complete";

   public static final String PROPERTY_VCLOUD_VERSION_SCHEMA = "jclouds.vcloud.version.schema";

   /** Name of the default org that your vApp will join, if an org isn't explicitly specified. */
   public static final String PROPERTY_VCLOUD_DEFAULT_ORG = "jclouds.vcloud.defaults.org";

   /** Name of the default catalog to query, if it isn't explicitly specified. */
   public static final String PROPERTY_VCLOUD_DEFAULT_CATALOG = "jclouds.vcloud.defaults.catalog";

   /** Name of the VDC that your vApp will join, if a vDC isn't explicitly specified. */
   public static final String PROPERTY_VCLOUD_DEFAULT_VDC = "jclouds.vcloud.defaults.vdc";

   /** Name of the default network, in the default VDC that your vApp will join. */
   public static final String PROPERTY_VCLOUD_DEFAULT_NETWORK = "jclouds.vcloud.defaults.network";

   /** TODO javadoc */
   // public static final String PROPERTY_VCLOUD_DEFAULT_FENCEMODE = "jclouds.vcloud.defaults.fencemode";

   /** TODO javadoc */
   public static final String PROPERTY_VCLOUD_XML_NAMESPACE = "jclouds.vcloud.xml.ns";

   /** TODO javadoc */
   public static final String PROPERTY_VCLOUD_XML_SCHEMA = "jclouds.vcloud.xml.schema";

}
