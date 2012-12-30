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
package org.jclouds.fujitsu.fgcp.services;

import java.util.Map;
import java.util.Set;
import org.jclouds.fujitsu.fgcp.domain.AddressRange;
import org.jclouds.fujitsu.fgcp.domain.DiskImage;
import org.jclouds.fujitsu.fgcp.domain.EventLog;
import org.jclouds.fujitsu.fgcp.domain.Information;
import org.jclouds.fujitsu.fgcp.domain.PublicIP;
import org.jclouds.fujitsu.fgcp.domain.ServerType;
import org.jclouds.fujitsu.fgcp.domain.UsageInfo;
import org.jclouds.fujitsu.fgcp.domain.VSystem;
import org.jclouds.fujitsu.fgcp.domain.VSystemDescriptor;
import org.jclouds.javax.annotation.Nullable;

/**
 * API relating to the virtual data center.
 * 
 * @see VirtualDCAsyncApi
 * @author Dies Koper
 */
public interface VirtualDCApi {

   String createVirtualSystem(String descriptorId, String name);

   Set<VSystem> listVirtualSystems();

   // according to the manual it takes a 'String diskImageId' but value seems
   // to be ignored
   Set<ServerType> listServerTypes();

   Set<DiskImage> listDiskImages();

   Set<DiskImage> listDiskImages(@Nullable String serverCategory, String vsysDescriptorId);

   Map<PublicIP, String> listPublicIPs();

   void addAddressRange(String pipFrom, String pipTo);

   void createAddressPool(String pipFrom, String pipTo);

   void deleteAddressRange(String pipFrom, String pipTo);

   Set<AddressRange> getAddressRange();

   Set<VSystemDescriptor> listVSYSDescriptor();

   Set<VSystemDescriptor> listVSYSDescriptor(String keyword, int estimateFrom,
         int estimateTo);

   Set<EventLog> getEventLogs(boolean all);

   Set<EventLog> getEventLogs();

   Set<Information> getInformation(boolean all);

   Set<Information> getInformation();

   Set<UsageInfo> getSystemUsage();

   Set<UsageInfo> getSystemUsage(String systemIds);
}
