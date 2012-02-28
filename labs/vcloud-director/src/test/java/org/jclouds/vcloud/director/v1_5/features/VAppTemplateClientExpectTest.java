/*
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
package org.jclouds.vcloud.director.v1_5.features;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.*;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import org.jclouds.vcloud.director.v1_5.domain.*;
import org.jclouds.vcloud.director.v1_5.domain.ovf.SectionType;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorRestClientExpectTest;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

/**
 * Tests the request/response behavior of {@link org.jclouds.vcloud.director.v1_5.features.VAppTemplateClient}
 *
 * @author Adam Lowe
 */
@Test(groups = {"unit", "user"}, singleThreaded = true, testName = "VAppTemplateClientExpectTest")
public class VAppTemplateClientExpectTest extends BaseVCloudDirectorRestClientExpectTest {

   public void testGetVAppTemplate() {
      final String templateId = "/vAppTemplate/vAppTemplate/vappTemplate-xxxx-xxxx-xxxx-xxx";
      Reference vappTemplateRef = Reference.builder().href(URI.create(endpoint + templateId)).build();

      VAppTemplateClient client = requestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId).acceptMedia(VAPP_TEMPLATE).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/vAppTemplate.xml", VAPP_TEMPLATE).httpResponseBuilder().build()
      ).getVAppTemplateClient();

      assertNotNull(client);
      VAppTemplate template = client.getVAppTemplate(vappTemplateRef);
      assertEquals(template, exampleTemplate());
   }

   private VAppTemplate exampleTemplate() {
      Link aLink = Link.builder().href(URI.create("https://vcloudbeta.bluelock.com/api/vdc/d16d333b-e3c0-4176-845d-a5ee6392df07"))
            .type("application/vnd.vmware.vcloud.vdc+xml").rel("up").build();
      Link bLink = Link.builder().href(URI.create("https://vcloudbeta.bluelock.com/api/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9"))
            .rel("remove").build();
      
      Owner owner = Owner.builder().user(Reference.builder().href(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/967d317c-4273-4a95-b8a4-bf63b78e9c69")).name("x@jclouds.org").type("application/vnd.vmware.admin.user+xml").build()).build();

      LeaseSettingsSection leaseSettings = LeaseSettingsSection.builder().type("application/vnd.vmware.vcloud.leaseSettingsSection+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9/leaseSettingsSection/"))
            .info("Lease settings section")
            .links(ImmutableList.of(Link.builder().rel("edit").type("application/vnd.vmware.vcloud.leaseSettingsSection+xml")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9/leaseSettingsSection/")).build()))
            .storageLeaseInSeconds(0)
            .build();
      CustomizationSection customization = CustomizationSection.builder()
            .type("application/vnd.vmware.vcloud.customizationSection+xml")
            .customizeOnInstantiate(true)
            .href(URI.create("https://vcloudbeta.bluelock.com/api/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9/customizationSection/"))
            .build();
            
      return VAppTemplate.builder().href(URI.create("https://vcloudbeta.bluelock.com/api/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9"))
            .links(ImmutableSet.of(aLink, bLink))
            .children(VAppTemplateChildren.builder().build())
            .type("application/vnd.vmware.vcloud.vAppTemplate+xml")
            .description("For testing")
            .id("urn:vcloud:vapptemplate:ef4415e6-d413-4cbb-9262-f9bbec5f2ea9")
            .name("ubuntu10")
            .sections(ImmutableList.<SectionType>of(leaseSettings, customization))
            .status(-1)
            .owner(owner)
            .ovfDescriptorUploaded(true)
            .goldMaster(false)
            .build();
   }
}
