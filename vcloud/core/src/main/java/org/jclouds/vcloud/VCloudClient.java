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

package org.jclouds.vcloud;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.Network;
import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TasksList;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.options.CloneVAppOptions;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx"
 *      />
 * @author Adrian Cole
 */
@Timeout(duration = 300, timeUnit = TimeUnit.SECONDS)
public interface VCloudClient {

   /**
    * Please use {@link #findOrganizationNamed(String)} passing null
    */
   @Deprecated
   Organization getDefaultOrganization();

   Organization getOrganization(URI orgId);

   /**
    * This call returns a list of all vCloud Data Centers (vdcs), catalogs, and
    * task lists within the organization.
    * 
    * @param name
    *           organization name, or null for the default
    */
   Organization findOrganizationNamed(String name);

   /**
    * Please use #findCatalogInOrgNamed(null, null)
    */
   @Deprecated
   Catalog getDefaultCatalog();

   /**
    * Please use #findCatalogInOrgNamed
    */
   @Deprecated
   Catalog getCatalog(String catalogId);

   Catalog findCatalogInOrgNamed(String orgName, String catalogName);

   CatalogItem getCatalogItem(URI catalogItem);

   CatalogItem findCatalogItemInOrgCatalogNamed(String orgName, String catalogName, String itemName);

   VAppTemplate getVAppTemplate(URI vAppTemplate);

   VAppTemplate findVAppTemplateInOrgCatalogNamed(String orgName, String catalogName, String templateName);

   Network getNetwork(String networkId);

   VDC getVDC(URI vdc);

   VDC findVDCInOrgNamed(String orgName, String vdcName);

   /**
    * Please use #findVDCInOrgNamed
    */
   @Deprecated
   VDC getDefaultVDC();

   /**
    * Please use #findTasksListInOrgNamed
    */
   @Deprecated
   TasksList getTasksList(String tasksListId);

   TasksList findTasksListInOrgNamed(String orgName, String tasksListName);

   /**
    * Please use #getTasksListInOrg(null, null)
    */
   @Deprecated
   TasksList getDefaultTasksList();

   Task deployVApp(String vAppId);

   void deleteVApp(String vAppId);

   Task undeployVApp(String vAppId);

   /**
    * This call powers on the vApp, as specified in the vApp's ovf:Startup
    * element.
    */
   Task powerOnVApp(String vAppId);

   /**
    * This call powers off the vApp, as specified in the vApp's ovf:Startup
    * element.
    */
   Task powerOffVApp(String vAppId);

   /**
    * This call shuts down the vApp.
    */
   void shutdownVApp(String vAppId);

   /**
    * This call resets the vApp.
    */
   Task resetVApp(String vAppId);

   /**
    * This call suspends the vApp.
    */
   Task suspendVApp(String vAppId);

   Task getTask(String taskId);

   void cancelTask(String taskId);

   VApp getVApp(String appId);

   VApp instantiateVAppTemplateInVDC(URI vDC, URI template, String appName, InstantiateVAppTemplateOptions... options);

   Task cloneVAppInVDC(URI vDC, URI toClone, String newName, CloneVAppOptions... options);

}
