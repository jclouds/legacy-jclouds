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

package org.jclouds.vcloud.hostingdotcom.compute;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeState;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.compute.BaseVCloudComputeClient;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.hostingdotcom.domain.HostingDotComVApp;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Singleton
public class HostingDotComVCloudComputeClient extends BaseVCloudComputeClient {

   @Inject
   protected HostingDotComVCloudComputeClient(VCloudClient client, Predicate<URI> successTester,
         Map<VAppStatus, NodeState> vAppStatusToNodeState) {
      super(client, successTester, vAppStatusToNodeState);
   }

   @Override
   protected Map<String, String> parseResponse(VAppTemplate template, VApp vAppResponse) {
      HostingDotComVApp hVApp = HostingDotComVApp.class.cast(vAppResponse);
      return ImmutableMap.<String, String> of("id", vAppResponse.getId().toASCIIString(), "username", hVApp
            .getUsername(), "password", hVApp.getPassword());
   }

}