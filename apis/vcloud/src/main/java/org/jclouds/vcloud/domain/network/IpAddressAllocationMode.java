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
package org.jclouds.vcloud.domain.network;


/**
 * 
 * The IpAddressAllocationMode element specifies how an IP address is allocated to this connection.
 * 
 * @author Adrian Cole
 */
public enum IpAddressAllocationMode {
   /**
    * no IP addressing mode specified
    * 
    * @since vcloud api 1.0
    */
   NONE,
   /**
    * static IP address assigned manually
    * 
    * @since vcloud api 1.0
    */
   MANUAL,
   /**
    * static IP address allocated from a pool
    * 
    * @since vcloud api 1.0
    */
   POOL,
   /**
    * IP address assigned by DHCP
    * 
    * @since vcloud api 1.0
    */
   DHCP;

}