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
package org.jclouds.cloudstack;

import org.jclouds.cloudstack.features.DomainAccountClient;
import org.jclouds.cloudstack.features.DomainDomainClient;
import org.jclouds.cloudstack.features.DomainLimitClient;
import org.jclouds.cloudstack.features.DomainUserClient;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides synchronous access to CloudStack.
 * <p/>
 * 
 * @author Adrian Cole
 * @see CloudStackDomainAsyncClient
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Domain_Admin.html"
 *      />
 */
public interface CloudStackDomainClient extends CloudStackClient {

   /**
    * Provides synchronous access to Resource Limits
    */
   @Delegate
   @Override
   DomainLimitClient getLimitClient();
   
   /**
    * Provides synchronous access to Accounts
    */
   @Delegate
   @Override
   DomainAccountClient getAccountClient();

   /**
    * Provides synchronous access to Users
    */
   @Delegate
   DomainUserClient getUserClient();

   /**
    * Provides synchronous access to Domains
    */
   @Delegate
   DomainDomainClient getDomainClient();
}
