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

import com.google.common.collect.ImmutableSet;
import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Options to the GlobalStoragePools[Async]Client.listStoragePools API call
 *
 * @author Richard Downer
 */
public class ListStoragePoolsOptions extends BaseHttpRequestOptions {

   public static ListStoragePoolsOptions NONE = new ListStoragePoolsOptions();
   
   public static class Builder {

      private Builder() {}
      
      public static ListStoragePoolsOptions clusterId(long clusterId) {
         ListStoragePoolsOptions options = new ListStoragePoolsOptions();
         return options.clusterId(clusterId);
      }

      public static ListStoragePoolsOptions id(long id) {
         ListStoragePoolsOptions options = new ListStoragePoolsOptions();
         return options.id(id);
      }

      public static ListStoragePoolsOptions ipAddress(String ipAddress) {
         ListStoragePoolsOptions options = new ListStoragePoolsOptions();
         return options.ipAddress(ipAddress);
      }

      public static ListStoragePoolsOptions keyword(String keyword) {
         ListStoragePoolsOptions options = new ListStoragePoolsOptions();
         return options.keyword(keyword);
      }

      public static ListStoragePoolsOptions name(String name) {
         ListStoragePoolsOptions options = new ListStoragePoolsOptions();
         return options.name(name);
      }

      public static ListStoragePoolsOptions path(String path) {
         ListStoragePoolsOptions options = new ListStoragePoolsOptions();
         return options.path(path);
      }

      public static ListStoragePoolsOptions podId(long podId) {
         ListStoragePoolsOptions options = new ListStoragePoolsOptions();
         return options.podId(podId);
      }

      public static ListStoragePoolsOptions zoneId(long zoneId) {
         ListStoragePoolsOptions options = new ListStoragePoolsOptions();
         return options.zoneId(zoneId);
      }
   }

   ListStoragePoolsOptions() {}
   
   public ListStoragePoolsOptions clusterId(long clusterId) {
      this.queryParameters.replaceValues("clusterid", ImmutableSet.of(clusterId + ""));
      return this;
   }

   public ListStoragePoolsOptions id(long id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id + ""));
      return this;
   }

   public ListStoragePoolsOptions ipAddress(String ipAddress) {
      this.queryParameters.replaceValues("ipaddress", ImmutableSet.of(ipAddress));
      return this;
   }

   public ListStoragePoolsOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword));
      return this;
   }

   public ListStoragePoolsOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }
   public ListStoragePoolsOptions path(String path) {
      this.queryParameters.replaceValues("path", ImmutableSet.of(path));
      return this;
   }

   public ListStoragePoolsOptions podId(long podId) {
      this.queryParameters.replaceValues("podid", ImmutableSet.of(podId + ""));
      return this;
   }

   public ListStoragePoolsOptions zoneId(long zoneId) {
      this.queryParameters.replaceValues("zoneid", ImmutableSet.of(zoneId + ""));
      return this;
   }

}
