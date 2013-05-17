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
 * Options used to control what domains are returned
 * 
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/domain_admin/listDomains.html"
 *      />
 * @author Andrei Savu
 */
public class ListDomainsOptions extends BaseHttpRequestOptions {

   public static final ListDomainsOptions NONE = new ListDomainsOptions();

   /**
    * @param id
    *    firewall rule ID
    */
   public ListDomainsOptions id(String id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id + ""));
      return this;
   }

   /**
    * @param keyword
    *    list by keyword
    */
   public ListDomainsOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword));
      return this;
   }

   /**
    * @param level
    *    list by domain level
    */
   public ListDomainsOptions level(long level) {
      this.queryParameters.replaceValues("level", ImmutableSet.of(level + ""));
      return this;
   }

   /**
    * @param name
    *    list by domain name
    */
   public ListDomainsOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }

   public ListDomainsOptions page(long page) {
      this.queryParameters.replaceValues("page", ImmutableSet.of(page + ""));
      return this;
   }

   public ListDomainsOptions pageSize(long pageSize) {
      this.queryParameters.replaceValues("pagesize", ImmutableSet.of(pageSize + ""));
      return this;
   }

   public static class Builder {

      /**
       * @see ListDomainsOptions#id
       */
      public static ListDomainsOptions id(String id) {
         ListDomainsOptions options = new ListDomainsOptions();
         return options.id(id);
      }

      /**
       * @see ListDomainsOptions#keyword
       */
      public static ListDomainsOptions keyword(String keyword) {
         ListDomainsOptions options = new ListDomainsOptions();
         return options.keyword(keyword);
      }

      /**
       * @see ListDomainsOptions#level
       */
      public static ListDomainsOptions level(long level) {
         ListDomainsOptions options = new ListDomainsOptions();
         return options.level(level);
      }

      /**
       * @see ListDomainsOptions#name
       */
      public static ListDomainsOptions name(String name) {
         ListDomainsOptions options = new ListDomainsOptions();
         return options.name(name);
      }

      /**
       * @see ListDomainsOptions#page
       */
      public static ListDomainsOptions page(long page) {
         ListDomainsOptions options = new ListDomainsOptions();
         return options.page(page);
      }

      /**
       * @see ListDomainsOptions#pageSize
       */
      public static ListDomainsOptions pageSize(long pageSize) {
         ListDomainsOptions options = new ListDomainsOptions();
         return options.pageSize(pageSize);
      }
   }
}
