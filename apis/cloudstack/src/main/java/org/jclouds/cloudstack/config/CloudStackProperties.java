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
package org.jclouds.cloudstack.config;


/**
 * Configuration properties and constants used in CloudStack connections.
 * 
 * @author Adrian Cole
 */
public interface CloudStackProperties {

   /**
    * Type of credentials specified during {@link ComputeServiceContextBuilder#overrides}. If
    * {@link CredentialType#API_ACCESS_KEY_CREDENTIALS}, the request signing is used. If
    * {@link CredentialType#PASSWORD_CREDENTIALS}, login will happen and a session will be
    * persisted.
    * 
    * <h3>valid values</h3>
    * <ul>
    * <li>apiAccessKeyCredentials</li>
    * <li>passwordCredentials</li>
    * </ul>
    * 
    * @see CredentialType
    * @see <a href="http://docs.cloud.com/CloudStack_Documentation/Customizing_the_CloudStack_UI#Cross_Site_Request_Forgery_%28CSRF%29"
    *      />
    */
   public static final String CREDENTIAL_TYPE = "jclouds.cloudstack.credential-type";

   /**
    * Whenever a node is created, automatically generate keypairs for groups, as needed, also
    * delete the keypair(s) when the last node in the group is destroyed.
    */
   public static final String AUTO_GENERATE_KEYPAIRS = "jclouds.cloudstack.auto-generate-keypairs";
}
