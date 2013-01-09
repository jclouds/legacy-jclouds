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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ADMIN_NETWORK;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ENTITY;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ERROR;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.METADATA;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.METADATA_VALUE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.NETWORK;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ORG;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.domain.Error;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.network.DhcpService;
import org.jclouds.vcloud.director.v1_5.domain.network.IpAddresses;
import org.jclouds.vcloud.director.v1_5.domain.network.IpRange;
import org.jclouds.vcloud.director.v1_5.domain.network.IpRanges;
import org.jclouds.vcloud.director.v1_5.domain.network.IpScope;
import org.jclouds.vcloud.director.v1_5.domain.network.Network.FenceMode;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkFeatures;
import org.jclouds.vcloud.director.v1_5.domain.network.SyslogServerSettings;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgNetwork;
import org.jclouds.vcloud.director.v1_5.internal.VCloudDirectorAdminApiExpectTest;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorApi;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.net.HttpHeaders;

/**
 * Test the {@link NetworkApi} via its side effects.
 * 
 * @author danikov
 */
@Test(groups = { "unit", "user" }, singleThreaded = true, testName = "NetworkApiExpectTest")
public class NetworkApiExpectTest extends VCloudDirectorAdminApiExpectTest {

   static String network = "55a677cf-ab3f-48ae-b880-fab90421980c";
   static String networkUrn = "urn:vcloud:network:" + network;
   static URI networkHref = URI.create(endpoint + "/network/" + network);
   
   HttpRequest get = HttpRequest.builder()
            .method("GET")
            .endpoint(networkHref)
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();

    HttpResponse getResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/network/network.xml", ORG + ";version=1.5"))
            .build();
    
   @Test
   public void testGetNetworkHref() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, get, getResponse);
      assertEquals(api.getNetworkApi().get(networkHref), network());
   }
   
   @Test
   public void testGetNetworkHrefInvalidId() {
      
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
               get.toBuilder().endpoint(endpoint + "/network/NOTAUUID").build(), 
               HttpResponse.builder()
                           .statusCode(400)
                           .payload(payloadFromResourceWithContentType("/network/error400.xml", ERROR + ";version=1.5"))
                           .build());
      
      Error expected = Error.builder()
         .message("validation error : EntityRef has incorrect type, expected type is com.vmware.vcloud.entity.network.")
         .majorErrorCode(400)
         .minorErrorCode("BAD_REQUEST")
         .build();

      try {
         api.getNetworkApi().get(URI.create(endpoint + "/network/NOTAUUID"));
         fail("Should give HTTP 400 error");
      } catch (VCloudDirectorException vde) {
         assertEquals(vde.getError(), expected);
      } catch (Exception e) {
         fail("Should have thrown a VCloudDirectorException");
      }
   }

   @Test
   public void testGetNetworkHrefCatalogId() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
               get.toBuilder().endpoint(endpoint + "/network/9e08c2f6-077a-42ce-bece-d5332e2ebb5c").build(), 
               HttpResponse.builder()
                           .statusCode(403)
                           .payload(payloadFromResourceWithContentType("/network/error403-catalog.xml", ERROR + ";version=1.5"))
                           .build());
      
      assertNull(api.getNetworkApi().get(URI.create(endpoint + "/network/9e08c2f6-077a-42ce-bece-d5332e2ebb5c")));
   }

   @Test
   public void testGetNetworkHrefFakeId() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
               get.toBuilder().endpoint(endpoint + "/network/aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee").build(), 
               HttpResponse.builder()
                           .statusCode(403)
                           .payload(payloadFromResourceWithContentType("/network/error403-fake.xml", ERROR + ";version=1.5"))
                           .build());
      
      assertNull(api.getNetworkApi().get(URI.create(endpoint + "/network/aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")));
   }
   
   HttpRequest resolveNetwork = HttpRequest.builder()
            .method("GET")
            .endpoint(endpoint + "/entity/" + networkUrn)
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();
   
   String networkEntity = asString(createXMLBuilder("Entity").a("xmlns", "http://www.vmware.com/vcloud/v1.5")
                                                             .a("name", networkUrn)
                                                             .a("id", networkUrn)
                                                             .a("type", ENTITY)
                                                             .a("href", endpoint + "/entity/" + networkUrn)
                                  .e("Link").a("rel", "alternate").a("type", NETWORK).a("href", networkHref.toString()).up()
                                  // TODO: remove this when VCloudDirectorApiExpectTest no longer inherits from VCloudDirectorAdminApiExpectTest
                                  .e("Link").a("rel", "alternate").a("type", ADMIN_NETWORK).a("href", networkHref.toString()).up());
   
   HttpResponse resolveNetworkResponse = HttpResponse.builder()
           .statusCode(200)
           .payload(payloadFromStringWithContentType(networkEntity, ENTITY + ";version=1.5"))
           .build();
   
   @Test
   public void testGetNetworkUrn() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, resolveNetwork, resolveNetworkResponse, get, getResponse);
      assertEquals(api.getNetworkApi().get(networkUrn), network());
   }
   

   HttpRequest getMetadata = HttpRequest.builder()
            .method("GET")
            .endpoint(networkHref + "/metadata")
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token).build();

   HttpResponse getMetadataResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/network/metadata.xml", METADATA))
            .build();

   @Test
   public void testGetNetworkMetadataHref() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, getMetadata, getMetadataResponse);
      assertEquals(api.getMetadataApi(networkHref).get(), metadata());
   }

   static Metadata metadata() {
      return Metadata.builder()
                     .type("application/vnd.vmware.vcloud.metadata+xml")
                     .href(URI.create(endpoint + "/network/" + network + "/metadata"))
                     .link(Link.builder()
                               .rel("up")
                               .type("application/vnd.vmware.vcloud.network+xml")
                               .href(networkHref)
                               .build())
                     .entries(ImmutableSet.of(metadataEntry()))
                     .build();
   }
   
   private static MetadataEntry metadataEntry() {
      return MetadataEntry.builder().entry("key", "value").build();
   }

   HttpRequest getMetadataValue = HttpRequest.builder()
            .method("GET")
            .endpoint(networkHref + "/metadata/KEY")
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token).build();

   HttpResponse getMetadataValueResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/network/metadataValue.xml", METADATA_VALUE))
            .build();

   @Test
   public void testGetNetworkMetadataEntryHref() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, getMetadataValue, getMetadataValueResponse);
      assertEquals(api.getMetadataApi(networkHref).get("KEY"), "value");
   }
   
   public static OrgNetwork network() {
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
