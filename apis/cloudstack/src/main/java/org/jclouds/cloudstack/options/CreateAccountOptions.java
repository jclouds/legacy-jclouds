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
package org.jclouds.cloudstack.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * Optional fields for account creation
 *
 * @author Adrian Cole
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api_2.2.12/global_admin/createAccount.html"
 *      />
 */
public class CreateAccountOptions extends BaseHttpRequestOptions {

   public static final CreateAccountOptions NONE = new CreateAccountOptions();

   /**
    * @param networkDomain network domain
    */
   public CreateAccountOptions networkDomain(String networkDomain) {
      this.queryParameters.replaceValues("networkdomain", ImmutableSet.of(networkDomain));
      return this;
   }

   /**
    * @param account an optional account for the resource
    */
   public CreateAccountOptions account(String account) {
      this.queryParameters.replaceValues("account", ImmutableSet.of(account));
      return this;
   }

   /**
    * @param domainId The domain for the resource
    */
   public CreateAccountOptions domainId(String domainId) {
      this.queryParameters.replaceValues("domainid", ImmutableSet.of(domainId + ""));
      return this;

   }

   public static class Builder {

      /**
       * @see CreateAccountOptions#networkDomain
       */
      public static CreateAccountOptions networkDomain(String networkDomain) {
         CreateAccountOptions options = new CreateAccountOptions();
         return options.networkDomain(networkDomain);
      }

      /**
       * @see CreateAccountOptions#account
       */
      public static CreateAccountOptions account(String account) {
         CreateAccountOptions options = new CreateAccountOptions();
         return options.account(account);
      }

      /**
       * @see CreateAccountOptions#domainId
       */
      public static CreateAccountOptions domainId(String domainId) {
         CreateAccountOptions options = new CreateAccountOptions();
         return options.domainId(domainId);
      }
   }
}
