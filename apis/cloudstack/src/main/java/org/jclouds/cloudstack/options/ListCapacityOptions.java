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

import org.jclouds.cloudstack.domain.Capacity;
import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * Options to the listCapacity command.
 *
 * @author Richard Downer
 */
public class ListCapacityOptions extends BaseHttpRequestOptions {

   public static final ListCapacityOptions NONE = new ListCapacityOptions();

   public ListCapacityOptions hostId(String hostId) {
      this.queryParameters.replaceValues("hostid", ImmutableSet.of(hostId + ""));
      return this;
   }

   public ListCapacityOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword));
      return this;
   }

   public ListCapacityOptions podId(String podId) {
      this.queryParameters.replaceValues("podid", ImmutableSet.of(podId + ""));
      return this;
   }

   public ListCapacityOptions type(Capacity.Type type) {
      this.queryParameters.replaceValues("type", ImmutableSet.of(type.ordinal() + ""));
      return this;
   }

   public ListCapacityOptions zoneId(String zoneId) {
      this.queryParameters.replaceValues("zoneid", ImmutableSet.of(zoneId + ""));
      return this;
   }

   public static class Builder {

      public static ListCapacityOptions hostId(String hostId) {
         final ListCapacityOptions options = new ListCapacityOptions();
         return options.hostId(hostId);
      }

      public static ListCapacityOptions keyword(String keyword) {
         final ListCapacityOptions options = new ListCapacityOptions();
         return options.keyword(keyword);
      }

      public static ListCapacityOptions podId(String podId) {
         final ListCapacityOptions options = new ListCapacityOptions();
         return options.podId(podId);
      }

      public static ListCapacityOptions type(Capacity.Type type) {
         final ListCapacityOptions options = new ListCapacityOptions();
         return options.type(type);
      }

      public static ListCapacityOptions zoneId(String zoneId) {
         final ListCapacityOptions options = new ListCapacityOptions();
         return options.zoneId(zoneId);
      }

   }

}

