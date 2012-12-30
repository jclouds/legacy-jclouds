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

import java.util.Set;
import org.jclouds.fujitsu.fgcp.domain.BuiltinServer;
import org.jclouds.fujitsu.fgcp.domain.PublicIP;
import org.jclouds.fujitsu.fgcp.domain.VDisk;
import org.jclouds.fujitsu.fgcp.domain.VServer;
import org.jclouds.fujitsu.fgcp.domain.VSystem;
import org.jclouds.fujitsu.fgcp.domain.VSystemStatus;
import org.jclouds.fujitsu.fgcp.domain.VSystemWithDetails;

/**
 * API relating to virtual systems.
 * 
 * @author Dies Koper
 */
public interface VirtualSystemApi {

   void destroy(String id);

   VSystemStatus getStatus(String id);

   VSystem get(String id);

   VSystemWithDetails getDetails(String id);

   void update(String id, String name, String value);

   void updateConfiguration(String id, String name, String value);

   String createServer(String name, String type, String diskImageId,
         String networkId);

   Set<VServer> listServers(String id);

   String createBuiltinServer(String name, String networkId);

   Set<BuiltinServer> listBuiltinServers(String id, String type);

   String createDisk(String id, String name, int size);

   Set<VDisk> listDisks(String id);

   void allocatePublicIP(String id);

   Set<PublicIP> listPublicIPs(String id);

   String standByConsole(String id, String networkId);

   void registerAsPrivateVSYSDescriptor(String id,
         String vsysDescriptorXMLFilePath);
}
