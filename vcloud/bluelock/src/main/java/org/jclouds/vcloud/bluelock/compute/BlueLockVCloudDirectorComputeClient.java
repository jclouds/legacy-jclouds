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

package org.jclouds.vcloud.bluelock.compute;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.compute.internal.VCloudComputeClientImpl;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppTemplate;

import com.google.common.base.Predicate;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlueLockVCloudDirectorComputeClient extends VCloudComputeClientImpl {
   private final PopulateDefaultLoginCredentialsForImageStrategy credentialsProvider;

   @Inject
   protected BlueLockVCloudDirectorComputeClient(PopulateDefaultLoginCredentialsForImageStrategy credentialsProvider,
            VCloudClient client, Predicate<URI> successTester, Map<Status, NodeState> vAppStatusToNodeState) {
      super(client, successTester, vAppStatusToNodeState);
      this.credentialsProvider = credentialsProvider;
   }

   @Override
   protected Map<String, String> parseAndValidateResponse(VAppTemplate template, VApp vAppResponse) {
      Credentials credentials = credentialsProvider.execute(template);
      Map<String, String> toReturn = super.parseResponse(template, vAppResponse);
      toReturn.put("username", credentials.identity);
      toReturn.put("password", credentials.credential);
      return toReturn;
   }

}