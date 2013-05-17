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
public class UpdateDomainOptions extends BaseHttpRequestOptions {

   public static final UpdateDomainOptions NONE = new UpdateDomainOptions();

   /**
    * @param name
    *       the new name for this domain
    */
   public UpdateDomainOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }

   /**
    * @param networkDomain
    *       network domain for networks in the domain
    */
   public UpdateDomainOptions networkDomain(String networkDomain) {
      this.queryParameters.replaceValues("networkdomain", ImmutableSet.of(networkDomain));
      return this;
   }

   public static class Builder {

      /**
       * @see UpdateDomainOptions#name
       */
      public static UpdateDomainOptions name(String name) {
         UpdateDomainOptions options = new UpdateDomainOptions();
         return options.name(name);
      }

      /**
       * @see UpdateDomainOptions#networkDomain
       */
      public static UpdateDomainOptions networkDomain(String networkDomain) {
         UpdateDomainOptions options = new UpdateDomainOptions();
         return options.networkDomain(networkDomain);
      }
   }
}
