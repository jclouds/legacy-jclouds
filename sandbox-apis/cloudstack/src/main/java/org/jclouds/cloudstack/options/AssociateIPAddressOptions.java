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
 * Options used to acquire and associate a public IP to an account.
 * 
 * @see <a href="http://download.cloud.com/releases/2.2.0/api/user/associateIpAddress.html" />
 * @author Adrian Cole
 */
public class AssociateIPAddressOptions extends BaseHttpRequestOptions {

   public static final AssociateIPAddressOptions NONE = new AssociateIPAddressOptions();

   /**
    * @param domainId
    *           the ID of the domain to associate with this IP address
    */
   public AssociateIPAddressOptions domainId(long domainId) {
      this.queryParameters.replaceValues("domainid", ImmutableSet.of(domainId + ""));
      return this;

   }

   /**
    * @param account
    *           the account to associate with this IP address
    */
   public AssociateIPAddressOptions account(String account) {
      this.queryParameters.replaceValues("account", ImmutableSet.of(account));
      return this;
   }

   /**
    * @param networkId
    *           The network this ip address should be associated to.
    */
   public AssociateIPAddressOptions networkId(long networkId) {
      this.queryParameters.replaceValues("networkid", ImmutableSet.of(networkId + ""));
      return this;

   }

   public static class Builder {

      /**
       * @see AssociateIPAddressOptions#account
       */
      public static AssociateIPAddressOptions account(String account) {
         AssociateIPAddressOptions options = new AssociateIPAddressOptions();
         return options.account(account);
      }

      /**
       * @see AssociateIPAddressOptions#domainId
       */
      public static AssociateIPAddressOptions domainId(long id) {
         AssociateIPAddressOptions options = new AssociateIPAddressOptions();
         return options.domainId(id);
      }

      /**
       * @see AssociateIPAddressOptions#networkId
       */
      public static AssociateIPAddressOptions networkId(long networkId) {
         AssociateIPAddressOptions options = new AssociateIPAddressOptions();
         return options.networkId(networkId);
      }
   }
}
