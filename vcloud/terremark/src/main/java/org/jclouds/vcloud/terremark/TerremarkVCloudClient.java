/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.terremark;

import java.net.InetAddress;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.jclouds.vcloud.terremark.domain.InternetService;
import org.jclouds.vcloud.terremark.domain.Node;
import org.jclouds.vcloud.terremark.domain.TerremarkVApp;
import org.jclouds.vcloud.terremark.options.AddInternetServiceOptions;
import org.jclouds.vcloud.terremark.options.AddNodeOptions;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx" />
 * @author Adrian Cole
 */
@Timeout(duration = 45, timeUnit = TimeUnit.SECONDS)
public interface TerremarkVCloudClient extends VCloudClient {

   @Override
   TerremarkVApp instantiateVAppTemplate(String appName, String templateId,
            InstantiateVAppTemplateOptions... options);

   InternetService addInternetService(String serviceName, String protocol, int port,
            AddInternetServiceOptions... options);

   InternetService addInternetServiceToExistingIp(String existingIpId, String serviceName,
            String protocol, int port, AddInternetServiceOptions... options);

   void deleteInternetService(String internetServiceId);

   InternetService getInternetService(String internetServiceId);

   Node addNode(String internetServiceId, InetAddress ipAddress, String name, int port,
            AddNodeOptions... options);

   Node getNode(String nodeId);

   void deleteNode(String nodeId);

   @Override
   TerremarkVApp getVApp(String vAppId);

   SortedSet<InternetService> getAllInternetServices();

   SortedSet<Node> getNodes(String internetServiceId);

}
