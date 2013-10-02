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
 * Options used to acquire and associate a public IP to an account.
 * 
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api/user/associateIpAddress.html"
 *      />
 * @author Adrian Cole
 */
public class AssociateIPAddressOptions extends AccountInDomainOptions {

   public static final AssociateIPAddressOptions NONE = new AssociateIPAddressOptions();

   /**
    * @param networkId
    *           The network this ip address should be associated to.
    */
   public AssociateIPAddressOptions networkId(String networkId) {
      this.queryParameters.replaceValues("networkid", ImmutableSet.of(networkId + ""));
      return this;

   }

   /**
    * @param projectId
    *          Project for the IP
    */
   public AssociateIPAddressOptions projectId(String projectId) {
      this.queryParameters.replaceValues("projectid", ImmutableSet.of(projectId + ""));
      return this;
   }

   public static class Builder {

      /**
       * @see AssociateIPAddressOptions#networkId
       */
      public static AssociateIPAddressOptions networkId(String networkId) {
         AssociateIPAddressOptions options = new AssociateIPAddressOptions();
         return options.networkId(networkId);
      }

      /**
       * @see AssociateIPAddressOptions#accountInDomain
       */
      public static AssociateIPAddressOptions accountInDomain(String account, String domain) {
         AssociateIPAddressOptions options = new AssociateIPAddressOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see AssociateIPAddressOptions#domainId
       */
      public static AssociateIPAddressOptions domainId(String domainId) {
         AssociateIPAddressOptions options = new AssociateIPAddressOptions();
         return options.domainId(domainId);
      }

      /**
       * @see AssociateIPAddressOptions#projectId(String)
       */
      public static AssociateIPAddressOptions projectId(String projectId) {
         AssociateIPAddressOptions options = new AssociateIPAddressOptions();
         return options.projectId(projectId);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AssociateIPAddressOptions accountInDomain(String account, String domain) {
      return AssociateIPAddressOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AssociateIPAddressOptions domainId(String domainId) {
      return AssociateIPAddressOptions.class.cast(super.domainId(domainId));
   }
}
