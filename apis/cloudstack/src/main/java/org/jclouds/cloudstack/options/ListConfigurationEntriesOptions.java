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
 * Options used to control what configuration entries are returned
 * 
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/global_admin/listConfigurations.html"
 *      />
 * @author Andrei Savu
 */
public class ListConfigurationEntriesOptions extends BaseHttpRequestOptions {

   public static final ListConfigurationEntriesOptions NONE = new ListConfigurationEntriesOptions();

   /**
    * @param category
    *    list by category name
    */
   public ListConfigurationEntriesOptions category(String category) {
      this.queryParameters.replaceValues("category", ImmutableSet.of(category));
      return this;
   }

   /**
    * @param keyword
    *    list by keyword
    */
   public ListConfigurationEntriesOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword));
      return this;
   }

   /**
    * @param name
    *    list by entry name
    */
   public ListConfigurationEntriesOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }

   public ListConfigurationEntriesOptions page(long page) {
      this.queryParameters.replaceValues("page", ImmutableSet.of(page + ""));
      return this;
   }

   public ListConfigurationEntriesOptions pageSize(long pageSize) {
      this.queryParameters.replaceValues("pagesize", ImmutableSet.of(pageSize + ""));
      return this;
   }

   public static class Builder {

      /**
       * @see ListConfigurationEntriesOptions#category
       */
      public static ListConfigurationEntriesOptions category(String category) {
         ListConfigurationEntriesOptions options = new ListConfigurationEntriesOptions();
         return options.category(category);
      }

      /**
       * @see ListConfigurationEntriesOptions#keyword
       */
      public static ListConfigurationEntriesOptions keyword(String keyword) {
         ListConfigurationEntriesOptions options = new ListConfigurationEntriesOptions();
         return options.keyword(keyword);
      }

      /**
       * @see ListConfigurationEntriesOptions#name
       */
      public static ListConfigurationEntriesOptions name(String name) {
         ListConfigurationEntriesOptions options = new ListConfigurationEntriesOptions();
         return options.name(name);
      }

      /**
       * @see ListConfigurationEntriesOptions#page
       */
      public static ListConfigurationEntriesOptions page(long page) {
         ListConfigurationEntriesOptions options = new ListConfigurationEntriesOptions();
         return options.page(page);
      }

      /**
       * @see ListConfigurationEntriesOptions#pageSize
       */
      public static ListConfigurationEntriesOptions pageSize(long pageSize) {
         ListConfigurationEntriesOptions options = new ListConfigurationEntriesOptions();
         return options.pageSize(pageSize);
      }
   }
}
