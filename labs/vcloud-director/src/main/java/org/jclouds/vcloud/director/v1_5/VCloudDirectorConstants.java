/*
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

   public static final String VCLOUD_VMW_NS = "ihttp://www.vmware.com/schema/ovf";

   public static final String VCLOUD_OVF_NS = "http://schemas.dmtf.org/ovf/envelope/1";

   public static final String VCLOUD_OVF_ENV_NS = "http://schemas.dmtf.org/ovf/environment/1";

   public static final String VCLOUD_CIM_NS = "http://schemas.dmtf.org/wbem/wscim/1/common";

   /** The property used to configure the timeout for task completion. */
   public static final String PROPERTY_VCLOUD_DIRECTOR_TIMEOUT_TASK_COMPLETED = "jclouds.vcloud-director.timeout.task-complete";

   public static final String PROPERTY_VCLOUD_DIRECTOR_VERSION_SCHEMA = "jclouds.vcloud-director.version.schema";

   /** Name of the default org that your vApp will join, if an org isn't explicitly specified. */
   public static final String PROPERTY_VCLOUD_DIRECTOR_DEFAULT_ORG = "jclouds.vcloud-director.defaults.org";

   /** Name of the default catalog to query, if it isn't explicitly specified. */
   public static final String PROPERTY_VCLOUD_DIRECTOR_DEFAULT_CATALOG = "jclouds.vcloud-director.defaults.catalog";

   /** Name of the VDC that your vApp will join, if a vDC isn't explicitly specified. */
   public static final String PROPERTY_VCLOUD_DIRECTOR_DEFAULT_VDC = "jclouds.vcloud-director.defaults.vdc";

   /** Name of the default network, in the default VDC that your vApp will join. */
   public static final String PROPERTY_VCLOUD_DIRECTOR_DEFAULT_NETWORK = "jclouds.vcloud-director.defaults.network";

   /** TODO javadoc */
   // public static final String PROPERTY_VCLOUD_DEFAULT_FENCEMODE = "jclouds.vcloud-director.defaults.fencemode";

   /** TODO javadoc */
   public static final String PROPERTY_VCLOUD_DIRECTOR_XML_NAMESPACE = "jclouds.vcloud-director.xml.ns";
   
   /** TODO javadoc */
   public static final String PROPERTY_VCLOUD_DIRECTOR_XML_SCHEMA = "jclouds.vcloud-director.xml.schema";

   // TODO put these somewhere else, maybe core?

   /** TODO javadoc */
   public static final String PROPERTY_DNS_NAME_LEN_MIN = "jclouds.dns_name_length_min";

   /** TODO javadoc */
   public static final String PROPERTY_NS_NAME_LEN_MAX = "jclouds.dns_name_length_max";
}
