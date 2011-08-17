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
package org.jclouds.vcloud.reference;

/**
 * Configuration properties and constants used in VCloud connections.
 * 
 * @author Adrian Cole
 */
public interface VCloudConstants {
   public static final String PROPERTY_VCLOUD_VERSION_SCHEMA = "jclouds.vcloud.version.schema";
   /**
    * name of the default org that your vApp will join, if an org isn't
    * explicitly specified.
    */
   public static final String PROPERTY_VCLOUD_DEFAULT_ORG = "jclouds.vcloud.defaults.org";
   /**
    * name of the default catalog to query, if it isn't explicitly specified.
    */
   public static final String PROPERTY_VCLOUD_DEFAULT_CATALOG = "jclouds.vcloud.defaults.catalog";
   /**
    * name of the VDC that your vApp will join, if a vDC isn't explicitly
    * specified.
    */
   public static final String PROPERTY_VCLOUD_DEFAULT_VDC = "jclouds.vcloud.defaults.vdc";
   /**
    * name of the default network, in the default VDC that your vApp will join.
    */
   public static final String PROPERTY_VCLOUD_DEFAULT_NETWORK = "jclouds.vcloud.defaults.network";
   public static final String PROPERTY_VCLOUD_DEFAULT_FENCEMODE = "jclouds.vcloud.defaults.fencemode";

   public static final String PROPERTY_VCLOUD_XML_NAMESPACE = "jclouds.vcloud.xml.ns";
   public static final String PROPERTY_VCLOUD_XML_SCHEMA = "jclouds.vcloud.xml.schema";

   public static final String PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED = "jclouds.vcloud.timeout.task-complete";

}
