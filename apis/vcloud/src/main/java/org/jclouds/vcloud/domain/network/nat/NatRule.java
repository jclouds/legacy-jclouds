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
package org.jclouds.vcloud.domain.network.nat;

import org.jclouds.javax.annotation.Nullable;

/**
 * 
 * Defines a rule associated with Nat
 * 
 * @since vcloud api 0.9
 * 
 * @author Adrian Cole
 */
public interface NatRule {
   /**
    * IP address to which this NAT rule maps the IP address specified in the InternalIp element.
    */
   @Nullable
   String getExternalIP();
}