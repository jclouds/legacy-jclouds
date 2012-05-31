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
package org.jclouds.vcloud.compute.functions;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Set;

import org.jclouds.cim.OSType;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.CIMOperatingSystem;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.location.suppliers.all.JustProvider;
import org.jclouds.vcloud.compute.BaseVCloudComputeServiceExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Test(singleThreaded = true, testName = "ListImagesInVCloudExpectTest")
public class ListImagesInVCloudExpectTest extends BaseVCloudComputeServiceExpectTest {

   public void testListImagesUsingVCloud1_0ApiOnServerRunningVCloudDirector1_5ReturnsImageWithLocationForVAppTemplateInVDC() throws Exception {
      ComputeService compute = requestsSendResponses(ImmutableMap.<HttpRequest, HttpResponse>builder()
               .put(versionsRequest, versionsResponseFromVCD1_5)
               .put(version1_0LoginRequest, successfulVersion1_0LoginResponseFromVCD1_5WithSingleOrg)
               .put(version1_0GetOrgRequest, successfulVersion1_0GetOrgResponseFromVCD1_5WithSingleTasksListVDCAndNetwork)
               .put(version1_0GetCatalogRequest, successfulVersion1_0GetCatalogResponseFromVCD1_5WithSingleTemplate)
               .put(version1_0GetCatalogItemRequest, successfulVersion1_0GetCatalogItemResponseFromVCD1_5ForTemplate)
               .put(version1_0GetVDCRequest, successfulVersion1_0GetVDCResponseFromVCD1_5WithSingleTemplateAndNetwork)
               .put(version1_0GetVAppTemplateRequest, successfulVersion1_0GetVAppTemplateResponseFromVCD1_5WithSingleVMAndVDCParent)
               .put(version1_0GetOVFForVAppTemplateRequest, successfulVersion1_0GetOVFForVAppTemplateResponseFromVCD1_5WithSingleVM)
               .build());

      Location provider = Iterables.getOnlyElement(compute.getContext().utils().injector().getInstance(JustProvider.class).get());

      Location orgLocation = new LocationBuilder().id(ENDPOINT + "/v1.0/org/" + orgId).scope(LocationScope.REGION)
               .description("jclouds").parent(provider).build();

      Location vdcLocation = new LocationBuilder().id(ENDPOINT + "/v1.0/vdc/" + vdcId).scope(LocationScope.ZONE)
               .description("orgVDC-jclouds-Tier1-PAYG").parent(orgLocation).build();

      Set<? extends Image> currentImages = compute.listImages();
      assertEquals(compute.listImages().size(), 1);
      Image onlyImage = Iterables.get(currentImages, 0);
      
      
      Image expectedImage = new ImageBuilder()
               .ids(ENDPOINT + "/v1.0/vAppTemplate/" + templateId)
               .uri(URI.create(ENDPOINT + "/v1.0/vAppTemplate/" + templateId))
               .name("UbuntuServer-x64-2GB")
               .operatingSystem(new CIMOperatingSystem(OSType.UBUNTU_64, "", null, "Ubuntu Linux (64-bit)"))
               // TODO: this looks like a bug, as it says network interfaces
               .description("This is a special place-holder used for disconnected network interfaces.")
               .defaultCredentials(LoginCredentials.builder().identity("root").build())
               .status(Image.Status.AVAILABLE)
               .location(vdcLocation).build();
      
      assertEquals(onlyImage, expectedImage);
      assertEquals(onlyImage.getStatus(), Image.Status.AVAILABLE);

   }
}
