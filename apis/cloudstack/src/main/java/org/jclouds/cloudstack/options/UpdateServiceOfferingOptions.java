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

import com.google.common.collect.ImmutableSet;

/**
 * Options to control how service offerings are created
 * 
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api_2.2.12/global_admin/createServiceOffering.html"
 *      />
 * @author Andrei Savu
 */
public class UpdateServiceOfferingOptions extends AccountInDomainOptions {

   public static final UpdateServiceOfferingOptions NONE = new UpdateServiceOfferingOptions();

   /**
    * @param name
    *       service offering name
    */
   public UpdateServiceOfferingOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.<String>of(name));
      return this;
   }

   /**
    * @param displayText
    *       service offering display text
    */
   public UpdateServiceOfferingOptions displayText(String displayText) {
      this.queryParameters.replaceValues("displaytext", ImmutableSet.<String>of(displayText));
      return this;
   }

   public static class Builder {

      /**
       * @see UpdateServiceOfferingOptions#name
       */
      public static UpdateServiceOfferingOptions name(String name) {
         UpdateServiceOfferingOptions options = new UpdateServiceOfferingOptions();
         return options.name(name);
      }

      /**
       * @see UpdateServiceOfferingOptions#displayText
       */
      public static UpdateServiceOfferingOptions displayText(String displayText) {
         UpdateServiceOfferingOptions options = new UpdateServiceOfferingOptions();
         return options.displayText(displayText);
      }

      /**
       * @see UpdateServiceOfferingOptions#accountInDomain
       */
      public static UpdateServiceOfferingOptions accountInDomain(String account, String domain) {
         UpdateServiceOfferingOptions options = new UpdateServiceOfferingOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see UpdateServiceOfferingOptions#domainId
       */
      public static UpdateServiceOfferingOptions domainId(String domainId) {
         UpdateServiceOfferingOptions options = new UpdateServiceOfferingOptions();
         return options.domainId(domainId);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public UpdateServiceOfferingOptions accountInDomain(String account, String domain) {
      return UpdateServiceOfferingOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public UpdateServiceOfferingOptions domainId(String domainId) {
      return UpdateServiceOfferingOptions.class.cast(super.domainId(domainId));
   }
}
