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

import org.jclouds.cloudstack.features.GlobalAccountAsyncClient;
import org.jclouds.cloudstack.features.GlobalAlertAsyncClient;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides asynchronous access to CloudStack via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 * @see CloudStackDomainClient
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html"
 *      />
 */
public interface CloudStackGlobalAsyncClient extends CloudStackDomainAsyncClient {

   /**
    * Provides asynchronous access to Accounts
    */
   @Delegate
   @Override
   GlobalAccountAsyncClient getAccountClient();

   /**
    * Provides asynchronous access to Alerts
    */
   @Delegate
   GlobalAlertAsyncClient getAlertClient();

}
