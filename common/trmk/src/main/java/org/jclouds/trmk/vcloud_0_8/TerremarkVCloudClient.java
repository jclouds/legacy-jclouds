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
package org.jclouds.trmk.vcloud_0_8;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.trmk.vcloud_0_8.domain.Catalog;
import org.jclouds.trmk.vcloud_0_8.domain.CatalogItem;
import org.jclouds.trmk.vcloud_0_8.domain.CustomizationParameters;
import org.jclouds.trmk.vcloud_0_8.domain.InternetService;
import org.jclouds.trmk.vcloud_0_8.domain.KeyPair;
import org.jclouds.trmk.vcloud_0_8.domain.Network;
import org.jclouds.trmk.vcloud_0_8.domain.Node;
import org.jclouds.trmk.vcloud_0_8.domain.Org;
import org.jclouds.trmk.vcloud_0_8.domain.Protocol;
import org.jclouds.trmk.vcloud_0_8.domain.PublicIpAddress;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.Task;
import org.jclouds.trmk.vcloud_0_8.domain.TasksList;
import org.jclouds.trmk.vcloud_0_8.domain.VApp;
import org.jclouds.trmk.vcloud_0_8.domain.VAppConfiguration;
import org.jclouds.trmk.vcloud_0_8.domain.VAppTemplate;
import org.jclouds.trmk.vcloud_0_8.domain.VDC;
import org.jclouds.trmk.vcloud_0_8.options.AddInternetServiceOptions;
import org.jclouds.trmk.vcloud_0_8.options.AddNodeOptions;
import org.jclouds.trmk.vcloud_0_8.options.CloneVAppOptions;
import org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href=
 *      "https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx"
 *      />
 * @author Adrian Cole
 */
public interface TerremarkVCloudClient {
   Catalog getCatalog(URI catalogId);

   /**
    * returns the catalog in the organization associated with the specified
    * name. Note that both parameters can be null to choose default.
    * 
    * @param orgName
    *           organization name, or null for the default
    * @param catalogName
    *           catalog name, or null for the default
    * @throws NoSuchElementException
    *            if you specified an org or catalog name that isn't present
    */
   Catalog findCatalogInOrgNamed(@Nullable String orgName, @Nullable String catalogName);

   CatalogItem getCatalogItem(URI catalogItem);

   /**
    * returns the catalog item in the catalog associated with the specified
    * name. Note that the org and catalog parameters can be null to choose
    * default.
    * 
    * @param orgName
    *           organization name, or null for the default
    * @param catalogName
    *           catalog name, or null for the default
    * @param itemName
    *           item you wish to lookup
    * 
    * @throws NoSuchElementException
    *            if you specified an org, catalog, or catalog item name that
    *            isn't present
    */
   CatalogItem findCatalogItemInOrgCatalogNamed(@Nullable String orgName, @Nullable String catalogName, String itemName);

   Network findNetworkInOrgVDCNamed(@Nullable String orgName, @Nullable String catalogName, String networkName);

   Network getNetwork(URI network);

   /**
    * returns the VDC in the organization associated with the specified name.
    * Note that both parameters can be null to choose default.
    * 
    * @param orgName
    *           organization name, or null for the default
    * @param vdcName
    *           catalog name, or null for the default
    * @throws NoSuchElementException
    *            if you specified an org or vdc name that isn't present
    */
   VDC findVDCInOrgNamed(String orgName, String vdcName);

   TasksList getTasksList(URI tasksListId);

   TasksList findTasksListInOrgNamed(String orgName, String tasksListName);

   /**
    * Whenever the result of a request cannot be returned immediately, the
    * server creates a Task object and includes it in the response, as a member
    * of the Tasks container in the response body. Each Task has an href value,
    * which is a URL that the client can use to retrieve the Task element alone,
    * without the rest of the response in which it was contained. All
    * information about the task is included in the Task element when it is
    * returned in the response's Tasks container, so a client does not need to
    * make an additional request to the Task URL unless it wants to follow the
    * progress of a task that was incomplete.
    */
   Task getTask(URI taskId);

   void cancelTask(URI taskId);

   /**
    * 
    * @return a listing of all orgs that the current user has access to.
    */
   Map<String, ReferenceType> listOrgs();

   VApp instantiateVAppTemplateInVDC(URI vDC, URI template, String appName, InstantiateVAppTemplateOptions... options);

   Task cloneVAppInVDC(URI vDC, URI toClone, String newName, CloneVAppOptions... options);

   VAppTemplate getVAppTemplate(URI vAppTemplate);

   /**
    * returns the vapp template corresponding to a catalog item in the catalog
    * associated with the specified name. Note that the org and catalog
    * parameters can be null to choose default.
    * 
    * @param orgName
    *           organization name, or null for the default
    * @param catalogName
    *           catalog name, or null for the default
    * @param itemName
    *           item you wish to lookup
    * 
    * @throws NoSuchElementException
    *            if you specified an org, catalog, or catalog item name that
    *            isn't present
    */
   VAppTemplate findVAppTemplateInOrgCatalogNamed(@Nullable String orgName, @Nullable String catalogName,
         String itemName);

   VApp findVAppInOrgVDCNamed(@Nullable String orgName, @Nullable String catalogName, String vAppName);

   VApp getVApp(URI vApp);

   Task deployVApp(URI vAppId);

   /**
    * 
    */
   Task undeployVApp(URI vAppId);

   /**
    * This call powers on the vApp, as specified in the vApp's ovf:Startup
    * element.
    */
   Task powerOnVApp(URI vAppId);

   /**
    * This call powers off the vApp, as specified in the vApp's ovf:Startup
    * element.
    */
   Task powerOffVApp(URI vAppId);

   /**
    * This call shuts down the vApp.
    */
   void shutdownVApp(URI vAppId);

   /**
    * This call resets the vApp.
    */
   Task resetVApp(URI vAppId);

   /**
    * This call suspends the vApp.
    */
   Task suspendVApp(URI vAppId);

   Task deleteVApp(URI vAppId);

   /**
    * {@inheritDoc}
    */
   VDC getVDC(URI catalogItem);

   Org getOrg(URI orgId);

   Org findOrgNamed(String orgName);

   CustomizationParameters getCustomizationOptions(URI customizationOptions);

   /**
    * This call returns a list of public IP addresses.
    */
   Set<PublicIpAddress> getPublicIpsAssociatedWithVDC(URI vDCId);

   void deletePublicIp(URI ipId);

   /**
    * This call adds an internet service to a known, existing public IP. This
    * call is identical to Add Internet Service except you specify the public IP
    * in the request.
    * 
    */
   InternetService addInternetServiceToExistingIp(URI existingIpId, String serviceName, Protocol protocol, int port,
         AddInternetServiceOptions... options);

   void deleteInternetService(URI internetServiceId);

   InternetService getInternetService(URI internetServiceId);

   Set<InternetService> getAllInternetServicesInVDC(URI vDCId);

   /**
    * This call returns information about the internet service on a public IP.
    */
   Set<InternetService> getInternetServicesOnPublicIp(URI ipId);

   Set<InternetService> getPublicIp(URI ipId);

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
   Node addNode(URI internetServiceId, String ipAddress, String name, int port, AddNodeOptions... options);

   Node getNode(URI nodeId);

   Node configureNode(URI nodeId, String name, boolean enabled, @Nullable String description);

   void deleteNode(URI nodeId);

   Set<Node> getNodes(URI internetServiceId);

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
    * @param VApp
    *           vApp to change in power state off
    * @param configuration
    *           (s) to change
    * @return task of configuration change
    */
   Task configureVApp(VApp vApp, VAppConfiguration configuration);

   /**
    */
   Set<KeyPair> listKeyPairsInOrg(URI org);

   /**
    * @throws IllegalStateException
    *            if a key of the same name already exists
    */
   KeyPair generateKeyPairInOrg(URI org, String name, boolean makeDefault);

   /**
    */
   KeyPair findKeyPairInOrg(URI org, String keyPairName);

   KeyPair getKeyPair(URI keyPair);

   // TODO
   // KeyPair configureKeyPair(int keyPairId, KeyPairConfiguration
   // keyPairConfiguration);

   void deleteKeyPair(URI keyPair);

}
