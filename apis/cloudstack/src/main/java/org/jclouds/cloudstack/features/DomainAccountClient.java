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
package org.jclouds.cloudstack.features;

import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;

/**
 * Provides synchronous access to CloudStack Account features available to Domain
 * Admin users.
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Domain_Admin.html"
 *      />
 */
public interface DomainAccountClient extends AccountClient {

   /**
    * Enable an account
    *
    * @param accountName
    *    the account name you are enabling
    * @param domainId
    *    the domain ID
    */
   Account enableAccount(String accountName, String domainId);

   /**
    * Disable or lock an account
    *
    * @param accountName
    *    the account name you are disabling
    * @param domainId
    *    the domain ID
    * @param onlyLock
    *    only lock if true disable otherwise
    */
   AsyncCreateResponse disableAccount(String accountName, String domainId, boolean onlyLock);

}
