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
package org.jclouds.openstack.nova.v2_0.options;

import java.util.Date;

import org.jclouds.openstack.v2_0.options.PaginationOptions;

/**
 * Options used to control the amount of detail in the request.
 * 
 * @see PaginationOptions
 * @see <a href="http://wiki.openstack.org/OpenStackAPI_1-1" />
 * @author Adrian Cole
 */
public class ListOptions extends PaginationOptions {

   public static final ListOptions NONE = new ListOptions();

   /**
    * unless used, only the name and id will be returned per row.
    * 
    * @return
    */
   public ListOptions withDetails() {
      this.pathSuffix = "/detail";
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListOptions changesSince(Date ifModifiedSince) {
      super.changesSince(ifModifiedSince);
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListOptions limit(int limit) {
      super.limit(limit);
      return this;

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListOptions marker(String marker) {
      super.marker(marker);
      return this;
   }

   public static class Builder {

      /**
       * @see ListOptions#withDetails()
       */
      public static ListOptions withDetails() {
         ListOptions options = new ListOptions();
         return options.withDetails();
      }

      /**
       * @see PaginationOptions#marker(String)
       */
      public static ListOptions marker(String marker) {
         ListOptions options = new ListOptions();
         return options.marker(marker);
      }

      /**
       * @see PaginationOptions#limit(long)
       */
      public static ListOptions maxResults(int maxKeys) {
         ListOptions options = new ListOptions();
         return options.limit(maxKeys);
      }

      /**
       * @see PaginationOptions#changesSince(Date)
       */
      public static ListOptions changesSince(Date since) {
         ListOptions options = new ListOptions();
         return options.changesSince(since);
      }

   }
}
