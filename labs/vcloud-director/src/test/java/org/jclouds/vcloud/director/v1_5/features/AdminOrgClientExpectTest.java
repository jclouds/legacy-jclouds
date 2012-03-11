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

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorClient;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.OrgLeaseSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgVAppTemplateLeaseSettings;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorRestClientExpectTest;
import org.testng.annotations.Test;

/**
 * Test the {@link GroupClient} by observing its side effects.
 * 
 * @author danikov
 */
@Test(groups = { "unit", "user", "org"}, singleThreaded = true, testName = "AdminOrgClientExpectTest")
public class AdminOrgClientExpectTest extends BaseVCloudDirectorRestClientExpectTest {
   
   private Reference orgRef = Reference.builder()
         .type("application/vnd.vmware.admin.???+xml")
         .name("???")
         .href(URI.create(endpoint + "/admin/org/???"))
         .build();
   
// GET /admin/org/{id}
   
// POST /admin/org/{id}/catalogs
 
// POST /admin/org/{id}/groups
 
// GET /admin/org/{id}/settings
 
// PUT /admin/org/{id}/settings
 
// GET /admin/org/{id}/settings/email
 
// PUT /admin/org/{id}/settings/email
 
// GET /admin/org/{id}/settings/general
 
// PUT /admin/org/{id}/settings/general
 
// GET /admin/org/{id}/settings/ldap
 
// GET /admin/org/{id}/settings/passwordPolicy
 
// PUT /admin/org/{id}/settings/passwordPolicy
 
   @Test(enabled = false)
   public void testGetVAppLeaseSettings() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/admin/org/???/settings/vAppLeaseSettings")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/org/admin/vAppLeaseSettings.xml", 
                  VCloudDirectorMediaType.ORG_LEASE_SETTINGS)
            .httpResponseBuilder().build());

      OrgLeaseSettings expected = orgVAppLeaseSettings();

      assertEquals(client.getAdminOrgClient().getVAppLeaseSettings(orgRef.getURI()), expected);
   }
   
   public static final OrgLeaseSettings orgVAppLeaseSettings() {
      return OrgLeaseSettings.builder()
         
         .build();
   }
   
   @Test(enabled = false)
   public void testUpdateOrgVAppLeaseSettings() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", "/admin/org/???/settings/vAppLeaseSettings")
            .xmlFilePayload("/org/admin/updateVAppLeaseSettingsSource.xml", 
                  VCloudDirectorMediaType.ORG_LEASE_SETTINGS)
            .acceptMedia(VCloudDirectorMediaType.ORG_LEASE_SETTINGS)
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/org/admin/updateVAppLeaseSettings.xml", 
                  VCloudDirectorMediaType.ORG_LEASE_SETTINGS)
            .httpResponseBuilder().build());

      OrgLeaseSettings expected = updateOrgVAppLeaseSettings();

      assertEquals(client.getAdminOrgClient().updateVAppLeaseSettings(orgRef.getURI(), expected), expected);
   }
   
   public static final OrgLeaseSettings updateOrgVAppLeaseSettings() {
      return orgVAppLeaseSettings().toBuilder()
         
         .build();
   }
 
   @Test(enabled = false)
   public void testGetVAppTemplateLeaseSettings() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/admin/org/???/settings/vAppTemplateLeaseSettings")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/org/admin/vAppTemplateLeaseSettings.xml", 
                  VCloudDirectorMediaType.ORG_VAPP_TEMPLATE_LEASE_SETTINGS)
            .httpResponseBuilder().build());

      OrgVAppTemplateLeaseSettings expected = orgVAppTemplateLeaseSettings();

      assertEquals(client.getAdminOrgClient().getVAppTemplateLeaseSettings(orgRef.getURI()), expected);
   }
   
   public static final OrgVAppTemplateLeaseSettings orgVAppTemplateLeaseSettings() {
      return OrgVAppTemplateLeaseSettings.builder()
         
         .build();
   }
   
   @Test(enabled = false)
   public void testUpdateOrgVAppTemplateLeaseSettings() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", "/admin/org/???/settings/vAppTemplateLeaseSettings")
            .xmlFilePayload("/org/admin/updateVAppLeaseSettingsSource.xml", 
                  VCloudDirectorMediaType.ORG_VAPP_TEMPLATE_LEASE_SETTINGS)
            .acceptMedia(VCloudDirectorMediaType.ORG_VAPP_TEMPLATE_LEASE_SETTINGS)
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/org/admin/updateVAppLeaseSettings.xml", 
                  VCloudDirectorMediaType.ORG_VAPP_TEMPLATE_LEASE_SETTINGS)
            .httpResponseBuilder().build());

      OrgVAppTemplateLeaseSettings expected = updateOrgVAppTemplateLeaseSettings();

      assertEquals(client.getAdminOrgClient().updateVAppTemplateLeaseSettings(orgRef.getURI(), expected), expected);
   }
   
   public static final OrgVAppTemplateLeaseSettings updateOrgVAppTemplateLeaseSettings() {
      return orgVAppTemplateLeaseSettings().toBuilder()
         
         .build();
   }
}
