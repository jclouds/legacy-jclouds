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

package org.jclouds.vcloud.terremark;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.jclouds.concurrent.Timeout;
import org.jclouds.vcloud.terremark.domain.PublicIpAddress;
import org.jclouds.vcloud.terremark.domain.TerremarkNetwork;
import org.jclouds.vcloud.terremark.domain.TerremarkOrgNetwork;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href= "http://support.theenterprisecloud.com/kb/default.asp?id=645&Lang=1&SID=" />
 * @author Adrian Cole
 */
@Timeout(duration = 300, timeUnit = TimeUnit.SECONDS)
public interface TerremarkECloudClient extends TerremarkVCloudClient {

   /**
    * Allocate a new public IP
    * 
    * @param vDCId
    * @return
    * @throws org.jclouds.rest.InsufficientResourcesException
    *            if there's no additional ips available
    */
   PublicIpAddress activatePublicIpInVDC(URI vDCId);

   TerremarkOrgNetwork findNetworkInOrgVDCNamed(@Nullable String orgName, @Nullable String vdc, String networkName);

   TerremarkOrgNetwork getNetwork(URI network);

   TerremarkNetwork getTerremarkNetwork(URI network);

}
