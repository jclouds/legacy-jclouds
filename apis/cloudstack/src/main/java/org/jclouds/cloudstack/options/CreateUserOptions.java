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
 * Optional fields for user creation
 *
 * @author Andrei Savu
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api_2.2.12/global_admin/createUser.html"
 *      />
 */
public class CreateUserOptions extends BaseHttpRequestOptions {

   public static final CreateUserOptions NONE = new CreateUserOptions();

   /**
    * @param domainId The domain for the resource
    */
   public CreateUserOptions domainId(String domainId) {
      this.queryParameters.replaceValues("domainid", ImmutableSet.of(domainId + ""));
      return this;

   }

   /**
    * @param timezone Specifies a timezone for this command. For more information
    *    on the timezone parameter, see Time Zone Format.
    */
   public CreateUserOptions timezone(String timezone) {
      this.queryParameters.replaceValues("timezone", ImmutableSet.of(timezone));
      return this;
   }

   public static class Builder {

      /**
       * @see CreateUserOptions#domainId
       */
      public static CreateUserOptions domainId(String domainId) {
         CreateUserOptions options = new CreateUserOptions();
         return options.domainId(domainId);
      }

      /**
       * @see CreateUserOptions#timezone
       */
      public static CreateUserOptions timezone(String timezone) {
         CreateUserOptions options = new CreateUserOptions();
         return options.timezone(timezone);
      }
   }
}
