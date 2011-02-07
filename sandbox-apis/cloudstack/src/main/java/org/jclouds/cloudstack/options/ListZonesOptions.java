/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudstack.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * Options used to control what zones information is returned
 * 
 * @see <a href="http://download.cloud.com/releases/2.2/api/user/listZones.html" />
 * @author Adrian Cole
 */
public class ListZonesOptions extends BaseHttpRequestOptions {

   public static final ListZonesOptions NONE = new ListZonesOptions();

   /**
    * @param id
    *           the ID of the zone
    */
   public ListZonesOptions id(String id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id));
      return this;
   }

   /**
    * @param domainId
    *           the ID of the domain associated with the zone
    */
   public ListZonesOptions domainId(String domainId) {
      this.queryParameters.replaceValues("domainid", ImmutableSet.of(domainId));
      return this;

   }

   /**
    * @param available
    *           true if you want to retrieve all available Zones. False if you only want to return
    *           the Zones from which you have at least one VM. Default is false.
    */
   public ListZonesOptions available(boolean available) {
      this.queryParameters.replaceValues("available", ImmutableSet.of(available + ""));
      return this;
   }

   public static class Builder {

      /**
       * @see ListZonesOptions#available
       */
      public static ListZonesOptions available(boolean available) {
         ListZonesOptions options = new ListZonesOptions();
         return options.available(available);
      }

      /**
       * @see ListZonesOptions#domainId
       */
      public static ListZonesOptions domainId(String id) {
         ListZonesOptions options = new ListZonesOptions();
         return options.domainId(id);
      }

      /**
       * @see ListZonesOptions#id
       */
      public static ListZonesOptions id(String id) {
         ListZonesOptions options = new ListZonesOptions();
         return options.id(id);
      }

   }
}
