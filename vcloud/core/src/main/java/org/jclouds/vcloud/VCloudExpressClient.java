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
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

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
public interface VCloudExpressClient {

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
    * @throws NoSuchElementException
    *            if you specified an org name that isn't present
    */
   Organization findOrganizationNamed(@Nullable String name);

   /**
    * Please use #findCatalogInOrgNamed(null, null)
    */
   @Deprecated
   Catalog getDefaultCatalog();

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

   Network findNetworkInOrgVDCNamed(@Nullable String orgName, @Nullable String catalogName, String networkName);

   Network getNetwork(URI network);

   VDC getVDC(URI vdc);

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

   /**
    * Please use #findVDCInOrgNamed
    */
   @Deprecated
   VDC getDefaultVDC();

   TasksList getTasksList(URI tasksListId);

   TasksList findTasksListInOrgNamed(String orgName, String tasksListName);

   /**
    * Please use #getTasksListInOrg(null, null)
    */
   @Deprecated
   TasksList getDefaultTasksList();

   Task deployVApp(URI vAppId);

   void deleteVApp(URI vAppId);

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

   Task getTask(URI taskId);

   void cancelTask(URI taskId);

   VApp findVAppInOrgVDCNamed(@Nullable String orgName, @Nullable String catalogName, String vAppName);

   VApp getVApp(URI vApp);

   VApp instantiateVAppTemplateInVDC(URI vDC, URI template, String appName, InstantiateVAppTemplateOptions... options);

   Task cloneVAppInVDC(URI vDC, URI toClone, String newName, CloneVAppOptions... options);

}
