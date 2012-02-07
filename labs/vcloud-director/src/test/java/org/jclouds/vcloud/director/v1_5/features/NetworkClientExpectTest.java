/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 *(Link.builder().regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless(Link.builder().required by applicable law or agreed to in writing,
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
import org.jclouds.vcloud.director.v1_5.domain.NetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.IpScope;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.OrgNetwork;
import org.jclouds.vcloud.director.v1_5.domain.VAppNetwork;
import org.jclouds.vcloud.director.v1_5.domain.Org;
import org.jclouds.vcloud.director.v1_5.domain.OrgList;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorRestClientExpectTest;
import org.testng.annotations.Test;

/**
 * Allows us to test a client via its side effects.
 * 
 * @author danikov
 */
@Test(groups = "unit", singleThreaded = true, testName = "NetworkClientExpectTest")
public class NetworkClientExpectTest extends BaseVCloudDirectorRestClientExpectTest {
   
   /*
      GET /network/{id}
      GET /network/{id}/metadata
      GET /network/{id}/metadata/{key}
    */
   
   @Test
   public void testWhenResponseIs2xxLoginReturnsValidNetwork() {
      URI orgRef = URI.create("https://vcloudbeta.bluelock.com/api/network/NETWORK_KEY");

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            getStandardRequest("GET", orgRef), 
            getStandardPayloadResponse("/network/network.xml", VCloudDirectorMediaType.ORG_NETWORK_XML));
      
      OrgNetwork expected = OrgNetwork
         .builder()
         .name("internet01-Jclouds")
         .id("urn:vcloud:network:55a677cf-ab3f-48ae-b880-fab90421980c")
         .type(VCloudDirectorMediaType.ORG_NETWORK_XML)
         .href(URI.create("https://vcloudbeta.bluelock.com/api/network/55a677cf-ab3f-48ae-b880-fab90421980c"))
         .link(Link.builder()
            .rel("up")
            .type("application/vnd.vmware.vcloud.org+xml")
            .name("Cluster01-JClouds")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
            .build())
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.vcloud.metadata+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/network/55a677cf-ab3f-48ae-b880-fab90421980c/metadat"))
            .build())
         .description("")
         .configuration(NetworkConfiguration.builder()
            .IpScope(IpScope.builder()
               .isInherited(true)
               .gateway("")
               .netmask("")
               .dns1("")
               .dns2("")
               .ipRange("", "")
               .build())
            .fenceMode("bridged")
            .retainNetInfoAcrossDeployments(false)
            .syslogServerSettings(null)
            .build())
         .allowedExternalIpAddresses(null)
         .build();

      assertEquals(client.getNetworkClient().getNetwork(networkRef), expected);
   }
   
   @Test
   public void testWhenResponseIs2xxLoginReturnsValidMetadataList() {
      URI orgRef = URI.create("https://vcloudbeta.bluelock.com/api/network/NETWORK_KEY");
      URI metaRef = URI.create(orgRef.toASCIIString()+"/metadata/");
      
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            getStandardRequest("GET", metaRef),
            getStandardPayloadResponse("/network/metadata.xml", VCloudDirectorMediaType.METADATA_XML));
      
      Metadata expected = Metadata.builder()
            .type("application/vnd.vmware.vcloud.metadata+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/network/NETWORK_KEY/metadata"))
            .link(Link.builder()
                  .rel("up")
                  .type("????????")
                  .href(URI.create("??????"))
                  .build())
            .build();

      assertEquals(client.getOrgClient().getMetadata(orgRef), expected);
   }
   
   @Test(enabled=false) // No metadata in exemplar xml...
   public void testWhenResponseIs2xxLoginReturnsValidMetadata() {
      URI metadataRef = URI.create("https://vcloudbeta.bluelock.com/api/network/NETWORK_KEY/metadata/KEY");
      
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            getStandardRequest("GET", metadataRef),
            getStandardPayloadResponse("/network/metadata.xml", VCloudDirectorMediaType.METADATAENTRY_XML));
      
      MetadataEntry expected = MetadataEntry.builder()
            .build();

      assertEquals(client.getOrgClient().getMetadataEntry(metadataRef), expected);
   }
}
