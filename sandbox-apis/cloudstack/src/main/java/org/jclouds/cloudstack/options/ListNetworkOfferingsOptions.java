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

import org.jclouds.cloudstack.domain.TrafficType;
import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * Options used to control what network offerings are returned
 * 
 * @see <a href="http://download.cloud.com/releases/2.2/api/user/listNetworkOfferings.html" />
 * @author Adrian Cole
 */
public class ListNetworkOfferingsOptions extends BaseHttpRequestOptions {

   public static final ListNetworkOfferingsOptions NONE = new ListNetworkOfferingsOptions();

   /**
    * @param id
    *           the ID of the network offering
    */
   public ListNetworkOfferingsOptions id(String id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id));
      return this;
   }

   /**
    * @param name
    *           the network offering name
    */
   public ListNetworkOfferingsOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }

   /**
    * @param displayText
    *           network offerings by display text
    */
   public ListNetworkOfferingsOptions displayText(String displayText) {
      this.queryParameters.replaceValues("displaytext", ImmutableSet.of(displayText));
      return this;
   }

   /**
    * @param availability
    *           the availability of network offering. Default value is Required
    */
   public ListNetworkOfferingsOptions availability(String availability) {
      this.queryParameters.replaceValues("availability", ImmutableSet.of(availability));
      return this;
   }

   /**
    * @param isDefault
    *           true if network offering is default, false otherwise
    */
   public ListNetworkOfferingsOptions isDefault(boolean isDefault) {
      this.queryParameters.replaceValues("isdefault", ImmutableSet.of(isDefault + ""));
      return this;
   }

   /**
    * @param isShared
    *           true if network offering is shared, false otherwise
    */
   public ListNetworkOfferingsOptions isShared(boolean isShared) {
      this.queryParameters.replaceValues("isshared", ImmutableSet.of(isShared + ""));
      return this;
   }

   /**
    * @param specifyVLAN
    *           True if we allow the network supports vlan, false otherwise. If you create network
    *           using this offering, you must specify vlan.
    */
   public ListNetworkOfferingsOptions specifyVLAN(boolean specifyVLAN) {
      this.queryParameters.replaceValues("specifyvlan", ImmutableSet.of(specifyVLAN + ""));
      return this;
   }

   /**
    * @param trafficType
    *           type of the traffic
    */
   public ListNetworkOfferingsOptions trafficType(TrafficType trafficType) {
      this.queryParameters.replaceValues("traffictype", ImmutableSet.of(trafficType.toString()));
      return this;
   }

   public static class Builder {
      /**
       * @see ListNetworkOfferingsOptions#specifyVLAN
       */
      public static ListNetworkOfferingsOptions specifyVLAN(boolean specifyVLAN) {
         ListNetworkOfferingsOptions options = new ListNetworkOfferingsOptions();
         return options.specifyVLAN(specifyVLAN);
      }

      /**
       * @see ListNetworkOfferingsOptions#isDefault
       */
      public static ListNetworkOfferingsOptions isDefault(boolean isDefault) {
         ListNetworkOfferingsOptions options = new ListNetworkOfferingsOptions();
         return options.isDefault(isDefault);
      }

      /**
       * @see ListNetworkOfferingsOptions#isShared
       */
      public static ListNetworkOfferingsOptions isShared(boolean isShared) {
         ListNetworkOfferingsOptions options = new ListNetworkOfferingsOptions();
         return options.isShared(isShared);
      }

      /**
       * @see ListNetworkOfferingsOptions#displayText
       */
      public static ListNetworkOfferingsOptions displayText(String displayText) {
         ListNetworkOfferingsOptions options = new ListNetworkOfferingsOptions();
         return options.displayText(displayText);
      }

      /**
       * @see ListNetworkOfferingsOptions#name
       */
      public static ListNetworkOfferingsOptions name(String name) {
         ListNetworkOfferingsOptions options = new ListNetworkOfferingsOptions();
         return options.name(name);
      }

      /**
       * @see ListNetworkOfferingsOptions#availability
       */
      public static ListNetworkOfferingsOptions availability(String availability) {
         ListNetworkOfferingsOptions options = new ListNetworkOfferingsOptions();
         return options.availability(availability);
      }

      /**
       * @see ListNetworkOfferingsOptions#id
       */
      public static ListNetworkOfferingsOptions id(String id) {
         ListNetworkOfferingsOptions options = new ListNetworkOfferingsOptions();
         return options.id(id);
      }

      /**
       * @see ListNetworkOfferingsOptions#trafficType
       */
      public static ListNetworkOfferingsOptions trafficType(TrafficType trafficType) {
         ListNetworkOfferingsOptions options = new ListNetworkOfferingsOptions();
         return options.trafficType(trafficType);
      }
   }
}
