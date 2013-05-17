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

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * Options to control how disk offerings are created
 * 
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api_2.2.12/global_admin/createDiskOffering.html"
 *      />
 * @author Andrei Savu
 */
public class CreateDiskOfferingOptions extends AccountInDomainOptions {

   public static final CreateDiskOfferingOptions NONE = new CreateDiskOfferingOptions();

   /**
    * @param customized
    *          whether disk offering is custom or not
    */
   public CreateDiskOfferingOptions customized(boolean customized) {
      this.queryParameters.replaceValues("customized", ImmutableSet.<String>of(customized + ""));
      return this;
   }

   /**
    * @param diskSizeInGB
    *          size of the disk offering in GB
    */
   public CreateDiskOfferingOptions diskSizeInGB(int diskSizeInGB) {
      this.queryParameters.replaceValues("disksize", ImmutableSet.<String>of(diskSizeInGB + ""));
      return this;
   }

   /**
    * @param tags
    *          the tags for this service offering
    */
   public CreateDiskOfferingOptions tags(Set<String> tags) {
      this.queryParameters.replaceValues("tags", ImmutableSet.copyOf(tags));
      return this;
   }

   public static class Builder {

      /**
       * @see CreateDiskOfferingOptions#customized
       */
      public static CreateDiskOfferingOptions customized(boolean customized) {
         CreateDiskOfferingOptions options = new CreateDiskOfferingOptions();
         return options.customized(customized);
      }

      /**
       * @see CreateDiskOfferingOptions#diskSizeInGB
       */
      public static CreateDiskOfferingOptions diskSizeInGB(int diskSizeInGB) {
         CreateDiskOfferingOptions options = new CreateDiskOfferingOptions();
         return options.diskSizeInGB(diskSizeInGB);
      }

      /**
       * @see CreateDiskOfferingOptions#tags
       */
      public static CreateDiskOfferingOptions tags(Set<String> tags) {
         CreateDiskOfferingOptions options = new CreateDiskOfferingOptions();
         return options.tags(tags);
      }

      /**
       * @see CreateDiskOfferingOptions#accountInDomain
       */
      public static CreateDiskOfferingOptions accountInDomain(String account, String domain) {
         CreateDiskOfferingOptions options = new CreateDiskOfferingOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see CreateDiskOfferingOptions#domainId
       */
      public static CreateDiskOfferingOptions domainId(String domainId) {
         CreateDiskOfferingOptions options = new CreateDiskOfferingOptions();
         return options.domainId(domainId);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CreateDiskOfferingOptions accountInDomain(String account, String domain) {
      return CreateDiskOfferingOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CreateDiskOfferingOptions domainId(String domainId) {
      return CreateDiskOfferingOptions.class.cast(super.domainId(domainId));
   }
}
