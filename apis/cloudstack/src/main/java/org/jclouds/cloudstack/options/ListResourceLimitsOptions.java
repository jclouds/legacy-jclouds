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
 * @author Vijay Kiran
 */
public class ListResourceLimitsOptions extends BaseHttpRequestOptions {
   public static final ListResourceLimitsOptions NONE = new ListResourceLimitsOptions();

   /**
    * Lists resource limits by account. Must be used with the domainId parameter.
    *
    * @param account - the account for which the resource limits are retrieved for.
    * @return ListResourceLimitsOptions
    */
   public ListResourceLimitsOptions account(String account, String domainId) {
      this.queryParameters.replaceValues("account", ImmutableSet.of(account));
      this.queryParameters.replaceValues("domainid", ImmutableSet.of(String.valueOf(domainId)));
      return this;
   }

   /**
    * Lists resource limits by domain ID. If used with the account parameter,
    * lists resource limits for a specified account in a specified domain.
    *
    * @param domainId
    * @return ListResourceLimitsOptions
    */
   public ListResourceLimitsOptions domainId(String domainId) {
      this.queryParameters.replaceValues("domainid", ImmutableSet.of(String.valueOf(domainId)));
      return this;
   }

   /**
    * Lists resource limits by ID.
    *
    * @param id of the resource limit.
    * @return ListResourceLimitsOptions
    */
   public ListResourceLimitsOptions id(String id) {
      this.queryParameters.replaceValues("account", ImmutableSet.of(String.valueOf(id)));
      return this;
   }

   /**
    * Lists resource limits by project.
    *
    * @param projectId the project
    * @return ListResourceLimitsOptions
    */
   public ListResourceLimitsOptions projectId(String projectId) {
      this.queryParameters.replaceValues("projectid", ImmutableSet.of(String.valueOf(projectId + "")));
      return this;
   }

   /**
    * List by keyword
    *
    * @param keyword
    * @return ListResourceLimitsOptions
    */
   public ListResourceLimitsOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword));
      return this;
   }

   /**
    * Type of resource to update. Values are 0, 1, 2, 3, and 4.
    * <ul>
    * <li>0 - Instance. Number of instances a user can create.</li>
    * <li>1 - IP. Number of public IP addresses a user can own.</li>
    * <li>2 - Volume. Number of disk volumes a user can create.</li>
    * <li>3 - Snapshot. Number of snapshots a user can create.</li>
    * <li>4 - Template. Number of templates that a user can register/create.</li>
    * </ul>
    *
    * @param resourceType type of the resource to query for
    * @return ListResourceLimitsOptions
    */
   public ListResourceLimitsOptions resourceType(int resourceType) {
      this.queryParameters.replaceValues("resourcetype", ImmutableSet.of(String.valueOf(resourceType)));
      return this;
   }


   public static class Builder {
      /**
       * @see ListResourceLimitsOptions#account(String, String)
       */
      public static ListResourceLimitsOptions account(String account, String domainId) {
         ListResourceLimitsOptions options = new ListResourceLimitsOptions();
         return options.account(account, domainId);
      }

      /**
       * @see ListResourceLimitsOptions#domainId(String)
       */
      public static ListResourceLimitsOptions domainId(String domainId) {
         ListResourceLimitsOptions options = new ListResourceLimitsOptions();
         return options.domainId(domainId);
      }

      /**
       * @see ListResourceLimitsOptions#id(String)
       */
      public static ListResourceLimitsOptions id(String id) {
         ListResourceLimitsOptions options = new ListResourceLimitsOptions();
         return options.id(id);
      }

      /**
       * @see ListResourceLimitsOptions#projectId(String)
       */
      public static ListResourceLimitsOptions projectId(String projectId) {
         ListResourceLimitsOptions options = new ListResourceLimitsOptions();
         return options.projectId(projectId);
      }

      /**
       * @see ListResourceLimitsOptions#keyword(String)
       */
      public static ListResourceLimitsOptions keyword(String keyword) {
         ListResourceLimitsOptions options = new ListResourceLimitsOptions();
         return options.keyword(keyword);
      }

      /**
       * @see ListResourceLimitsOptions#resourceType(int)
       */
      public static ListResourceLimitsOptions resourceType(int resourceType) {
         ListResourceLimitsOptions options = new ListResourceLimitsOptions();
         return options.resourceType(resourceType);
      }
   }
}
