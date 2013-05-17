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

import org.jclouds.cloudstack.domain.AllocationState;
import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * Options to the GlobalHostClient.addHost() API call
 *
 * @author Richard Downer
 */
public class AddClusterOptions extends BaseHttpRequestOptions {

   public static final AddClusterOptions NONE = new AddClusterOptions();

   /**
    * @param allocationState Allocation state of this Host for allocation of new resources
    */
   public AddClusterOptions allocationState(AllocationState allocationState) {
      this.queryParameters.replaceValues("allocationstate", ImmutableSet.of(allocationState.toString()));
      return this;
   }

   /**
    * @param password the password for the host
    */
   public AddClusterOptions password(String password) {
      this.queryParameters.replaceValues("password", ImmutableSet.of(password));
      return this;
   }

   /**
    * @param podId the Pod ID for the host
    */
   public AddClusterOptions podId(String podId) {
      this.queryParameters.replaceValues("podid", ImmutableSet.of(podId + ""));
      return this;
   }

   /**
    * @param url the URL
    */
   public AddClusterOptions url(String url) {
      this.queryParameters.replaceValues("url", ImmutableSet.of(url));
      return this;
   }

   /**
    * @param username the username for the cluster
    */
   public AddClusterOptions username(String username) {
      this.queryParameters.replaceValues("username", ImmutableSet.of(username));
      return this;
   }

   public static class Builder {

      /**
       * @param allocationState Allocation state of this Host for allocation of new resources
       */
      public static AddClusterOptions allocationState(AllocationState allocationState) {
         return new AddClusterOptions().allocationState(allocationState);
      }

      /**
       * @param password the password for the host
       */
      public static AddClusterOptions password(String password) {
         return new AddClusterOptions().password(password);
      }

      /**
       * @param podId the Pod ID for the host
       */
      public static AddClusterOptions podId(String podId) {
         return new AddClusterOptions().podId(podId);
      }

      /**
       * @param url the URL
       */
      public static AddClusterOptions url(String url) {
         return new AddClusterOptions().url(url);
      }

      /**
       * @param username the username for the cluster
       */
      public static AddClusterOptions username(String username) {
         return new AddClusterOptions().username(username);
      }

   }
}
