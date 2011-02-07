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
 * Options used to control what disk offerings are returned
 * 
 * @see <a href="http://download.cloud.com/releases/2.2/api/user/listDiskOfferings.html" />
 * @author Adrian Cole
 */
public class ListDiskOfferingsOptions extends BaseHttpRequestOptions {

   public static final ListDiskOfferingsOptions NONE = new ListDiskOfferingsOptions();

   /**
    * @param id
    *           the ID of the disk offering
    */
   public ListDiskOfferingsOptions id(String id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id));
      return this;
   }

   /**
    * @param domainId
    *           the ID of the domain associated with the disk offering
    */
   public ListDiskOfferingsOptions domainId(String domainId) {
      this.queryParameters.replaceValues("domainid", ImmutableSet.of(domainId));
      return this;

   }

   /**
    * @param name
    *           the disk offering name
    */
   public ListDiskOfferingsOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }

   public static class Builder {

      /**
       * @see ListDiskOfferingsOptions#name
       */
      public static ListDiskOfferingsOptions name(String name) {
         ListDiskOfferingsOptions options = new ListDiskOfferingsOptions();
         return options.name(name);
      }

      /**
       * @see ListDiskOfferingsOptions#domainId
       */
      public static ListDiskOfferingsOptions domainId(String id) {
         ListDiskOfferingsOptions options = new ListDiskOfferingsOptions();
         return options.domainId(id);
      }

      /**
       * @see ListDiskOfferingsOptions#id
       */
      public static ListDiskOfferingsOptions id(String id) {
         ListDiskOfferingsOptions options = new ListDiskOfferingsOptions();
         return options.id(id);
      }
   }
}
