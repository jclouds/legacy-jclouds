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

import org.jclouds.cloudstack.domain.StorageType;
import org.jclouds.cloudstack.domain.SystemVmType;

import com.google.common.collect.ImmutableSet;

/**
 * Options to control how service offerings are created
 * 
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api_2.2.12/global_admin/createServiceOffering.html"
 *      />
 * @author Andrei Savu
 */
public class CreateServiceOfferingOptions extends AccountInDomainOptions {

   public static final CreateServiceOfferingOptions NONE = new CreateServiceOfferingOptions();

   /**
    * @param hostTags
    *          the host tag for this service offering
    */
   public CreateServiceOfferingOptions hostTags(Set<String> hostTags) {
      this.queryParameters.replaceValues("hosttags", ImmutableSet.copyOf(hostTags));
      return this;
   }

   /**
    * @param isSystem
    *          is this a system vm offering
    */
   public CreateServiceOfferingOptions isSystem(boolean isSystem) {
      this.queryParameters.replaceValues("issystem", ImmutableSet.<String>of(isSystem + ""));
      return this;
   }

   /**
    * @param limitCpuUse
    *          restrict the CPU usage to committed service offering
    */
   public CreateServiceOfferingOptions limitCpuUse(boolean limitCpuUse) {
      this.queryParameters.replaceValues("limitcpuuse", ImmutableSet.<String>of(limitCpuUse + ""));
      return this;
   }

   /**
    * @param networkRateInMb
    *          data transfer rate in megabits per second allowed. Supported only
    *          for non-System offering and system offerings having "domainrouter"
    *          systemvmtype
    */
   public CreateServiceOfferingOptions networkRateInMb(int networkRateInMb) {
      this.queryParameters.replaceValues("networkrate", ImmutableSet.<String>of(networkRateInMb + ""));
      return this;
   }

   /**
    * @param highlyAvailable
    *          the HA for the service offering
    */
   public CreateServiceOfferingOptions highlyAvailable(boolean highlyAvailable) {
      this.queryParameters.replaceValues("offerha", ImmutableSet.<String>of(highlyAvailable + ""));
      return this;
   }

   /**
    * @param storageType
    *          the storage type of the service offering
    */
   public CreateServiceOfferingOptions storageType(StorageType storageType) {
      this.queryParameters.replaceValues("storagetype", ImmutableSet.<String>of(storageType.toString()));
      return this;
   }

   /**
    * @param systemVmType
    *          the system VM type. Possible types are "domainrouter", "consoleproxy" and "secondarystoragevm"
    */
   public CreateServiceOfferingOptions systemVmType(SystemVmType systemVmType) {
      this.queryParameters.replaceValues("systemvmtype", ImmutableSet.<String>of(systemVmType.toString()));
      return this;
   }

   /**
    * @param tags
    *          the tags for this service offering
    */
   public CreateServiceOfferingOptions tags(Set<String> tags) {
      this.queryParameters.replaceValues("tags", ImmutableSet.copyOf(tags));
      return this;
   }

   public static class Builder {

      /**
       * @see CreateServiceOfferingOptions#hostTags
       */
      public static CreateServiceOfferingOptions hostTags(Set<String> hostTags) {
         CreateServiceOfferingOptions options = new CreateServiceOfferingOptions();
         return options.hostTags(hostTags);
      }

      /**
       * @see CreateServiceOfferingOptions#isSystem
       */
      public static CreateServiceOfferingOptions isSystem(boolean isSystem) {
         CreateServiceOfferingOptions options = new CreateServiceOfferingOptions();
         return options.isSystem(isSystem);
      }

      /**
       * @see CreateServiceOfferingOptions#limitCpuUse
       */
      public static CreateServiceOfferingOptions limitCpuUse(boolean limitCpuUse) {
         CreateServiceOfferingOptions options = new CreateServiceOfferingOptions();
         return options.limitCpuUse(limitCpuUse);
      }

      /**
       * @see CreateServiceOfferingOptions#networkRateInMb
       */
      public static CreateServiceOfferingOptions networkRateInMb(int networkRate) {
         CreateServiceOfferingOptions options = new CreateServiceOfferingOptions();
         return options.networkRateInMb(networkRate);
      }

      /**
       * @see CreateServiceOfferingOptions#highlyAvailable
       */
      public static CreateServiceOfferingOptions highlyAvailable(boolean highlyAvailable) {
         CreateServiceOfferingOptions options = new CreateServiceOfferingOptions();
         return options.highlyAvailable(highlyAvailable);
      }

      /**
       * @see CreateServiceOfferingOptions#storageType
       */
      public static CreateServiceOfferingOptions storageType(StorageType storageType) {
         CreateServiceOfferingOptions options = new CreateServiceOfferingOptions();
         return options.storageType(storageType);
      }

      /**
       * @see CreateServiceOfferingOptions#systemVmType
       */
      public static CreateServiceOfferingOptions systemVmType(SystemVmType systemVmType) {
         CreateServiceOfferingOptions options = new CreateServiceOfferingOptions();
         return options.systemVmType(systemVmType);
      }

      /**
       * @see CreateServiceOfferingOptions#tags
       */
      public static CreateServiceOfferingOptions tags(Set<String> tags) {
         CreateServiceOfferingOptions options = new CreateServiceOfferingOptions();
         return options.tags(tags);
      }

      /**
       * @see CreateServiceOfferingOptions#accountInDomain
       */
      public static CreateServiceOfferingOptions accountInDomain(String account, String domain) {
         CreateServiceOfferingOptions options = new CreateServiceOfferingOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see CreateServiceOfferingOptions#domainId
       */
      public static CreateServiceOfferingOptions domainId(String domainId) {
         CreateServiceOfferingOptions options = new CreateServiceOfferingOptions();
         return options.domainId(domainId);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CreateServiceOfferingOptions accountInDomain(String account, String domain) {
      return CreateServiceOfferingOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CreateServiceOfferingOptions domainId(String domainId) {
      return CreateServiceOfferingOptions.class.cast(super.domainId(domainId));
   }
}
