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
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Media;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorRestClientExpectTest;
import org.testng.annotations.Test;

/**
 * Allows us to test a client via its side effects.
 * 
 * @author danikov
 */
@Test(groups = "unit", singleThreaded = true, testName = "NetworkClientExpectTest")
public class MediaClientExpectTest extends BaseVCloudDirectorRestClientExpectTest {
   
   @Test
   public void testWhenResponseIs2xxLoginReturnsValidMedia() {
      URI mediaRef = URI.create("https://vcloudbeta.bluelock.com/api/media/KEY");

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            getStandardRequest("GET", mediaRef), 
            getStandardPayloadResponse("/media/media.xml", VCloudDirectorMediaType.MEDIA_XML));
      
      Media expected = Media
         .builder()
         .build();

      assertEquals(client.getMediaClient().getMedia(mediaRef), expected);
   }
   
   @Test
   public void testWhenResponseIs2xxLoginReturnsValidMetadata() {
      URI mediaRef = URI.create("https://vcloudbeta.bluelock.com/api/media/KEY");
      URI metaRef = URI.create(mediaRef.toASCIIString()+"/metadata/");
      
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            getStandardRequest("GET", metaRef),
            getStandardPayloadResponse("/media/metadata.xml", VCloudDirectorMediaType.METADATA_XML));
      
      Metadata expected = Metadata.builder()
            .type("application/vnd.vmware.vcloud.metadata+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/media/KEY/metadata"))
            .link(Link.builder()
                  .rel("up")
                  .type("application/vnd.vmware.vcloud.network+xml")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/media/KEY"))
                  .build())
            .build();

      assertEquals(client.getMediaClient().getMetadata(mediaRef), expected);
   }
   
   @Test(enabled=false) // No metadata in exemplar xml...
   public void testWhenResponseIs2xxLoginReturnsValidMetadataEntry() {
      URI metadataRef = URI.create(
            "https://vcloudbeta.bluelock.com/api/media/KEY/metadata/KEY");
      
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            getStandardRequest("GET", metadataRef),
            getStandardPayloadResponse("/media/metadataEntry.xml", VCloudDirectorMediaType.METADATAENTRY_XML));
      
      MetadataEntry expected = MetadataEntry.builder()
            .build();

      assertEquals(client.getMediaClient().getMetadataEntry(metadataRef), expected);
   }
}
