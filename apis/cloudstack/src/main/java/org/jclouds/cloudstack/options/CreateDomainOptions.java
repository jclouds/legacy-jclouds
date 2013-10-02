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
 * Options used to control how a domain is created
 * 
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/global_admin/createDomain.html"
 *      />
 * @author Andrei Savu
 */
public class CreateDomainOptions extends BaseHttpRequestOptions {

   public static final CreateDomainOptions NONE = new CreateDomainOptions();

   /**
    * @param networkDomain
    *       network domain for networks in the domain
    */
   public CreateDomainOptions networkDomain(String networkDomain) {
      this.queryParameters.replaceValues("networkdomain", ImmutableSet.of(networkDomain));
      return this;
   }

   /**
    * @param parentDomainId
    *       the ID of the parent domain
    */
   public CreateDomainOptions parentDomainId(String parentDomainId) {
      this.queryParameters.replaceValues("parentdomainid", ImmutableSet.of(parentDomainId + ""));
      return this;
   }

   public static class Builder {

      /**
       * @see CreateDomainOptions#networkDomain
       */
      public static CreateDomainOptions networkDomain(String networkDomain) {
         CreateDomainOptions options = new CreateDomainOptions();
         return options.networkDomain(networkDomain);
      }

      /**
       * @see CreateDomainOptions#parentDomainId
       */
      public static CreateDomainOptions parentDomainId(String parentDomainId) {
         CreateDomainOptions options = new CreateDomainOptions();
         return options.parentDomainId(parentDomainId);
      }
   }
}
