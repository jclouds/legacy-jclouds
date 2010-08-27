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
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VCloudExpressVApp;
import org.jclouds.vcloud.domain.VCloudExpressVAppTemplate;
import org.jclouds.vcloud.options.CloneVAppOptions;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx" />
 * @author Adrian Cole
 */
@Timeout(duration = 300, timeUnit = TimeUnit.SECONDS)
public interface VCloudExpressClient extends CommonVCloudClient {

   VCloudExpressVApp instantiateVAppTemplateInVDC(URI vDC, URI template, String appName,
            InstantiateVAppTemplateOptions... options);

   Task cloneVAppInVDC(URI vDC, URI toClone, String newName, CloneVAppOptions... options);

   VCloudExpressVAppTemplate getVAppTemplate(URI vAppTemplate);

   /**
    * returns the vapp template corresponding to a catalog item in the catalog associated with the
    * specified name. Note that the org and catalog parameters can be null to choose default.
    * 
    * @param orgName
    *           organization name, or null for the default
    * @param catalogName
    *           catalog name, or null for the default
    * @param itemName
    *           item you wish to lookup
    * 
    * @throws NoSuchElementException
    *            if you specified an org, catalog, or catalog item name that isn't present
    */
   VCloudExpressVAppTemplate findVAppTemplateInOrgCatalogNamed(@Nullable String orgName, @Nullable String catalogName,
            String itemName);

   VCloudExpressVApp findVAppInOrgVDCNamed(@Nullable String orgName, @Nullable String catalogName, String vAppName);

   VCloudExpressVApp getVApp(URI vApp);

   Task deployVApp(URI vAppId);

   /**
    * 
    */
   Task undeployVApp(URI vAppId);

   /**
    * This call powers on the vApp, as specified in the vApp's ovf:Startup element.
    */
   Task powerOnVApp(URI vAppId);

   /**
    * This call powers off the vApp, as specified in the vApp's ovf:Startup element.
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

   void deleteVApp(URI vAppId);

}
