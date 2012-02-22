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
import static org.testng.Assert.fail;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorClient;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Error;
import org.jclouds.vcloud.director.v1_5.domain.IpAddresses;
import org.jclouds.vcloud.director.v1_5.domain.IpRange;
import org.jclouds.vcloud.director.v1_5.domain.IpRanges;
import org.jclouds.vcloud.director.v1_5.domain.IpScope;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.OrgNetwork;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.SyslogServerSettings;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorRestClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Allows us to test a client via its side effects.
 * 
 * @author danikov
 */
@Test(groups = { "unit", "user" }, singleThreaded = true, testName = "NetworkClientExpectTest")
public class NetworkClientExpectTest extends BaseVCloudDirectorRestClientExpectTest {
   
   @Test
   public void testWhenResponseIs2xxLoginReturnsValidNetwork() {
      URI networkUri = URI.create(endpoint + "/network/55a677cf-ab3f-48ae-b880-fab90421980c");

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            getStandardRequest("GET", "/network/55a677cf-ab3f-48ae-b880-fab90421980c"),
            getStandardPayloadResponse("/network/network.xml", VCloudDirectorMediaType.ORG_NETWORK));
      
      OrgNetwork expected = orgNetwork();

      Reference networkRef = Reference.builder().href(networkUri).build();

      assertEquals(client.getNetworkClient().getNetwork(networkRef), expected);
   }

   @Test
   public void testWhenResponseIs400ForInvalidNetworkId() {
      URI networkUri = URI.create(endpoint + "/network/NOTAUUID");

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse,
            getStandardRequest("GET", "/network/NOTAUUID"),
            getStandardPayloadResponse(400, "/network/error400.xml", VCloudDirectorMediaType.ERROR));

      Error expected = Error.builder()
            .message("validation error : EntityRef has incorrect type, expected type is com.vmware.vcloud.entity.network.")
            .majorErrorCode(400)
            .minorErrorCode("BAD_REQUEST")
            .build();

      Reference networkRef = Reference.builder().href(networkUri).build();
      try {
         client.getNetworkClient().getNetwork(networkRef);
         fail("Should give HTTP 400 error");
      } catch (VCloudDirectorException vde) {
         assertEquals(vde.getError(), expected);
      } catch (Exception e) {
         fail("Should have thrown a VCloudDirectorException");
      }
   }

   @Test
   public void testWhenResponseIs403ForCatalogIdUsedAsNetworkId() {
      URI networkUri = URI.create(endpoint + "/network/9e08c2f6-077a-42ce-bece-d5332e2ebb5c");

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse,
            getStandardRequest("GET", "/network/9e08c2f6-077a-42ce-bece-d5332e2ebb5c"),
            getStandardPayloadResponse(403, "/network/error403-catalog.xml", VCloudDirectorMediaType.ERROR));

      Error expected = Error.builder()
            .message("This operation is denied.")
            .majorErrorCode(403)
            .minorErrorCode("ACCESS_TO_RESOURCE_IS_FORBIDDEN")
            .build();

      Reference networkRef = Reference.builder().href(networkUri).build();

      try {
         client.getNetworkClient().getNetwork(networkRef);
         fail("Should give HTTP 403 error");
      } catch (VCloudDirectorException vde) {
         assertEquals(vde.getError(), expected);
      } catch (Exception e) {
         fail("Should have thrown a VCloudDirectorException");
      }
   }

   @Test
   public void testWhenResponseIs403ForFakeNetworkId() {
      URI networkUri = URI.create(endpoint + "/network/aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse,
            getStandardRequest("GET", "/network/aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"),
            getStandardPayloadResponse(403, "/network/error403-fake.xml", VCloudDirectorMediaType.ERROR));

      Error expected = Error.builder()
            .message("This operation is denied.")
            .majorErrorCode(403)
            .minorErrorCode("ACCESS_TO_RESOURCE_IS_FORBIDDEN")
            .build();

      Reference networkRef = Reference.builder().href(networkUri).build();

      try {
         client.getNetworkClient().getNetwork(networkRef);
         fail("Should give HTTP 403 error");
      } catch (VCloudDirectorException vde) {
         assertEquals(vde.getError(), expected);
      } catch (Exception e) {
         fail("Should have thrown a VCloudDirectorException");
      }
   }
   
   @Test
   public void testWhenResponseIs2xxLoginReturnsValidMetadataList() {
      URI networkUri = URI.create(endpoint + "/network/55a677cf-ab3f-48ae-b880-fab90421980c");
      
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            getStandardRequest("GET", "/network/55a677cf-ab3f-48ae-b880-fab90421980c/metadata"),
            getStandardPayloadResponse("/network/metadata.xml", VCloudDirectorMediaType.METADATA));
      
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

       Reference networkRef = Reference.builder().href(networkUri).build();
 
       assertEquals(client.getNetworkClient().getMetadata(networkRef), expected);
   }
   
   @Test(enabled=false) // No metadata in exemplar xml...
   public void testWhenResponseIs2xxLoginReturnsValidMetadata() {
      URI networkUri = URI.create("https://vcloudbeta.bluelock.com/api/network/55a677cf-ab3f-48ae-b880-fab90421980c");
      
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            getStandardRequest("GET", "/network/55a677cf-ab3f-48ae-b880-fab90421980c/metadata/KEY"),
            getStandardPayloadResponse("/network/metadataEntry.xml", VCloudDirectorMediaType.METADATA_ENTRY));
      
      MetadataEntry expected = MetadataEntry.builder()
            .entry("key", "value")
            .build();

      Reference networkRef = Reference.builder().href(networkUri).build();

      assertEquals(client.getNetworkClient().getMetadataValue(networkRef, "KEY"), expected);
   }

   public static OrgNetwork orgNetwork() {
      return OrgNetwork.builder()
            .name("internet01-Jclouds")
            .id("urn:vcloud:network:55a677cf-ab3f-48ae-b880-fab90421980c")
            .type(VCloudDirectorMediaType.ORG_NETWORK)
            .href(URI.create("https://vcloudbeta.bluelock.com/api/network/55a677cf-ab3f-48ae-b880-fab90421980c"))
            .link(Link.builder()
               .rel("up")
               .type("application/vnd.vmware.vcloud.org+xml")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
               .build())
            .link(Link.builder()
               .rel("down")
               .type("application/vnd.vmware.vcloud.metadata+xml")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/network/55a677cf-ab3f-48ae-b880-fab90421980c/metadata"))
               .build())
            .description("")
            .configuration(NetworkConfiguration.builder()
               .ipScope(IpScope.builder()
                  .isInherited(true)
                  .gateway("173.240.107.49")
                  .netmask("255.255.255.240")
                  .dns1("173.240.111.52")
                  .dns2("173.240.111.53")
                  .ipRanges(IpRanges.builder()
                        .ipRange(IpRange.builder()
                              .startAddress("173.240.107.50")
                              .endAddress("173.240.107.62")
                              .build())
                        .build())
                  .build())
               .fenceMode("bridged")
               .retainNetInfoAcrossDeployments(false)
               .syslogServerSettings(SyslogServerSettings.builder().build())
               .build())
            .allowedExternalIpAddresses(IpAddresses.builder().build())
            .build();
   }
}
