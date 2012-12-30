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
import org.jclouds.fujitsu.fgcp.domain.BuiltinServerBackup;
import org.jclouds.fujitsu.fgcp.domain.BuiltinServerConfiguration;
import org.jclouds.fujitsu.fgcp.domain.BuiltinServerStatus;

/**
 * API relating to built-in servers, also called extended function module (EFM),
 * such as a firewall or load balancer (SLB).
 * 
 * @author Dies Koper
 */
public interface BuiltinServerApi {

   void start(String id);

   void stop(String id);

   void destroy(String id);

   void backup(String id);

   void restore(String id, String backupId);

   Set<BuiltinServerBackup> listBackups(String id);

   void destroyBackup(String id, String backupId);

   BuiltinServer get(String id);

   void update(String id, String name, String value);

   BuiltinServerStatus getStatus(String id);

   BuiltinServer getConfiguration(String id, BuiltinServerConfiguration configuration);

   //   BuiltinServer getConfiguration(String id, BuiltinServerConfiguration configuration, ConfigurationRequest request);
   // void updateConfiguration(String id, xml?);
   /*
getDNSConfiguration(String id)
getNATConfiguration(String id)
getPolicyConfiguration(String id)
getLBConfiguration(String id)

    *   UpdateEFMConfiguration
   BuiltinServer getConfiguration(String id, BuiltinServerConfiguration configuration);

    */
}
