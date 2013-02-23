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
package org.jclouds.vcloud.director.v1_5.compute.util;

import static com.google.common.collect.Iterables.filter;

import java.util.Set;

import org.jclouds.cim.OSType;
import org.jclouds.compute.domain.CIMOperatingSystem;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.dmtf.CIMPredicates;
import org.jclouds.dmtf.cim.ResourceAllocationSettingData;
import org.jclouds.dmtf.cim.ResourceAllocationSettingData.ResourceType;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.Vm;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkConnection;
import org.jclouds.vcloud.director.v1_5.domain.section.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConnectionSection;
import org.jclouds.vcloud.director.v1_5.domain.section.OperatingSystemSection;
import org.jclouds.vcloud.director.v1_5.domain.section.VirtualHardwareSection;
import org.jclouds.vcloud.director.v1_5.functions.SectionForVApp;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * 
 * @author danikov
 */
public class VCloudDirectorComputeUtils {
   
   public static OperatingSystem toComputeOs(VApp vApp, OperatingSystem defaultOs) {
      CIMOperatingSystem cimOs = toComputeOs(vApp);
      return cimOs != null ? cimOs : defaultOs;
   }

   public static CIMOperatingSystem toComputeOs(VApp vApp) {
      // TODO we need to change the design so that it doesn't assume single-vms
      return vApp.getChildren().getVms().size() > 0 ? toComputeOs(Iterables.get(vApp.getChildren().getVms(), 0)) : null;
   }
   
   private static SectionForVApp<OperatingSystemSection> findOperatingSystemSectionForVApp = 
         new SectionForVApp<OperatingSystemSection>(OperatingSystemSection.class);
   
   public static CIMOperatingSystem toComputeOs(Vm vm) {
      return toComputeOs(findOperatingSystemSectionForVApp.apply(vm));
   }
   
   public static CIMOperatingSystem toComputeOs(OperatingSystemSection os) {
      return new CIMOperatingSystem(OSType.fromValue(os.getId()), "", null, os.getDescription());
   }

   public static String getVirtualSystemIdentifierOfFirstVMIn(VApp vApp) {
      return vApp.getChildren().getVms().size() > 0 ? 
            getVirtualSystemIdentifierOf(Iterables.get(vApp.getChildren().getVms(), 0)) : null;
   }
   
   @Inject private static SectionForVApp<VirtualHardwareSection> findVirtualHardwareSectionForVApp = 
         new SectionForVApp<VirtualHardwareSection>(VirtualHardwareSection.class);

   public static String getVirtualSystemIdentifierOf(Vm vm) {
      VirtualHardwareSection virtualHardwareSection = findVirtualHardwareSectionForVApp.apply(vm);
      if (virtualHardwareSection != null && virtualHardwareSection.getSystem() != null)
         return virtualHardwareSection.getSystem().getVirtualSystemIdentifier();
      return null;
   }

   public static LoginCredentials getCredentialsFrom(VApp vApp) {
      return vApp.getChildren().getVms().size() > 0 ? 
            getCredentialsFrom(Iterables.get(vApp.getChildren().getVms(), 0)) : null;
   }

   public static LoginCredentials getCredentialsFrom(VAppTemplate vApp) {
      return vApp.getChildren().size() > 0 ? getCredentialsFrom(Iterables.get(vApp.getChildren(), 0)) : null;
   }
   
   @Inject private static SectionForVApp<GuestCustomizationSection> findGuestCustomizationSectionForVApp = 
         new SectionForVApp<GuestCustomizationSection>(GuestCustomizationSection.class);

   public static LoginCredentials getCredentialsFrom(Vm vm) {
      LoginCredentials.Builder builder = LoginCredentials.builder();
      GuestCustomizationSection guestCustomizationSection = findGuestCustomizationSectionForVApp.apply(vm);
      if (guestCustomizationSection != null)
         builder.password(guestCustomizationSection.getAdminPassword());
      return builder.build();
   }
   
   @Inject private static SectionForVApp<NetworkConnectionSection> findNetworkConnectionSectionForVApp = 
         new SectionForVApp<NetworkConnectionSection>(NetworkConnectionSection.class);

   public static Set<String> getIpsFromVApp(VApp vApp) {
      // TODO make this work with composite vApps
      if (vApp.getChildren().getVms().size() == 0)
         return ImmutableSet.of();
      return getIpsFromVm(Iterables.get(vApp.getChildren().getVms(), 0));
   }
   
   public static Set<String> getIpsFromVm(Vm vm) {
      Builder<String> ips = ImmutableSet.builder();
      // TODO: figure out how to differentiate public from private ip addresses
      // assumption is that we'll do this from the network object, which may
      // have
      // enough data to tell whether or not it is a public network without
      // string
      // parsing. At worst, we could have properties set per cloud provider to
      // declare the networks which are public, then check against these in
      // networkconnection.getNetwork
      NetworkConnectionSection networkConnectionSection = findNetworkConnectionSectionForVApp.apply(vm);
      if (networkConnectionSection != null) {
         for (NetworkConnection connection : networkConnectionSection.getNetworkConnections()) {
            if (connection.getIpAddress() != null)
               ips.add(connection.getIpAddress());
            if (connection.getExternalIpAddress() != null)
               ips.add(connection.getExternalIpAddress());
         }
      } else {
         for (ResourceAllocationSettingData net : filter(findVirtualHardwareSectionForVApp.apply(vm).getItems(),
               CIMPredicates.resourceTypeIn(ResourceType.ETHERNET_ADAPTER))) {
            // FIXME: not yet implemented
//            if (net instanceof VCloudNetworkAdapter) {
//               VCloudNetworkAdapter vNet = VCloudNetworkAdapter.class.cast(net);
//               if (vNet.getIpAddress() != null)
//                  ips.add(vNet.getIpAddress());
//            }
         }
      }
      return ips.build();
   }
}
