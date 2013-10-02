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
 * Options used to control what domain children are returned
 * 
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/domain_admin/listDomains.html"
 *      />
 * @author Andrei Savu
 */
public class ListDomainChildrenOptions extends BaseHttpRequestOptions {

   public static final ListDomainChildrenOptions NONE = new ListDomainChildrenOptions();

   /**
    * @param parentDomainId
    *    firewall rule ID
    */
   public ListDomainChildrenOptions parentDomainId(String parentDomainId) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(parentDomainId + ""));
      return this;
   }

   /**
    * @param isRecursive
    *    to return the entire tree, use the value "true". To return
    *    the first level children, use the value "false".
    */
   public ListDomainChildrenOptions isRecursive(boolean isRecursive) {
      this.queryParameters.replaceValues("isrecursive", ImmutableSet.of(isRecursive + ""));
      return this;
   }

   /**
    * @param keyword
    *    list by keyword
    */
   public ListDomainChildrenOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword));
      return this;
   }

   /**
    * @param name
    *    list by domain name
    */
   public ListDomainChildrenOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }

   public ListDomainChildrenOptions page(long page) {
      this.queryParameters.replaceValues("page", ImmutableSet.of(page + ""));
      return this;
   }

   public ListDomainChildrenOptions pageSize(long pageSize) {
      this.queryParameters.replaceValues("pagesize", ImmutableSet.of(pageSize + ""));
      return this;
   }

   public static class Builder {

      /**
       * @see ListDomainChildrenOptions#parentDomainId
       */
      public static ListDomainChildrenOptions parentDomainId(String parentDomainId) {
         ListDomainChildrenOptions options = new ListDomainChildrenOptions();
         return options.parentDomainId(parentDomainId);
      }

      /**
       * @see ListDomainChildrenOptions#isRecursive
       */
      public static ListDomainChildrenOptions isRecursive(boolean isRecursive) {
         ListDomainChildrenOptions options = new ListDomainChildrenOptions();
         return options.isRecursive(isRecursive);
      }

      /**
       * @see ListDomainChildrenOptions#keyword
       */
      public static ListDomainChildrenOptions keyword(String keyword) {
         ListDomainChildrenOptions options = new ListDomainChildrenOptions();
         return options.keyword(keyword);
      }

      /**
       * @see ListDomainChildrenOptions#name
       */
      public static ListDomainChildrenOptions name(String name) {
         ListDomainChildrenOptions options = new ListDomainChildrenOptions();
         return options.name(name);
      }

      /**
       * @see ListDomainChildrenOptions#page
       */
      public static ListDomainChildrenOptions page(long page) {
         ListDomainChildrenOptions options = new ListDomainChildrenOptions();
         return options.page(page);
      }

      /**
       * @see ListDomainChildrenOptions#pageSize
       */
      public static ListDomainChildrenOptions pageSize(long pageSize) {
         ListDomainChildrenOptions options = new ListDomainChildrenOptions();
         return options.pageSize(pageSize);
      }
   }
}
