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

import org.jclouds.fujitsu.fgcp.services.AdditionalDiskAsyncApi;
import org.jclouds.fujitsu.fgcp.services.DiskImageAsyncApi;
import org.jclouds.fujitsu.fgcp.services.FirewallAsyncApi;
import org.jclouds.fujitsu.fgcp.services.LoadBalancerAsyncApi;
import org.jclouds.fujitsu.fgcp.services.PublicIPAddressAsyncApi;
import org.jclouds.fujitsu.fgcp.services.SystemTemplateAsyncApi;
import org.jclouds.fujitsu.fgcp.services.VirtualDCAsyncApi;
import org.jclouds.fujitsu.fgcp.services.VirtualServerAsyncApi;
import org.jclouds.fujitsu.fgcp.services.VirtualSystemAsyncApi;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides asynchronous access to FGCP services.
 * 
 * @author Dies Koper
 */
public interface FGCPAsyncApi {
   public static final String VERSION = "2012-02-18";

   @Delegate
   VirtualDCAsyncApi getVirtualDCApi();

   @Delegate
   VirtualSystemAsyncApi getVirtualSystemApi();

   @Delegate
   VirtualServerAsyncApi getVirtualServerApi();

   @Delegate
   AdditionalDiskAsyncApi getAdditionalDiskApi();

   @Delegate
   SystemTemplateAsyncApi getSystemTemplateApi();

   @Delegate
   DiskImageAsyncApi getDiskImageApi();

   @Delegate
   FirewallAsyncApi getFirewallApi();

   @Delegate
   LoadBalancerAsyncApi getLoadBalancerApi();

   @Delegate
   PublicIPAddressAsyncApi getPublicIPAddressApi();
}
