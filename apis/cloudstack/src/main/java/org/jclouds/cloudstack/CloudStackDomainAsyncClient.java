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
package org.jclouds.cloudstack;

import org.jclouds.cloudstack.features.DomainAccountAsyncClient;
import org.jclouds.cloudstack.features.DomainDomainAsyncClient;
import org.jclouds.cloudstack.features.DomainLimitAsyncClient;
import org.jclouds.cloudstack.features.DomainUserAsyncClient;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides asynchronous access to CloudStack via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 * @see CloudStackDomainClient
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Domain_Admin.html"
 *      />
 * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(CloudStackDomainClient.class)} as
 *             {@link CloudStackDomainAsyncClient} interface will be removed in jclouds 1.7.
 */
@Deprecated
public interface CloudStackDomainAsyncClient extends CloudStackAsyncClient {

   /**
    * Provides synchronous access to Resource Limits
    */
   @Delegate
   @Override
   DomainLimitAsyncClient getLimitClient();

   /**
    * Provides synchronous access to Accounts
    */
   @Delegate
   @Override
   DomainAccountAsyncClient getAccountClient();

   /**
    * Provides asynchronous access to Users
    */
   @Delegate
   DomainUserAsyncClient getUserClient();

   /**
    * Provides asynchronous access to Domains
    */
   @Delegate
   DomainDomainAsyncClient getDomainClient();

}
