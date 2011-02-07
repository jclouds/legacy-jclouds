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
 * Options used to control what service offerings are returned
 * 
 * @see <a href="http://download.cloud.com/releases/2.2/api/user/listServiceOfferings.html" />
 * @author Adrian Cole
 */
public class ListServiceOfferingsOptions extends BaseHttpRequestOptions {

   public static final ListServiceOfferingsOptions NONE = new ListServiceOfferingsOptions();

   /**
    * @param id
    *           the ID of the service offering
    */
   public ListServiceOfferingsOptions id(String id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id));
      return this;
   }

   /**
    * @param domainId
    *           the ID of the domain associated with the service offering
    */
   public ListServiceOfferingsOptions domainId(String domainId) {
      this.queryParameters.replaceValues("domainid", ImmutableSet.of(domainId));
      return this;

   }

   /**
    * @param name
    *           the service offering name
    */
   public ListServiceOfferingsOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }

   /**
    * @param virtualMachineId
    *           the ID of the virtual machine. Pass this in if you want to see the available service
    *           offering that a virtual machine can be changed to.
    */
   public ListServiceOfferingsOptions virtualMachineId(String virtualMachineId) {
      this.queryParameters.replaceValues("virtualmachineid", ImmutableSet.of(virtualMachineId));
      return this;

   }

   public static class Builder {

      /**
       * @see ListServiceOfferingsOptions#name
       */
      public static ListServiceOfferingsOptions name(String name) {
         ListServiceOfferingsOptions options = new ListServiceOfferingsOptions();
         return options.name(name);
      }

      /**
       * @see ListServiceOfferingsOptions#domainId
       */
      public static ListServiceOfferingsOptions domainId(String id) {
         ListServiceOfferingsOptions options = new ListServiceOfferingsOptions();
         return options.domainId(id);
      }

      /**
       * @see ListServiceOfferingsOptions#id
       */
      public static ListServiceOfferingsOptions id(String id) {
         ListServiceOfferingsOptions options = new ListServiceOfferingsOptions();
         return options.id(id);
      }

      /**
       * @see ListServiceOfferingsOptions#virtualMachineId
       */
      public static ListServiceOfferingsOptions virtualMachineId(String virtualMachineId) {
         ListServiceOfferingsOptions options = new ListServiceOfferingsOptions();
         return options.virtualMachineId(virtualMachineId);
      }
   }
}
