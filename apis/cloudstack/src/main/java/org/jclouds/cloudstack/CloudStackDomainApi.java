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

import org.jclouds.cloudstack.features.DomainAccountApi;
import org.jclouds.cloudstack.features.DomainDomainApi;
import org.jclouds.cloudstack.features.DomainLimitApi;
import org.jclouds.cloudstack.features.DomainUserApi;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides synchronous access to CloudStack.
 * <p/>
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Domain_Admin.html"
 *      />
 */
public interface CloudStackDomainApi extends CloudStackApi {

   /**
    * Provides synchronous access to Resource Limits
    */
   @Delegate
   @Override
   DomainLimitApi getLimitApi();
   
   /**
    * Provides synchronous access to Accounts
    */
   @Delegate
   @Override
   DomainAccountApi getAccountApi();

   /**
    * Provides synchronous access to Users
    */
   @Delegate
   DomainUserApi getUserClient();

   /**
    * Provides synchronous access to Domains
    */
   @Delegate
   DomainDomainApi getDomainClient();
}
