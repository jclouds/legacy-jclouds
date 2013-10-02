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
 * Options to control how disk offerings are created
 * 
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api_2.2.12/global_admin/updateDiskOffering.html"
 *      />
 * @author Andrei Savu
 */
public class UpdateDiskOfferingOptions extends AccountInDomainOptions {

   public static final UpdateDiskOfferingOptions NONE = new UpdateDiskOfferingOptions();

   /**
    * @param name
    *       service offering name
    */
   public UpdateDiskOfferingOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.<String>of(name));
      return this;
   }

   /**
    * @param displayText
    *       service offering display text
    */
   public UpdateDiskOfferingOptions displayText(String displayText) {
      this.queryParameters.replaceValues("displaytext", ImmutableSet.<String>of(displayText));
      return this;
   }

   public static class Builder {

      /**
       * @see org.jclouds.cloudstack.options.UpdateDiskOfferingOptions#name
       */
      public static UpdateDiskOfferingOptions name(String name) {
         UpdateDiskOfferingOptions options = new UpdateDiskOfferingOptions();
         return options.name(name);
      }

      /**
       * @see org.jclouds.cloudstack.options.UpdateDiskOfferingOptions#displayText
       */
      public static UpdateDiskOfferingOptions displayText(String displayText) {
         UpdateDiskOfferingOptions options = new UpdateDiskOfferingOptions();
         return options.displayText(displayText);
      }

      /**
       * @see org.jclouds.cloudstack.options.UpdateDiskOfferingOptions#accountInDomain
       */
      public static UpdateDiskOfferingOptions accountInDomain(String account, String domain) {
         UpdateDiskOfferingOptions options = new UpdateDiskOfferingOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see org.jclouds.cloudstack.options.UpdateDiskOfferingOptions#domainId
       */
      public static UpdateDiskOfferingOptions domainId(String domainId) {
         UpdateDiskOfferingOptions options = new UpdateDiskOfferingOptions();
         return options.domainId(domainId);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public UpdateDiskOfferingOptions accountInDomain(String account, String domain) {
      return UpdateDiskOfferingOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public UpdateDiskOfferingOptions domainId(String domainId) {
      return UpdateDiskOfferingOptions.class.cast(super.domainId(domainId));
   }
}
