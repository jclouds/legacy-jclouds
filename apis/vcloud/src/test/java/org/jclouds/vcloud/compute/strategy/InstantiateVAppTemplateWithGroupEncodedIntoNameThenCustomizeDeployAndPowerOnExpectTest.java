/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.compute.strategy;

import static org.testng.Assert.assertEquals;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.vcloud.compute.BaseVCloudComputeServiceExpectTest;
import org.jclouds.vcloud.domain.VApp;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 */
@Test(singleThreaded = true, testName = "InstantiateVAppTemplateWithGroupEncodedIntoNameThenCustomizeDeployAndPowerOnExpectTest")
public class InstantiateVAppTemplateWithGroupEncodedIntoNameThenCustomizeDeployAndPowerOnExpectTest extends
         BaseVCloudComputeServiceExpectTest {

   // TODO: finish me
   @Test(enabled = false)
   public void testCreateNodeUsingVCloud1_0ApiAgainstVCloudDirector1_5WhenVAppTemplateHasNetworkNamedNone()
            throws Exception {
      ComputeService compute = requestsSendResponses(ImmutableMap.<HttpRequest, HttpResponse> builder().put(
               versionsRequest, versionsResponseFromVCD1_5).put(version1_0LoginRequest,
               successfulVersion1_0LoginResponseFromVCD1_5WithSingleOrg).put(version1_0GetOrgRequest,
               successfulVersion1_0GetOrgResponseFromVCD1_5WithSingleTasksListVDCAndNetwork).put(
               version1_0GetCatalogRequest, successfulVersion1_0GetCatalogResponseFromVCD1_5WithSingleTemplate).put(
               version1_0GetCatalogItemRequest, successfulVersion1_0GetCatalogItemResponseFromVCD1_5ForTemplate).put(
               version1_0GetVDCRequest, successfulVersion1_0GetVDCResponseFromVCD1_5WithSingleTemplateAndNetwork).put(
               version1_0GetVAppTemplateRequest,
               successfulVersion1_0GetVAppTemplateResponseFromVCD1_5WithSingleVMAndVDCParent).put(
               version1_0GetOVFForVAppTemplateRequest,
               successfulVersion1_0GetOVFForVAppTemplateResponseFromVCD1_5WithSingleVM).build());

      InstantiateVAppTemplateWithGroupEncodedIntoNameThenCustomizeDeployAndPowerOn starter = compute.getContext()
               .utils().injector().getInstance(
                        InstantiateVAppTemplateWithGroupEncodedIntoNameThenCustomizeDeployAndPowerOn.class);

      String group = "group";
      String name = "group-abcd";

      NodeAndInitialCredentials<VApp> appAndCreds = starter.createNodeWithGroupEncodedIntoName(group, name, compute
               .templateBuilder().build());

      assertEquals(appAndCreds.getNode().getName(), name);
      assertEquals(appAndCreds.getCredentials(), LoginCredentials.builder().user("root").password("fromVApp").build());

   }
}
