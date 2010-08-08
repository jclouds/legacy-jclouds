/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.terremark;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.terremark.domain.CustomizationParameters;
import org.jclouds.vcloud.terremark.domain.InternetService;
import org.jclouds.vcloud.terremark.domain.Node;
import org.jclouds.vcloud.terremark.domain.NodeConfiguration;
import org.jclouds.vcloud.terremark.domain.Protocol;
import org.jclouds.vcloud.terremark.domain.PublicIpAddress;
import org.jclouds.vcloud.terremark.domain.TerremarkOrganization;
import org.jclouds.vcloud.terremark.domain.VAppConfiguration;
import org.jclouds.vcloud.terremark.options.AddInternetServiceOptions;
import org.jclouds.vcloud.terremark.options.AddNodeOptions;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx"
 *      />
 * @author Adrian Cole
 */
@Timeout(duration = 300, timeUnit = TimeUnit.SECONDS)
public interface TerremarkVCloudClient extends VCloudClient {
   @Override
   TerremarkOrganization getDefaultOrganization();

   @Override
   TerremarkOrganization getOrganization(String orgId);

   @Override
   TerremarkOrganization getOrganizationNamed(String orgName);

   CustomizationParameters getCustomizationOptionsOfCatalogItem(String catalogItemId);

   /**
    * This call returns a list of public IP addresses.
    */
   Set<PublicIpAddress> getPublicIpsAssociatedWithVDC(String vDCId);

   void deletePublicIp(int ipId);

   /**
    * Allocate a new public IP
    * 
    * @param vDCId
    * @return
    */
   PublicIpAddress activatePublicIpInVDC(String vDCId);

   /**
    * The call creates a new internet server, including protocol and port
    * information. The public IP is dynamically allocated.
    * 
    */
   InternetService addInternetServiceToVDC(String vDCId, String serviceName, Protocol protocol, int port,
         AddInternetServiceOptions... options);

   /**
    * This call adds an internet service to a known, existing public IP. This
    * call is identical to Add Internet Service except you specify the public IP
    * in the request.
    * 
    */
   InternetService addInternetServiceToExistingIp(int existingIpId, String serviceName, Protocol protocol, int port,
         AddInternetServiceOptions... options);

   void deleteInternetService(int internetServiceId);

   InternetService getInternetService(int internetServiceId);

   Set<InternetService> getAllInternetServicesInVDC(String vDCId);

   /**
    * This call returns information about the internet service on a public IP.
    */
   Set<InternetService> getInternetServicesOnPublicIp(int ipId);

   Set<InternetService> getPublicIp(int ipId);

   /**
    * This call adds a node to an existing internet service.
    * <p/>
    * Every vDC is assigned a network of 60 IP addresses that can be used as
    * nodes. Each node can associated with multiple internet service. You can
    * get a list of the available IP addresses by calling Get IP Addresses for a
    * Network.
    * 
    * @param internetServiceId
    * @param ipAddress
    * @param name
    * @param port
    * @param options
    * @return
    */
   Node addNode(int internetServiceId, String ipAddress, String name, int port, AddNodeOptions... options);

   Node getNode(int nodeId);

   Node configureNode(int nodeId, NodeConfiguration nodeConfiguration);

   void deleteNode(int nodeId);

   Set<Node> getNodes(int internetServiceId);

   /**
    * This call configures the settings of an existing vApp by passing the new
    * configuration. The existing vApp must be in a powered off state (status =
    * 2).
    * <p/>
    * You can change the following items for a vApp.
    * <ol>
    * <li>vApp name Number of virtual CPUs</li>
    * <li>Amount of virtual memory</li>
    * <li>Add a virtual disk</li>
    * <li>Delete a virtual disk</li>
    * </ol>
    * You can make more than one change in a single request. For example, you
    * can increase the number of virtual CPUs and the amount of virtual memory
    * in the same request.
    * 
    * @param vApp
    *           vApp to change in power state off
    * @param configuration
    *           (s) to change
    * @return task of configuration change
    */
   Task configureVApp(VApp vApp, VAppConfiguration configuration);

}