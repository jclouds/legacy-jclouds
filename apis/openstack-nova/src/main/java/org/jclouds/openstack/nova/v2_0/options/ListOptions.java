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
package org.jclouds.openstack.nova.v2_0.options;

import java.util.Date;

import org.jclouds.openstack.v2_0.options.BaseListOptions;

/**
 * Options used to control the amount of detail in the request.
 * 
 * @see BaseListOptions
 * @see <a href="http://wiki.openstack.org/OpenStackAPI_1-1" />
 * @author Adrian Cole
 */
public class ListOptions extends BaseListOptions {

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
   public ListOptions maxResults(int limit) {
      super.maxResults(limit);
      return this;

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListOptions startAt(long offset) {
      super.startAt(offset);
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
       * @see BaseListOptions#startAt(long)
       */
      public static ListOptions startAt(long prefix) {
         ListOptions options = new ListOptions();
         return options.startAt(prefix);
      }

      /**
       * @see BaseListOptions#maxResults(long)
       */
      public static ListOptions maxResults(int maxKeys) {
         ListOptions options = new ListOptions();
         return options.maxResults(maxKeys);
      }

      /**
       * @see BaseListOptions#changesSince(Date)
       */
      public static ListOptions changesSince(Date since) {
         ListOptions options = new ListOptions();
         return options.changesSince(since);
      }

   }
}
