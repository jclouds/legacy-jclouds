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

package org.jclouds.vcloud.hostingdotcom;

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.hostingdotcom.domain.HostingDotComVApp;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx"
 *      />
 * @author Adrian Cole
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface HostingDotComVCloudClient extends VCloudClient {

   @Override
   @Timeout(duration = 600, timeUnit = TimeUnit.SECONDS)
   HostingDotComVApp instantiateVAppTemplateInOrg(String org, String vDC, String appName, String templateId,
         InstantiateVAppTemplateOptions... options);

   @Override
   @Timeout(duration = 600, timeUnit = TimeUnit.SECONDS)
   HostingDotComVApp instantiateVAppTemplateInVDC(String vDC, String appName, String templateId,
         InstantiateVAppTemplateOptions... options);

   @Override
   @Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
   HostingDotComVApp getVApp(String vAppId);

}
