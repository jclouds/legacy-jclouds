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
 * Optional arguments for updating an Account
 * 
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api_2.2.12/global_admin/updateAccount.html"
 *      />
 * @author Andrei Savu
 */
public class UpdateAccountOptions extends BaseHttpRequestOptions {

   public static final UpdateAccountOptions NONE = new UpdateAccountOptions();

   /**
    * @param networkDomain
    *       network domain for the account's networks
    */
   public UpdateAccountOptions networkDomain(String networkDomain) {
      this.queryParameters.replaceValues("networkdomain", ImmutableSet.<String>of(networkDomain));
      return this;
   }

   public static class Builder {

      /**
       * @see UpdateAccountOptions#networkDomain
       */
      public static UpdateAccountOptions networkDomain(String networkDomain) {
         UpdateAccountOptions options = new UpdateAccountOptions();
         return options.networkDomain(networkDomain);
      }
   }

}
