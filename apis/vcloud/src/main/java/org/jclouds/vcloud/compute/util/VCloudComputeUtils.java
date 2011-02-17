/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vcloud.compute.util;

import static com.google.common.collect.Iterables.filter;
import static org.jclouds.vcloud.predicates.VCloudPredicates.resourceType;

import java.util.Set;

import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.os.CIMOperatingSystem;
import org.jclouds.domain.Credentials;
import org.jclouds.vcloud.domain.NetworkConnection;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.Vm;
import org.jclouds.vcloud.domain.ovf.OvfEnvelope;
import org.jclouds.vcloud.domain.ovf.ResourceAllocation;
import org.jclouds.vcloud.domain.ovf.ResourceType;
import org.jclouds.vcloud.domain.ovf.VCloudNetworkAdapter;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

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

   public static CIMOperatingSystem toComputeOs(OvfEnvelope ovf) {
      return toComputeOs(ovf.getVirtualSystem().getOperatingSystem());
   }

   public static CIMOperatingSystem toComputeOs(Vm vm) {
      return toComputeOs(vm.getOperatingSystemSection());
   }

   public static CIMOperatingSystem toComputeOs(org.jclouds.vcloud.domain.ovf.OperatingSystemSection os) {
      return new CIMOperatingSystem(CIMOperatingSystem.OSType.fromValue(os.getId()), "", null, os.getDescription());
   }

   public static Credentials getCredentialsFrom(VApp vApp) {
      return vApp.getChildren().size() > 0 ? getCredentialsFrom(Iterables.get(vApp.getChildren(), 0)) : null;
   }

   public static Credentials getCredentialsFrom(VAppTemplate vApp) {
      return vApp.getChildren().size() > 0 ? getCredentialsFrom(Iterables.get(vApp.getChildren(), 0)) : null;
   }

   public static Credentials getCredentialsFrom(Vm vm) {
      String user = "root";
      if (vm.getOperatingSystemSection() != null && vm.getOperatingSystemSection().getDescription() != null
               && vm.getOperatingSystemSection().getDescription().indexOf("Windows") >= 0)
         user = "Administrator";
      String password = null;
      if (vm.getGuestCustomizationSection() != null)
         password = vm.getGuestCustomizationSection().getAdminPassword();
      return new Credentials(user, password);
   }

   public static Set<String> getPublicIpsFromVApp(VApp vApp) {
      Set<String> ips = Sets.newLinkedHashSet();
      // TODO make this work with composite vApps
      if (vApp.getChildren().size() == 0)
         return ips;
      Vm vm = Iterables.get(vApp.getChildren(), 0);
      // TODO: figure out how to differentiate public from private ip addresses
      // assumption is that we'll do this from the network object, which may have
      // enough data to tell whether or not it is a public network without string
      // parsing.  At worst, we could have properties set per cloud provider to
      // declare the networks which are public, then check against these in 
      // networkconnection.getNetwork
      if (vm.getNetworkConnectionSection() != null) {
         for (NetworkConnection connection : vm.getNetworkConnectionSection().getConnections())
            ips.add(connection.getIpAddress());
      } else {
         for (ResourceAllocation net : filter(vm.getVirtualHardwareSection().getResourceAllocations(),
                  resourceType(ResourceType.ETHERNET_ADAPTER))) {
            if (net instanceof VCloudNetworkAdapter) {
               VCloudNetworkAdapter vNet = VCloudNetworkAdapter.class.cast(net);
               ips.add(vNet.getIpAddress());
            }
         }
      }
      return ips;
   }

   public static Set<String> getPrivateIpsFromVApp(VApp vApp) {
      return Sets.newLinkedHashSet();
   }
}
