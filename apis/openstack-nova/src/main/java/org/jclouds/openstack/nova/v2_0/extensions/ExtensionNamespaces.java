/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Name 2.0 (the
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
package org.jclouds.openstack.nova.v2_0.extensions;

/**
 * Extension namespaces
 *
 * @author Adrian Cole
 * @see <a href= "http://nova.openstack.org/api_ext/" />
 */
public interface ExtensionNamespaces {
   /**
    * Keypair Support
    */
   public static final String KEYPAIRS = "http://docs.openstack.org/ext/keypairs/api/v1.1";
   /**
    * Volumes support
    */
   public static final String VOLUMES = "http://docs.openstack.org/ext/volumes/api/v1.1";
   /**
    * Volume types support
    */
   public static final String VOLUME_TYPES = "http://docs.openstack.org/ext/volume_types/api/v1.1";
   /**
    * Security group support
    */
   public static final String SECURITY_GROUPS = "http://docs.openstack.org/ext/securitygroups/api/v1.1";
   /**
    * Floating IPs support
    */
   public static final String FLOATING_IPS = "http://docs.openstack.org/ext/floating_ips/api/v1.1";
   /**
    * Multiple network support
    */
   public static final String MULTINIC = "http://docs.openstack.org/ext/multinic/api/v1.1";
   /**
    * Host administration
    */
   public static final String HOSTS = "http://docs.openstack.org/ext/hosts/api/v1.1";
   /**
    * Quotas management support
    */
   public static final String QUOTAS = "http://docs.openstack.org/ext/quotas-sets/api/v1.1";
   /**
    * Instance type (flavor) extra specs
    */
   public static final String FLAVOR_EXTRA_SPECS = "http://docs.openstack.org/ext/flavor_extra_specs/api/v1.1";
   /**
    * Provide additional data for flavors
    */
   public static final String FLAVOR_EXTRA_DATA = "http://docs.openstack.org/ext/flavor_extra_data/api/v1.1";
   /**
    * Virtual interface support
    */
   public static final String VIRTUAL_INTERFACES = "http://docs.openstack.org/ext/virtual_interfaces/api/v1.1";
   /**
    * Extended support to the Create Server v1.1 API
    */
   public static final String CREATESERVEREXT = "http://docs.openstack.org/ext/createserverext/api/v1.1";
   /**
    * Virtual Storage Arrays support
    */
   public static final String VSA = "http://docs.openstack.org/ext/vsa/api/v1.1";
   /**
    * Simple tenant usage extension
    */
   public static final String SIMPLE_TENANT_USAGE = "http://docs.openstack.org/ext/os-simple-tenant-usage/api/v1.1";
   /**
    * Instance rescue mode
    */
   public static final String RESCUE = "http://docs.openstack.org/ext/rescue/api/v1.1";
   /**
    * Admin Action extension
    */
   public static final String ADMIN_ACTIONS = "http://docs.openstack.org/ext/admin-actions/api/v1.1";

   /**
    * Extended Server Status extension
    */
   public static final String EXTENDED_STATUS = "http://docs.openstack.org/compute/ext/extended_status/api/v1.1";

   /**
    * Quota Classes extension
    */
   public static final String QUOTA_CLASSES = "http://docs.openstack.org/ext/quota-classes-sets/api/v1.1";

   /**
    * Disk Config extension
    */
   public static final String DISK_CONFIG = "http://docs.openstack.org/compute/ext/disk_config/api/v1.1";

   /**
    * Aggregates extension
    */
   public static final String AGGREGATES = "http://docs.openstack.org/ext/aggregates/api/v1.1";
}