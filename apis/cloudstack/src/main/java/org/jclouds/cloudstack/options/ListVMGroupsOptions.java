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
package org.jclouds.cloudstack.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * Options used to control what VMGroups information is returned
 *
 * @author Richard Downer
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.8/api/user/listInstanceGroups.html"
 *      />
 */
public class ListVMGroupsOptions extends BaseHttpRequestOptions {

   public static final ListVMGroupsOptions NONE = new ListVMGroupsOptions();

   /**
    * @param id list VMGroups by id
    */
   public ListVMGroupsOptions id(long id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id + ""));
      return this;
   }

   /**
    * @param account account who owns the VMGroups
    */
   public ListVMGroupsOptions account(String account) {
      this.queryParameters.replaceValues("account", ImmutableSet.of(account));
      return this;
   }

   /**
    * @param domainId domain ID of the account owning the VMGroups
    */
   public ListVMGroupsOptions domainId(long domainId) {
      this.queryParameters.replaceValues("domainid", ImmutableSet.of(domainId + ""));
      return this;
   }

   /**
    * @param keyword keyword to search on
    */
   public ListVMGroupsOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword));
      return this;
   }

   /**
    * @param name find a VMGroup by its name
    */
   public ListVMGroupsOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }

   public static class Builder {
      /**
       * @see org.jclouds.cloudstack.options.ListVMGroupsOptions#id
       */
      public static ListVMGroupsOptions id(long id) {
         ListVMGroupsOptions options = new ListVMGroupsOptions();
         return options.id(id);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListVMGroupsOptions#account
       */
      public static ListVMGroupsOptions account(String account) {
         ListVMGroupsOptions options = new ListVMGroupsOptions();
         return options.account(account);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListVMGroupsOptions#domainId
       */
      public static ListVMGroupsOptions domainId(long id) {
         ListVMGroupsOptions options = new ListVMGroupsOptions();
         return options.domainId(id);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListVMGroupsOptions#keyword
       */
      public static ListVMGroupsOptions keyword(String keyword) {
         ListVMGroupsOptions options = new ListVMGroupsOptions();
         return options.keyword(keyword);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListVMGroupsOptions#account
       */
      public static ListVMGroupsOptions name(String name) {
         ListVMGroupsOptions options = new ListVMGroupsOptions();
         return options.name(name);
      }
   }

}
