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
package org.jclouds.glesys.features;

import org.jclouds.glesys.domain.IpDetails;
import org.jclouds.glesys.options.ListIpOptions;

import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to IP Addresses.
 * <p/>
 *
 * @author Adrian Cole, Mattias Holmqvist, Adam Lowe
 * @see IpAsyncApi
 * @see <a href="https://github.com/GleSYS/API/wiki/API-Documentation" />
 */
public interface IpApi {
   /**
    * Get a set of all IP addresses that are available and not used on any account or server.
    *
    * @param ipVersion  4 or 6, for IPV4 or IPV6, respectively
    * @param datacenter the datacenter
    * @param platform   the platform
    * @return a set of free IP addresses
    */
   FluentIterable<String> listFree(int ipVersion, String datacenter, String platform);

   /**
    * Take a free IP address and add it to this account. You can list free IP addresses with the function listFree().
    * Once your free IP on this account you can add it to a server with the add() function.
    *
    * @param ipAddress the IP address to be add to this account (reserve)
    */
   IpDetails take(String ipAddress);

   /**
    * Return an unused IP address to the pool of free ips. If the IP address is allocated to a server,
    * it must first be removed by calling remove(ipAddress) before it can be released.
    *
    * @param ipAddress the IP address to be released
    */
   IpDetails release(String ipAddress);

   /**
    * Get IP addresses associated with your account (reserved, assigned to servers, etc)
    *
    * @param options options to filter the results (by IPV4/6, serverId, etc)
    * @return the set of IP addresses
    */
   FluentIterable<IpDetails> list(ListIpOptions... options);

   /**
    * Get details about the given IP address such as gateway and netmask. Different details are available
    * on different platforms.
    *
    * @param ipAddress the ip address
    * @return details about the given IP address
    */
   IpDetails get(String ipAddress);

   /**
    * Add an IP address to an server. The IP has to be free, but reserved to this account. You are able to list such addresses
    * with listOwn() and reserve an address for this account by using take(). To find free ips you can use ip/listfree
    * ip to an Xen-server you have to configure the server yourself, unless the ip was added during the c
    * server (server/create). You can get detailed information such as gateway and netmask using the ip
    *
    * @param ipAddress the IP address to remove
    * @param serverId  the server to add the IP address to
    */
   IpDetails addToServer(String ipAddress, String serverId);

   /**
    * Remove an IP address from a server. This does not release it back to GleSYS pool of free ips. The address will be
    * kept on the account so that you can use it for other servers or the same server at a later time. To completely remove
    * the IP address from this account, use removeFromServerAndRelease to do so
    *
    * @param ipAddress the IP address to remove
    * @param serverId  the server to remove the IP address from
    * @see #removeFromServerAndRelease
    */
   IpDetails removeFromServer(String ipAddress, String serverId);

   /**
    * Remove an IP address from a server and release it back to GleSYS pool of free ips.
    *
    * @param ipAddress the IP address to remove
    * @param serverId  the server to remove the IP address from
    * @see #removeFromServer
    */
   IpDetails removeFromServerAndRelease(String ipAddress, String serverId);

   /**
    * Sets PTR data for an IP. Use ip/listown or ip/details to get current PTR data
    */
   IpDetails setPtr(String ipAddress, String ptr);

   /**
    * Resets PTR data for an IP back to the default value
    */
   IpDetails resetPtr(String ipAddress);

}
