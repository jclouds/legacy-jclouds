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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ANY;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ERROR;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.METADATA;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.METADATA_ENTRY;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.TASK;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.net.URI;
import java.util.TimeZone;

import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.internal.VCloudDirectorAdminApiExpectTest;
import org.testng.annotations.Test;

/**
 * Tests the request/response behavior of {@link org.jclouds.vcloud.director.v1_5.features.MetadataApi}
 *
 * @author Adam Lowe
 */
@Test(groups = { "unit", "user" }, testName = "MetadataApiExpectTest")
public class MetadataApiExpectTest extends VCloudDirectorAdminApiExpectTest {

   public MetadataApiExpectTest() {
      TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
   }

   public void testVappTemplateMetadata() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      MetadataApi api = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/metadata").acceptMedia(ANY).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/metadata.xml", METADATA).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("POST", templateId + "/metadata").xmlFilePayload("/vapptemplate/metadata.xml", METADATA).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getMetadataApi(uri);

      assertNotNull(api);
      Metadata metadata = api.get();

      assertEquals(metadata, exampleMetadata());

      Task task = api.putAll(exampleMetadata());
      assertNotNull(task);
   }

   @Test(expectedExceptions = VCloudDirectorException.class)
   public void testErrorGetMetadata() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      MetadataApi api = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/metadata").acceptMedia(ANY).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error400.xml", ERROR).httpResponseBuilder().statusCode(400).build()).getMetadataApi(uri);

      api.get();
   }

   @Test(expectedExceptions = VCloudDirectorException.class)
   public void testErrorEditMetadata() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      MetadataApi api = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("POST", templateId + "/metadata").xmlFilePayload("/vapptemplate/metadata.xml", METADATA).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error400.xml", ERROR).httpResponseBuilder().statusCode(400).build()).getMetadataApi(uri);

      api.putAll(exampleMetadata());
   }
   
   public void testVappTemplateMetadataValue() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      MetadataApi api = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/metadata/12345").acceptMedia(METADATA_ENTRY).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/metadataValue.xml", METADATA_ENTRY).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId + "/metadata/12345").xmlFilePayload("/vapptemplate/metadataValue.xml", METADATA_ENTRY).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("DELETE", templateId + "/metadata/12345").acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getMetadataApi(uri);

      assertNotNull(api);
      String metadata = api.get("12345");

      assertEquals(metadata, "some value");

      Task task = api.put("12345", "some value");
      assertNotNull(task);

      task = api.remove("12345");
      assertNotNull(task);
   }

   public void testErrorGetMetadataValue() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      MetadataApi api = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/metadata/12345").acceptMedia(METADATA_ENTRY).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error403.xml", ERROR).httpResponseBuilder().statusCode(403).build()).getMetadataApi(uri);

      assertNull(api.get("12345"));
   }
   
   @Test(expectedExceptions = VCloudDirectorException.class)
   public void testErrorEditMetadataValue() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      MetadataApi api = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId + "/metadata/12345").xmlFilePayload("/vapptemplate/metadataValue.xml", METADATA_ENTRY).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error400.xml", ERROR).httpResponseBuilder().statusCode(400).build()).getMetadataApi(uri);

      api.put("12345", "some value");
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testRemoveMissingMetadataValue() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      MetadataApi api = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("DELETE", templateId + "/metadata/12345").acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error403.xml", ERROR).httpResponseBuilder().statusCode(403).build()).getMetadataApi(uri);

      api.remove("12345");
   }
   private Metadata exampleMetadata() {
      return Metadata.builder()
            .href(URI.create("https://vcloudbeta.bluelock.com/api/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9/metadata"))
            .type("application/vnd.vmware.vcloud.metadata+xml")
            .link(Link.builder().href(URI.create("https://vcloudbeta.bluelock.com/api/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9"))
                  .type("application/vnd.vmware.vcloud.vAppTemplate+xml").rel("up").build())
            .entry(MetadataEntry.builder().key("key").value("value").build()).build();
   }

}
