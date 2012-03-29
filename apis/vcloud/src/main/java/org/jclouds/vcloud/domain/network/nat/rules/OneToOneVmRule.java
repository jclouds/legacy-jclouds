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
package org.jclouds.vcloud.domain.network.nat.rules;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.domain.network.nat.NatRule;

/**
 * The OneToOneVmRule element describes a NAT rule that specifies network address translation
 * details for a single virtual machine. The external IP address can be specified manually or
 * assigned automatically at deployment time. The internal IP address is discovered by looking up
 * the specified VmReference and NIC ID.
 * 
 * @since vcloud 0.9
 * @author Adrian Cole
 */
public class OneToOneVmRule implements NatRule {
   private final MappingMode mappingMode;
   @Nullable
   private final String externalIP;
   @Nullable
   private final String vAppScopedVmId;
   private final int vmNicId;

   public OneToOneVmRule(MappingMode mappingMode, @Nullable String externalIp, @Nullable String vAppScopedVmId,
            int vmNicId) {
      this.mappingMode = checkNotNull(mappingMode, "mappingMode");
      this.externalIP = externalIp;
      this.vAppScopedVmId = vAppScopedVmId;
      this.vmNicId = vmNicId;
   }

   /**
    * @return how IP address mapping is implemented by the NAT service
    * @since vcloud 0.9
    */
   public MappingMode getMappingMode() {
      return mappingMode;
   }

   /**
    * @return if MappingMode is manual, specifies the external IP address of this Vm, otherwise
    *         null.
    * @since vcloud 0.9
    */
   @Nullable
   @Override
   public String getExternalIP() {
      return externalIP;
   }

   /**
    * @return read‐only identifier created on import
    * @since vcloud 0.9
    */
   @Nullable
   public String getVAppScopedVmId() {
      return vAppScopedVmId;
   }

   /**
    * @return device number of the NIC on the referenced virtual machine
    * @since vcloud 0.9
    */
   public int getVmNicId() {
      return vmNicId;
   }

}