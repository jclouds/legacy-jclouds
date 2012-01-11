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
package org.jclouds.ec2.domain;

import java.util.Set;

import com.google.common.collect.Multimap;

/**
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-IpPermissionType.html"
 *      />
 * @author Adrian Cole
 */
public interface IpPermission extends Comparable<IpPermission> {

   /**
    * Start of port range for the TCP and UDP protocols, or an ICMP type number.
    * An ICMP type number of -1 indicates a wildcard (i.e., any ICMP type
    * number).
    */
   int getFromPort();

   /**
    * End of port range for the TCP and UDP protocols, or an ICMP code. An ICMP
    * code of -1 indicates a wildcard (i.e., any ICMP code).
    */
   int getToPort();

   /**
    * List of security group and user ID pairs.
    */
   Multimap<String, String> getUserIdGroupPairs();

   /**
    * List of security group Ids
    */
   Set<String> getGroupIds();

   /**
    * IP protocol
    */
   IpProtocol getIpProtocol();

   /**
    * IP ranges.
    */
   Set<String> getIpRanges();
}