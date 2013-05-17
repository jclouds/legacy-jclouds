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
 * Options for services that apply to accounts in domains
 * 
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @author Adrian Cole
 */
public class AccountInDomainOptions extends BaseHttpRequestOptions {

   public static final AccountInDomainOptions NONE = new AccountInDomainOptions();

   /**
    * 
    * @param account
    *           an optional account for the resource
    * @param domain
    *           domain id
    */
   public AccountInDomainOptions accountInDomain(String account, String domain) {
      this.queryParameters.replaceValues("account", ImmutableSet.of(account));
      this.queryParameters.replaceValues("domainid", ImmutableSet.of(domain + ""));
      return this;
   }

   /**
    * @param domainId
    *           The domain for the resource
    */
   public AccountInDomainOptions domainId(String domainId) {
      this.queryParameters.replaceValues("domainid", ImmutableSet.of(domainId + ""));
      return this;

   }

   public static class Builder {
      /**
       * @see AccountInDomainOptions#accountInDomain
       */
      public static AccountInDomainOptions accountInDomain(String account, String domain) {
         AccountInDomainOptions options = new AccountInDomainOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see AccountInDomainOptions#domainId
       */
      public static AccountInDomainOptions domainId(String domainId) {
         AccountInDomainOptions options = new AccountInDomainOptions();
         return options.domainId(domainId);
      }
   }
}
