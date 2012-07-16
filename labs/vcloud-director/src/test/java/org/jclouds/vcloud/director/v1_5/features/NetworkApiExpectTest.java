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
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Error;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.network.DhcpService;
import org.jclouds.vcloud.director.v1_5.domain.network.IpAddresses;
import org.jclouds.vcloud.director.v1_5.domain.network.IpRange;
import org.jclouds.vcloud.director.v1_5.domain.network.IpRanges;
import org.jclouds.vcloud.director.v1_5.domain.network.IpScope;
import org.jclouds.vcloud.director.v1_5.domain.network.Network;
import org.jclouds.vcloud.director.v1_5.domain.network.Network.FenceMode;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkFeatures;
import org.jclouds.vcloud.director.v1_5.domain.network.SyslogServerSettings;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgNetwork;
import org.jclouds.vcloud.director.v1_5.internal.VCloudDirectorAdminApiExpectTest;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorApi;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Test the {@link NetworkApi} via its side effects.
 * 
 * @author danikov
 */
@Test(groups = { "unit", "user" }, singleThreaded = true, testName = "NetworkApiExpectTest")
public class NetworkApiExpectTest extends VCloudDirectorAdminApiExpectTest {
   
   @Test
   public void testGetNetwork() {
      URI networkUri = URI.create(endpoint + "/network/f3ba8256-6f48-4512-aad6-600e85b4dc38");

      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/network/f3ba8256-6f48-4512-aad6-600e85b4dc38")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/network/network.xml", VCloudDirectorMediaType.ORG_NETWORK)
            .httpResponseBuilder().build());
      
      OrgNetwork expected = orgNetwork();
      assertEquals(Network.<OrgNetwork>toSubType(api.getNetworkApi().getNetwork(networkUri)), expected);
   }

   @Test
   public void testGetNetworkWithInvalidId() {
      URI networkUri = URI.create(endpoint + "/network/NOTAUUID");

      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/network/NOTAUUID")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/network/error400.xml", VCloudDirectorMediaType.ERROR)
            .httpResponseBuilder().statusCode(400).build());
   
      Error expected = Error.builder()
         .message("validation error : EntityRef has incorrect type, expected type is com.vmware.vcloud.entity.network.")
         .majorErrorCode(400)
         .minorErrorCode("BAD_REQUEST")
         .build();

      try {
         api.getNetworkApi().getNetwork(networkUri);
         fail("Should give HTTP 400 error");
      } catch (VCloudDirectorException vde) {
         assertEquals(vde.getError(), expected);
      } catch (Exception e) {
         fail("Should have thrown a VCloudDirectorException");
      }
   }

   @Test
   public void testGetNetworkWithCatalogId() {
      URI networkUri = URI.create(endpoint + "/network/9e08c2f6-077a-42ce-bece-d5332e2ebb5c");

      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/network/9e08c2f6-077a-42ce-bece-d5332e2ebb5c")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/network/error403-catalog.xml", VCloudDirectorMediaType.ERROR)
            .httpResponseBuilder().statusCode(403).build());
      
      assertNull(api.getNetworkApi().getNetwork(networkUri));
   }

   @Test
   public void testGetNetworkWithFakeId() {
      URI networkUri = URI.create(endpoint + "/network/aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/network/aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/network/error403-fake.xml", VCloudDirectorMediaType.ERROR)
            .httpResponseBuilder().statusCode(403).build());
      
      assertNull(api.getNetworkApi().getNetwork(networkUri));
   }
   
   @Test
   public void testGetMetadata() {
      URI networkUri = URI.create(endpoint + "/network/55a677cf-ab3f-48ae-b880-fab90421980c");
      
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/network/55a677cf-ab3f-48ae-b880-fab90421980c/metadata")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/network/metadata.xml", VCloudDirectorMediaType.METADATA)
            .httpResponseBuilder().build());
      
      Metadata expected = Metadata.builder()
         .type("application/vnd.vmware.vcloud.metadata+xml")
         .href(URI.create("https://vcloudbeta.bluelock.com/api/network/55a677cf-ab3f-48ae-b880-fab90421980c/metadata"))
         .link(Link.builder()
            .rel("up")
            .type("application/vnd.vmware.vcloud.network+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/network/55a677cf-ab3f-48ae-b880-fab90421980c"))
            .build())
         .entries(ImmutableSet.of(MetadataEntry.builder().entry("key", "value").build()))
         .build();
 
       assertEquals(api.getNetworkApi().getMetadataApi().getMetadata(networkUri), expected);
   }
   
   @Test
   public void testGetMetadataValue() {
      URI networkUri = URI.create("https://vcloudbeta.bluelock.com/api/network/55a677cf-ab3f-48ae-b880-fab90421980c");
      
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/network/55a677cf-ab3f-48ae-b880-fab90421980c/metadata/KEY")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/network/metadataValue.xml", VCloudDirectorMediaType.METADATA_ENTRY)
            .httpResponseBuilder().build());
      
      MetadataValue expected = MetadataValue.builder()
         .href(URI.create("https://vcloudbeta.bluelock.com/api/network/55a677cf-ab3f-48ae-b880-fab90421980c/metadata/key"))
         .link(Link.builder()
            .rel("up")
            .type("application/vnd.vmware.vcloud.metadata+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/network/55a677cf-ab3f-48ae-b880-fab90421980c/metadata"))
            .build())
         .value("value")
         .build();

      assertEquals(api.getNetworkApi().getMetadataApi().getMetadataValue(networkUri, "KEY"), expected);
   }
   
   public static OrgNetwork orgNetwork() {
      return OrgNetwork.builder()
         .name("ilsolation01-Jclouds")
         .id("urn:vcloud:network:f3ba8256-6f48-4512-aad6-600e85b4dc38")
         .type("application/vnd.vmware.vcloud.orgNetwork+xml")
         .href(URI.create("https://vcloudbeta.bluelock.com/api/network/f3ba8256-6f48-4512-aad6-600e85b4dc38"))
         .link(Link.builder()
            .rel("up")
            .type("application/vnd.vmware.vcloud.org+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
            .build())
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.vcloud.metadata+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/network/f3ba8256-6f48-4512-aad6-600e85b4dc38/metadata"))
            .build())
         .description("")
         .configuration(NetworkConfiguration.builder()
            .ipScope(IpScope.builder()
               .isInherited(false)
               .gateway("192.168.1.1")
               .netmask("255.255.255.0")
               .dns1("173.240.111.52")
               .dns2("173.240.111.53")
               .ipRanges(IpRanges.builder()
                     .ipRange(IpRange.builder()
                           .startAddress("192.168.1.100")
                           .endAddress("192.168.1.199")
                           .build())
                     .build())
               .build())
            .fenceMode(FenceMode.ISOLATED)
            .retainNetInfoAcrossDeployments(false)
            .features(NetworkFeatures.builder()
               .service(DhcpService.builder()
                  .enabled(false)
                  .defaultLeaseTime(3600)
                  .maxLeaseTime(7200)
                  .ipRange(IpRange.builder()
                     .startAddress("192.168.1.2")
                     .endAddress("192.168.1.99")
                     .build())
                  .build())
               .build())
            .syslogServerSettings(SyslogServerSettings.builder().build())
            .build())
         .allowedExternalIpAddresses(IpAddresses.builder().build())
         .build();
   }
}
