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
package org.jclouds.fujitsu.fgcp.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.base.Function;

/**
 * Determines the id of the firewall associated with a virtual system, server,
 * load balancer or disk.
 * 
 * @author Dies Koper
 */
@Singleton
public class ResourceIdToFirewallId implements Function<String, String> {

   private ResourceIdToSystemId toSystemId;

   @Inject
   private ResourceIdToFirewallId(ResourceIdToSystemId resourceIdToSystemId) {
      this.toSystemId = checkNotNull(resourceIdToSystemId,
            "resourceIdToSystemId");
   }

   @Override
   public String apply(String id) {
      checkNotNull(id, "resource id");

      return toSystemId.apply(id) + "-S-0001";
   }
}
