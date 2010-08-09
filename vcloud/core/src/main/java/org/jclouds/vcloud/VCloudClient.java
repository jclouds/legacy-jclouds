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
    * Please use {@link #getOrganizationNamed(String)} passing null
    */
   @Deprecated
   Organization getDefaultOrganization();

   /**
    * Please use #getOrganizationByName
    */
   @Deprecated
   Organization getOrganization(String orgId);

   /**
    * This call returns a list of all vCloud Data Centers (vdcs), catalogs, and
    * task lists within the organization.
    * 
    * @param name
    *           organization name, or null for the default
    */
   Organization getOrganizationNamed(String name);

   Catalog getDefaultCatalog();

   Catalog getCatalog(String catalogId);

   CatalogItem getCatalogItem(String catalogItemId);

   VAppTemplate getVAppTemplate(String vAppTemplateId);

   Network getNetwork(String networkId);

   /**
    * please use {@link #getVDC(URI)}
    */
   @Deprecated
   VDC getVDC(String vDCId);

   VDC getVDCInOrg(String orgName, String vdcName);

   VDC getVDC(URI vdc);

   VDC getDefaultVDC();

   TasksList getTasksList(String tasksListId);

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

   @Deprecated
   VApp instantiateVAppTemplateInVDC(String vDCId, String appName, String templateId,
         InstantiateVAppTemplateOptions... options);

   VApp instantiateVAppTemplateInOrg(String org, String vdc, String appName, String templateId,
         InstantiateVAppTemplateOptions... options);

   @Deprecated
   Task cloneVAppInVDC(String vDCId, String vAppIdToClone, String newName, CloneVAppOptions... options);

   Task cloneVAppInOrg(String org, String vdc, String vAppIdToClone, String newName, CloneVAppOptions... options);

}
