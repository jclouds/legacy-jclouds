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
package org.jclouds.vcloud.compute.util;

import static com.google.common.collect.Iterables.filter;

import java.util.Set;

import org.jclouds.cim.CIMPredicates;
import org.jclouds.cim.ResourceAllocationSettingData;
import org.jclouds.cim.ResourceAllocationSettingData.ResourceType;
import org.jclouds.compute.domain.CIMOperatingSystem;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.vcloud.domain.NetworkConnection;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.Vm;
import org.jclouds.vcloud.domain.ovf.VCloudNetworkAdapter;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
public class VCloudComputeUtils {
   public static OperatingSystem toComputeOs(VApp vApp, OperatingSystem defaultOs) {
      CIMOperatingSystem cimOs = toComputeOs(vApp);
      return cimOs != null ? cimOs : defaultOs;
   }

   public static CIMOperatingSystem toComputeOs(VApp vApp) {
      // TODO we need to change the design so that it doesn't assume single-vms
      return vApp.getChildren().size() > 0 ? toComputeOs(Iterables.get(vApp.getChildren(), 0)) : null;
   }

   public static CIMOperatingSystem toComputeOs(Vm vm) {
      return CIMOperatingSystem.toComputeOs(vm.getOperatingSystemSection());
   }

   public static String getVirtualSystemIdentifierOfFirstVMIn(VApp vApp) {
      return vApp.getChildren().size() > 0 ? getVirtualSystemIdentifierOf(Iterables.get(vApp.getChildren(), 0)) : null;
   }

   public static String getVirtualSystemIdentifierOf(Vm vm) {
      if (vm.getVirtualHardwareSection() != null && vm.getVirtualHardwareSection().getSystem() != null)
         return vm.getVirtualHardwareSection().getSystem().getVirtualSystemIdentifier();
      return null;
   }

   public static LoginCredentials getCredentialsFrom(VApp vApp) {
      return vApp.getChildren().size() > 0 ? getCredentialsFrom(Iterables.get(vApp.getChildren(), 0)) : null;
   }

   public static LoginCredentials getCredentialsFrom(VAppTemplate vApp) {
      return vApp.getChildren().size() > 0 ? getCredentialsFrom(Iterables.get(vApp.getChildren(), 0)) : null;
   }

   public static LoginCredentials getCredentialsFrom(Vm vm) {
      LoginCredentials.Builder builder = LoginCredentials.builder();
      if (vm.getGuestCustomizationSection() != null)
         builder.password(vm.getGuestCustomizationSection().getAdminPassword());
      return builder.build();
   }

   public static Set<String> getIpsFromVApp(VApp vApp) {
      // TODO make this work with composite vApps
      if (vApp.getChildren().size() == 0)
         return ImmutableSet.of();
      Builder<String> ips = ImmutableSet.builder();
      Vm vm = Iterables.get(vApp.getChildren(), 0);
      // TODO: figure out how to differentiate public from private ip addresses
      // assumption is that we'll do this from the network object, which may
      // have
      // enough data to tell whether or not it is a public network without
      // string
      // parsing. At worst, we could have properties set per cloud provider to
      // declare the networks which are public, then check against these in
      // networkconnection.getNetwork
      if (vm.getNetworkConnectionSection() != null) {
         for (NetworkConnection connection : vm.getNetworkConnectionSection().getConnections()) {
            if (connection.getIpAddress() != null)
               ips.add(connection.getIpAddress());
            if (connection.getExternalIpAddress() != null)
               ips.add(connection.getExternalIpAddress());
         }
      } else {
         for (ResourceAllocationSettingData net : filter(vm.getVirtualHardwareSection().getItems(),
               CIMPredicates.resourceTypeIn(ResourceType.ETHERNET_ADAPTER))) {
            if (net instanceof VCloudNetworkAdapter) {
               VCloudNetworkAdapter vNet = VCloudNetworkAdapter.class.cast(net);
               if (vNet.getIpAddress() != null)
                  ips.add(vNet.getIpAddress());
            }
         }
      }
      return ips.build();
   }
}
