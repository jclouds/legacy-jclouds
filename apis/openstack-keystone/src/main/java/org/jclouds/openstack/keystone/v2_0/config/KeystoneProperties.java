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
package org.jclouds.openstack.keystone.v2_0.config;

import org.jclouds.openstack.v2_0.ServiceType;

/**
 * Configuration properties and constants used in Keystone connections.
 * 
 * @author Adrian Cole
 */
public interface KeystoneProperties {

   /**
    * Type of credentials used to log into the auth service.
    * 
    * <h3>valid values</h3>
    * <ul>
    * <li>apiAccessKeyCredentials</li>
    * <li>passwordCredentials</li>
    * </ul>
    * 
    * @see CredentialTypes
    * @see <a href="http://docs.openstack.org/api/openstack-identity-service/2.0/content/POST_authenticate_v2.0_tokens_Service_API_Client_Operations.html"
    *      />
    */
   public static final String CREDENTIAL_TYPE = "jclouds.keystone.credential-type";
   
   /**
    * set this property to specify the tenant id of the authenticated user.  Cannot be used simultaneously with {@link #TENANT_NAME}
    * @see <a href="http://wiki.openstack.org/CLIAuth">openstack docs</a>
    */
   public static final String TENANT_ID = "jclouds.keystone.tenant-id";
   
   /**
    * set this property to specify the tenant name of the authenticated user.  Cannot be used simultaneously with {@link #TENANT_ID}
    * @see <a href="http://wiki.openstack.org/CLIAuth">openstack docs</a>
    */
   public static final String TENANT_NAME = "jclouds.keystone.tenant-name";
   
   /**
    * set this property to {@code true} to designate that the service requires explicit specification of either {@link #TENANT_NAME} or {@link #TENANT_ID}
    * @see <a href="http://wiki.openstack.org/CLIAuth">openstack docs</a>
    */
   public static final String REQUIRES_TENANT = "jclouds.keystone.requires-tenant";
   
   /**
    * version of the keystone service
    */
   public static final String VERSION = "jclouds.keystone.version";

   /**
    * type of the keystone service. ex. {@code compute}
    * 
    * @see ServiceType
    */
   public static final String SERVICE_TYPE = "jclouds.keystone.service-type";
}
