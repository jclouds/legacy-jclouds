/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.jclouds.concurrent.Timeout;
import org.jclouds.ovf.Envelope;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.GuestCustomizationSection;
import org.jclouds.vcloud.domain.NetworkConnectionSection;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TasksList;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.domain.Vm;
import org.jclouds.vcloud.domain.network.OrgNetwork;
import org.jclouds.vcloud.features.CatalogClient;
import org.jclouds.vcloud.features.NetworkClient;
import org.jclouds.vcloud.features.OrgClient;
import org.jclouds.vcloud.features.TaskClient;
import org.jclouds.vcloud.features.VAppClient;
import org.jclouds.vcloud.features.VAppTemplateClient;
import org.jclouds.vcloud.features.VDCClient;
import org.jclouds.vcloud.features.VmClient;
import org.jclouds.vcloud.options.CaptureVAppOptions;
import org.jclouds.vcloud.options.CloneVAppOptions;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="http://communities.vmware.com/community/developer/forums/vcloudapi" />
 * @author Adrian Cole
 */
@Timeout(duration = 300, timeUnit = TimeUnit.SECONDS)
public interface VCloudClient {
   /**
    * Provides asynchronous access to VApp Template features.
    * 
    */
   @Delegate
   VAppTemplateClient getVAppTemplateClient();

   /**
    * Provides synchronous access to VApp features.
    */
   @Delegate
   VAppClient getVAppClient();

   /**
    * Provides synchronous access to Vm features.
    */
   @Delegate
   VmClient getVmClient();

   /**
    * Provides synchronous access to Catalog features.
    */
   @Delegate
   CatalogClient getCatalogClient();

   /**
    * Provides synchronous access to Task features.
    */
   @Delegate
   TaskClient getTaskClient();

   /**
    * Provides synchronous access to VDC features.
    */
   @Delegate
   VDCClient getVDCClient();

   /**
    * Provides synchronous access to Network features.
    */
   @Delegate
   NetworkClient getNetworkClient();

   /**
    * Provides synchronous access to Org features.
    */
   @Delegate
   OrgClient getOrgClient();

   /**
    * @see VmClient#getThumbnail
    */
   @Deprecated
   InputStream getThumbnailOfVm(URI vm);

   /**
    * @see OrgClient#listOrgs
    */
   @Deprecated
   Map<String, ReferenceType> listOrgs();

   /**
    * @see VAppTemplateClient#createVAppInVDCByInstantiatingTemplate
    */
   @Deprecated
   VApp instantiateVAppTemplateInVDC(URI vDC, URI template, String appName, InstantiateVAppTemplateOptions... options);

   /**
    * @see VAppClient#copyVAppToVDCAndName
    */
   @Deprecated
   Task cloneVAppInVDC(URI vDC, URI toClone, String newName, CloneVAppOptions... options);

   /**
    * @see VAppClient#captureAsTemplateInVDC
    */
   @Deprecated
   VAppTemplate captureVAppInVDC(URI vDC, URI toClone, String templateName, CaptureVAppOptions... options);

   /**
    * @see VAppTemplateClient#get
    */
   @Deprecated
   VAppTemplate getVAppTemplate(URI vAppTemplate);

   /**
    * @see VAppTemplateClient#getOvfEnvelope
    */
   @Deprecated
   Envelope getOvfEnvelopeForVAppTemplate(URI vAppTemplate);

   /**
    * @see VmClient#updateGuestCustomization
    */
   @Deprecated
   Task updateGuestCustomizationOfVm(URI vm, GuestCustomizationSection guestCustomizationSection);

   /**
    * @see VmClient#updateNetworkConnection
    */
   @Deprecated
   Task updateNetworkConnectionOfVm(URI vm, NetworkConnectionSection guestCustomizationSection);

   /**
    * @see VAppTemplateClient#findInOrgCatalogNamed
    */
   @Deprecated
   VAppTemplate findVAppTemplateInOrgCatalogNamed(@Nullable String orgName, @Nullable String catalogName,
            String itemName);

   /**
    * @see VAppClient#findInOrgVDCNamed
    */
   @Deprecated
   VApp findVAppInOrgVDCNamed(@Nullable String orgName, @Nullable String catalogName, String vAppName);

   /**
    * @see VAppClient#get
    */
   @Deprecated
   VApp getVApp(URI vApp);

   /**
    * @see VmClient#get
    */
   @Deprecated
   Vm getVm(URI vm);

   /**
    * 
    * @see VAppClient#deploy
    * @see VmClient#deploy
    */
   @Deprecated
   Task deployVAppOrVm(URI vAppOrVmId);

   /**
    * 
    * @see VAppClient#deployAndPowerOn
    * @see VmClient#deployAndPowerOn
    */
   @Deprecated
   Task deployAndPowerOnVAppOrVm(URI vAppOrVmId);

   /**
    * 
    * @see VAppClient#undeploy
    * @see VmClient#undeploy
    */
   @Deprecated
   Task undeployVAppOrVm(URI vAppOrVmId);

   /**
    * 
    * @see VAppClient#undeployAndSaveState
    * @see VmClient#undeployAndSaveState
    */
   @Deprecated
   Task undeployAndSaveStateOfVAppOrVm(URI vAppOrVmId);

   /**
    * 
    * @see VAppClient#powerOn
    * @see VmClient#powerOn
    */
   @Deprecated
   Task powerOnVAppOrVm(URI vAppOrVmId);

   /**
    * 
    * @see VAppClient#powerOff
    * @see VmClient#powerOff
    */
   @Deprecated
   Task powerOffVAppOrVm(URI vAppOrVmId);

   /**
    * 
    * @see VAppClient#shutdown
    * @see VmClient#shutdown
    */
   @Deprecated
   void shutdownVAppOrVm(URI vAppOrVmId);

   /**
    * 
    * @see VAppClient#reset
    * @see VmClient#reset
    */
   @Deprecated
   Task resetVAppOrVm(URI vAppOrVmId);

   /**
    * 
    * @see VAppClient#reboot
    * @see VmClient#reboot
    */
   @Deprecated
   void rebootVAppOrVm(URI vAppOrVmId);

   /**
    * 
    * @see VAppClient#suspend
    * @see VmClient#suspend
    */
   @Deprecated
   Task suspendVAppOrVm(URI vAppOrVmId);

   /**
    * 
    * @see VAppClient#delete
    */
   @Deprecated
   Task deleteVApp(URI vAppId);

   /**
    * 
    * @see CatalogClient#getCatalog
    */
   @Deprecated
   Catalog getCatalog(URI catalogId);

   /**
    * 
    * @see CatalogClient#getCatalogItem
    */
   @Deprecated
   Catalog findCatalogInOrgNamed(@Nullable String orgName, @Nullable String catalogName);

   /**
    * 
    * @see CatalogClient#getCatalogItem
    */
   @Deprecated
   CatalogItem getCatalogItem(URI catalogItem);

   /**
    * 
    * @see CatalogClient#findCatalogItemInOrgCatalogNamed
    */
   @Deprecated
   CatalogItem findCatalogItemInOrgCatalogNamed(@Nullable String orgName, @Nullable String catalogName, String itemName);

   /**
    * 
    * @see TaskClient#getTasksList
    */
   @Deprecated
   TasksList getTasksList(URI tasksListId);

   /**
    * 
    * @see TaskClient#findTasksListInOrgNamed
    */
   @Deprecated
   TasksList findTasksListInOrgNamed(String orgName);

   /**
    * 
    * @see TaskClient#getTask
    */
   @Deprecated
   Task getTask(URI taskId);

   /**
    * 
    * @see TaskClient#cancelTask
    */
   @Deprecated
   void cancelTask(URI taskId);

   /**
    * 
    * @see VDCClient#getVDC
    */
   @Deprecated
   VDC getVDC(URI vdc);

   /**
    * 
    * @see VDCClient#findVDCInOrgNamed
    */
   @Deprecated
   VDC findVDCInOrgNamed(String orgName, String vdcName);

   /**
    * 
    * @see NetworkClient#findNetworkInOrgVDCNamed
    */
   @Deprecated
   OrgNetwork findNetworkInOrgVDCNamed(@Nullable String orgName, @Nullable String catalogName, String networkName);

   /**
    * 
    * @see NetworkClient#getNetwork
    */
   @Deprecated
   OrgNetwork getNetwork(URI network);

   /**
    * 
    * @see OrgClient#getOrg
    */
   @Deprecated
   Org getOrg(URI orgId);

   /**
    * 
    * @see OrgClient#findOrgNamed
    */
   @Deprecated
   Org findOrgNamed(@Nullable String name);
}
