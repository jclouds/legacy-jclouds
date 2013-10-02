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
 * Options to the listAlerts command.
 *
 * @author Richard Downer
 */
public class ListAlertsOptions extends BaseHttpRequestOptions {

   public static final ListAlertsOptions NONE = new ListAlertsOptions();

   public ListAlertsOptions id(String id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id + ""));
      return this;
   }

   public ListAlertsOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword));
      return this;
   }

   public ListAlertsOptions type(String type) {
      this.queryParameters.replaceValues("type", ImmutableSet.of(type));
      return this;
   }

   public static class Builder {

      public static ListAlertsOptions id(String id) {
         final ListAlertsOptions options = new ListAlertsOptions();
         return options.id(id);
      }

      public static ListAlertsOptions keyword(String keyword) {
         final ListAlertsOptions options = new ListAlertsOptions();
         return options.keyword(keyword);
      }

      public static ListAlertsOptions type(String type) {
         final ListAlertsOptions options = new ListAlertsOptions();
         return options.type(type);
      }

   }

}

