/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.ecloud;

import java.net.URI;
import java.util.Set;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.trmk.ecloud.domain.ECloudOrg;
import org.jclouds.trmk.ecloud.features.DataCenterOperationsClient;
import org.jclouds.trmk.ecloud.features.TagOperationsClient;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudClient;
import org.jclouds.trmk.vcloud_0_8.domain.IpAddress;
import org.jclouds.trmk.vcloud_0_8.domain.Network;
import org.jclouds.trmk.vcloud_0_8.domain.NetworkExtendedInfo;
import org.jclouds.trmk.vcloud_0_8.domain.PublicIpAddress;
import org.jclouds.trmk.vcloud_0_8.domain.VAppExtendedInfo;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href=
 *      "http://support.theenterprisecloud.com/kb/default.asp?id=645&Lang=1&SID="
 *      />
 * @author Adrian Cole
 */
public interface TerremarkECloudClient extends TerremarkVCloudClient {
   /**
    * Provides synchronous access to Data Center Operations.
    * 
    */
   @Delegate
   DataCenterOperationsClient getDataCenterOperationsClient();

   /**
    * Provides synchronous access to Data Center Operations.
    * 
    */
   @Delegate
   TagOperationsClient getTagOperationsClient();

   /**
    * {@inheritDoc}
    */
   @Override
   ECloudOrg getOrg(URI orgId);

   /**
    * Allocate a new public IP
    * 
    * @param vDCId
    * @return
    * @throws org.jclouds.rest.InsufficientResourcesException
    *            if there's no additional ips available
    */
   PublicIpAddress activatePublicIpInVDC(URI vDCId);

   Network findNetworkInOrgVDCNamed(@Nullable String orgName, @Nullable String vdc, String networkName);

   Network getNetwork(URI network);

   NetworkExtendedInfo getNetworkExtendedInfo(URI network);

   Set<IpAddress> getIpAddresses(URI network);

   /**
    * Returns extended information for the vApp.
    * 
    * @param vApp
    *           The URI at which the vApp information is available.
    * @return Extended vApp information like tags, long name, network adapter
    *         information.
    */
   VAppExtendedInfo getVAppExtendedInfo(URI href);
}
