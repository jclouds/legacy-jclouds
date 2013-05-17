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

import org.jclouds.cloudstack.domain.NetworkOfferingAvailabilityType;

import com.google.common.collect.ImmutableSet;

/**
 * Options to control how network offerings are created
 * 
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api_2.2.12/global_admin/updateNetworkOffering.html"
 *      />
 * @author Andrei Savu
 */
public class UpdateNetworkOfferingOptions extends AccountInDomainOptions {

   public static final UpdateNetworkOfferingOptions NONE = new UpdateNetworkOfferingOptions();

   /**
    * @param name
    *       service offering name
    */
   public UpdateNetworkOfferingOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.<String>of(name));
      return this;
   }

   /**
    * @param displayText
    *       service offering display text
    */
   public UpdateNetworkOfferingOptions displayText(String displayText) {
      this.queryParameters.replaceValues("displaytext", ImmutableSet.<String>of(displayText));
      return this;
   }

   /**
    * @param availability
    *       the availability of network offering. Default value is Required for Guest
    *       Virtual network offering; Optional for Guest Direct network offering
    */
   public UpdateNetworkOfferingOptions availability(NetworkOfferingAvailabilityType availability) {
      this.queryParameters.replaceValues("availability", ImmutableSet.<String>of(availability.toString()));
      return this;
   }

   public static class Builder {

      /**
       * @see UpdateNetworkOfferingOptions#name
       */
      public static UpdateNetworkOfferingOptions name(String name) {
         UpdateNetworkOfferingOptions options = new UpdateNetworkOfferingOptions();
         return options.name(name);
      }

      /**
       * @see UpdateNetworkOfferingOptions#displayText
       */
      public static UpdateNetworkOfferingOptions displayText(String displayText) {
         UpdateNetworkOfferingOptions options = new UpdateNetworkOfferingOptions();
         return options.displayText(displayText);
      }

      /**
       * @see UpdateNetworkOfferingOptions#availability
       */
      public static UpdateNetworkOfferingOptions availability(NetworkOfferingAvailabilityType availability) {
         UpdateNetworkOfferingOptions options = new UpdateNetworkOfferingOptions();
         return options.availability(availability);
      }

      /**
       * @see UpdateNetworkOfferingOptions#accountInDomain
       */
      public static UpdateNetworkOfferingOptions accountInDomain(String account, String domain) {
         UpdateNetworkOfferingOptions options = new UpdateNetworkOfferingOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see UpdateNetworkOfferingOptions#domainId
       */
      public static UpdateNetworkOfferingOptions domainId(String domainId) {
         UpdateNetworkOfferingOptions options = new UpdateNetworkOfferingOptions();
         return options.domainId(domainId);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public UpdateNetworkOfferingOptions accountInDomain(String account, String domain) {
      return UpdateNetworkOfferingOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public UpdateNetworkOfferingOptions domainId(String domainId) {
      return UpdateNetworkOfferingOptions.class.cast(super.domainId(domainId));
   }
}
