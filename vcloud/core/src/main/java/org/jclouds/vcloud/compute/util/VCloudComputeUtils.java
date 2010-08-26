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

import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.os.CIMOperatingSystem;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.Vm;
import org.jclouds.vcloud.domain.ovf.OvfEnvelope;

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
      return toComputeOs(Iterables.get(vApp.getChildren(), 0));
   }

   public static CIMOperatingSystem toComputeOs(OvfEnvelope ovf) {
      return toComputeOs(ovf.getVirtualSystem().getOperatingSystem());
   }

   public static CIMOperatingSystem toComputeOs(Vm vm) {
      return toComputeOs(vm.getOperatingSystem());
   }

   public static CIMOperatingSystem toComputeOs(org.jclouds.vcloud.domain.ovf.OperatingSystemSection os) {
      return new CIMOperatingSystem(CIMOperatingSystem.OSType.fromValue(os.getId()), null, null, os.getDescription());
   }

}
