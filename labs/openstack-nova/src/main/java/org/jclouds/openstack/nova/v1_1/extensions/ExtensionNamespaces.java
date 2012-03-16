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
package org.jclouds.openstack.nova.v1_1.extensions;

/**
 * Extension namespaces
 * 
 * @author Adrian Cole
 * @see <a href= "http://nova.openstack.org/api_ext/" />
 */
public interface ExtensionNamespaces {
   public static final String KEYPAIRS = "http://docs.openstack.org/ext/keypairs/api/v1.1";
   public static final String VOLUMES = "http://docs.openstack.org/ext/volumes/api/v1.1";
   public static final String SECURITY_GROUPS = "http://docs.openstack.org/ext/securitygroups/api/v1.1";
   public static final String FLOATING_IPS = "http://docs.openstack.org/ext/floating_ips/api/v1.1";
}
