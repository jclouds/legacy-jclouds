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
package org.jclouds.glesys.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * @author Adam Lowe
 */
public class ListIpOptions extends BaseHttpRequestOptions {

   public static class Builder {
      /**
       * @see org.jclouds.glesys.options.ListIpOptions#used
       */
      public static ListIpOptions used(boolean used) {
         return new ListIpOptions().used(used);
      }

      /**
       * @see org.jclouds.glesys.options.ListIpOptions#serverId
       */
      public static ListIpOptions serverId(String serverId) {
         return new ListIpOptions().serverId(serverId);
      }

      /**
       * @see org.jclouds.glesys.options.ListIpOptions#ipVersion
       */
      public static ListIpOptions ipVersion(int ipVersion) {
         return new ListIpOptions().ipVersion(ipVersion);
      }

      /**
       * @see org.jclouds.glesys.options.ListIpOptions#datacenter
       */
      public static ListIpOptions datacenter(String datacenter) {
         return new ListIpOptions().datacenter(datacenter);
      }

      /**
       * @see org.jclouds.glesys.options.ListIpOptions#platform
       */
      public static ListIpOptions platform(String platform) {
         return new ListIpOptions().platform(platform);
      }

   }

   /**
    * Retrieve only IPs that are in use
    */
   public ListIpOptions used(boolean used) {
      formParameters.put("used", Boolean.toString(used));
      return this;
   }

   /**
    * Retrieve only IP assigned to the specified server
    */
   public ListIpOptions serverId(String serverId) {
      formParameters.put("serverid", serverId);
      return this;
   }

   /**
    * Retrieve only IPs of the requested version
    */
   public ListIpOptions ipVersion(int ipVersion) {
      formParameters.put("ipversion", Integer.toString(ipVersion));
      return this;
   }

   /**
    * Retrieve only IPs served in the specified datacenter
    */
   public ListIpOptions datacenter(String datacenter) {
      formParameters.put("datacenter", datacenter);
      return this;
   }

   /**
    * Retrieve only IPs served on the specified platform
    */
   public ListIpOptions platform(String platform) {
      formParameters.put("platform", platform);
      return this;
   }

}
