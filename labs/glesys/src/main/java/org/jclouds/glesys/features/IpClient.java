/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.glesys.features;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.glesys.domain.IpDetails;

/**
 * Provides synchronous access to IP Addresses.
 * <p/>
 *
 * @author Adrian Cole, Mattias Holmqvist
 * @see IpAsyncClient
 * @see <a href="https://customer.glesys.com/api.php" />
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface IpClient {

    /**
     * Take a free IP address and add it to this account. You can list free IP addresses with the function listFree().
     * Once your free IP on this account you can add it to a server with the add() function.
     *
     * @param ipAddress
     */
    void take(String ipAddress);

    /**
     * Return an unused IP address to the pool of free ips. If the IP address is allocated to a server,
     * it must first be removed by calling remove(ipAddress) before it can be released.
     *
     * @param ipAddress the IP address to be released
     */
    void release(String ipAddress);

    /**
     * Add an IP address to an server. The IP has to be free, but reserved to this account. You are able to list such addresses
     * with listOwn() and reserve an address for this account by using take(). To find free ips you can use ip/listfree
     * ip to an Xen-server you have to configure the server yourself, unless the ip was added during the c
     * server (server/create). You can get detailed information such as gateway and netmask using the ip
     *
     * @param ipAddress the IP address to remove
     * @param serverId  the server to add the IP address to
     */
    void addIpToServer(String ipAddress, String serverId);

    /**
     * Remove an IP address from a server. This does not release it back to GleSYS pool of free ips. The address will be
     * kept on the account so that you can use it for other servers or the same server at a later time. To completely remove
     * the IP address from this account, use the function release().
     *
     * @param ipAddress the IP address to remove
     * @param serverId  the server to remove the IP address from
     */
    void removeIpFromServer(String ipAddress, String serverId);

    /**
     * Get a set of all IP addresses that are available and not used on any account or server.
     *
     * @param ipversion  "4" or "6", for IPV4 or IPV6, respectively
     * @param datacenter the datacenter
     * @param platform   the platform
     * @return a set of free IP addresses
     */
    Set<String> listFree(String ipversion, String datacenter, String platform);

    /**
     * Get details about the given IP address such as gateway and netmask. Different details are available
     * on different platforms.
     *
     * @param ipAddress the ip address
     * @return details about the given IP address
     */
    IpDetails getIpDetails(String ipAddress);

}