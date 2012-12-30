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
import org.jclouds.fujitsu.fgcp.domain.PerformanceInfo;
import org.jclouds.fujitsu.fgcp.domain.VServer;
import org.jclouds.fujitsu.fgcp.domain.VServerStatus;
import org.jclouds.fujitsu.fgcp.domain.VServerWithDetails;

/**
 * API relating to virtual servers.
 * 
 * @author Dies Koper
 */
public interface VirtualServerApi {

   void start(String id);

   void stop(String id);

   void stopForcefully(String id);

   void destroy(String id);

   VServer get(String id);

   VServerWithDetails getDetails(String id);

   void update(String id, String name, String value);

   VServerStatus getStatus(String id);

   String getInitialPassword(String id);

   void attachDisk(String serverId, String diskId);

   Set<PerformanceInfo> getPerformanceInformation(String id, String interval);

   void registerAsPrivateDiskImage(String xml);
}
