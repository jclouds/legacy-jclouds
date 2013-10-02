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

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Options used to control what disk offerings are returned
 * 
 * @see <a href=
 *      "http://download.cloud.com/releases/3.0.3/api_3.0.3/root_admin/assignVirtualMachine.html"
 *      />
 * @author Adrian Cole
 */
public class AssignVirtualMachineOptions extends AccountInDomainOptions {

   public static final AssignVirtualMachineOptions NONE = new AssignVirtualMachineOptions();

   /**
    * @param networkId
    *           network id used by virtual machine
    */
   public AssignVirtualMachineOptions networkId(String networkId) {
      this.queryParameters.replaceValues("networkids", ImmutableSet.of(networkId + ""));
      return this;
   }

   /**
    * @param networkIds
    *           network ids used by virtual machine
    */
   public AssignVirtualMachineOptions networkIds(Iterable<String> networkIds) {
      this.queryParameters.replaceValues("networkids", ImmutableSet.of(Joiner.on(',').join(networkIds)));
      return this;
   }

   public Iterable<String> getNetworkIds() {
      if (queryParameters.get("networkids").size() == 1) {
         return Iterables.transform(
               Splitter.on(",").split(Iterables.getOnlyElement(queryParameters.get("networkids"))),
               new Function<String, String>() {

                  @Override
                  public String apply(String arg0) {
                     return arg0;
                  }

               });
      } else {
         return ImmutableSet.<String> of();
      }
   }

   /**
    * @param securityGroupId
    *           security group applied to the virtual machine. Should be passed
    *           only when vm is created from a zone with Basic Network support
    */
   public AssignVirtualMachineOptions securityGroupId(String securityGroupId) {
      this.queryParameters.replaceValues("securitygroupids", ImmutableSet.of(securityGroupId + ""));
      return this;
   }

   /**
    * @param securityGroupIds
    *           security groups applied to the virtual machine. Should be passed
    *           only when vm is created from a zone with Basic Network support
    */
   public AssignVirtualMachineOptions securityGroupIds(Iterable<String> securityGroupIds) {
      this.queryParameters.replaceValues("securitygroupids", ImmutableSet.of(Joiner.on(',').join(securityGroupIds)));
      return this;
   }

   public static class Builder {
      /**
       * @see AssignVirtualMachineOptions#networkId
       */
      public static AssignVirtualMachineOptions networkId(String id) {
         AssignVirtualMachineOptions options = new AssignVirtualMachineOptions();
         return options.networkId(id);
      }

      /**
       * @see AssignVirtualMachineOptions#networkIds
       */
      public static AssignVirtualMachineOptions networkIds(Iterable<String> networkIds) {
         AssignVirtualMachineOptions options = new AssignVirtualMachineOptions();
         return options.networkIds(networkIds);
      }

      /**
       * @see AssignVirtualMachineOptions#securityGroupId
       */
      public static AssignVirtualMachineOptions securityGroupId(String id) {
         AssignVirtualMachineOptions options = new AssignVirtualMachineOptions();
         return options.securityGroupId(id);
      }

      /**
       * @see AssignVirtualMachineOptions#securityGroupIds
       */
      public static AssignVirtualMachineOptions securityGroupIds(Iterable<String> securityGroupIds) {
         AssignVirtualMachineOptions options = new AssignVirtualMachineOptions();
         return options.securityGroupIds(securityGroupIds);
      }

      /**
       * @see AssignVirtualMachineOptions#accountInDomain
       */
      public static AssignVirtualMachineOptions accountInDomain(String account, String domain) {
         AssignVirtualMachineOptions options = new AssignVirtualMachineOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see AssignVirtualMachineOptions#domainId
       */
      public static AssignVirtualMachineOptions domainId(String domainId) {
         AssignVirtualMachineOptions options = new AssignVirtualMachineOptions();
         return options.domainId(domainId);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AssignVirtualMachineOptions accountInDomain(String account, String domain) {
      return AssignVirtualMachineOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AssignVirtualMachineOptions domainId(String domainId) {
      return AssignVirtualMachineOptions.class.cast(super.domainId(domainId));
   }
}
