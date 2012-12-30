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
package org.jclouds.fujitsu.fgcp;

import org.jclouds.fujitsu.fgcp.services.AdditionalDiskApi;
import org.jclouds.fujitsu.fgcp.services.DiskImageApi;
import org.jclouds.fujitsu.fgcp.services.FirewallApi;
import org.jclouds.fujitsu.fgcp.services.LoadBalancerApi;
import org.jclouds.fujitsu.fgcp.services.PublicIPAddressApi;
import org.jclouds.fujitsu.fgcp.services.SystemTemplateApi;
import org.jclouds.fujitsu.fgcp.services.VirtualDCApi;
import org.jclouds.fujitsu.fgcp.services.VirtualServerApi;
import org.jclouds.fujitsu.fgcp.services.VirtualSystemApi;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides synchronous access to FGCP services.
 * 
 * @author Dies Koper
 */
public interface FGCPApi {

   @Delegate
   VirtualDCApi getVirtualDCApi();

   @Delegate
   VirtualSystemApi getVirtualSystemApi();

   @Delegate
   VirtualServerApi getVirtualServerApi();

   @Delegate
   AdditionalDiskApi getAdditionalDiskApi();

   @Delegate
   SystemTemplateApi getSystemTemplateApi();

   @Delegate
   DiskImageApi getDiskImageApi();

   @Delegate
   FirewallApi getFirewallApi();

   @Delegate
   LoadBalancerApi getLoadBalancerApi();

   @Delegate
   PublicIPAddressApi getPublicIPAddressApi();
}
