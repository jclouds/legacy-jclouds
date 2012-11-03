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
package org.jclouds.mezeo.pcs;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.mezeo.pcs.options.PutBlockOptions.Builder.range;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.inject.Provider;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.Constants;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.mezeo.pcs.PCSAsyncClient;
import org.jclouds.mezeo.pcs.PCSClient;
import org.jclouds.mezeo.pcs.domain.ContainerList;
import org.jclouds.mezeo.pcs.domain.FileInfoWithMetadata;
import org.jclouds.mezeo.pcs.domain.PCSFile;
import org.jclouds.mezeo.pcs.domain.ResourceInfo;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.util.Strings2;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.Module;
import com.sun.jersey.api.uri.UriBuilderImpl;

/**
 * Tests behavior of {@code PCSDiscovery}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "pcs2.PCSClientLiveTest")
public class PCSClientLiveTest {

   Provider<UriBuilder> uriBuilderProvider = new Provider<UriBuilder>() {

      @Override
      public UriBuilder get() {
         return new UriBuilderImpl();
      }

   };

   private RestContext<PCSClient, PCSAsyncClient> context;
   private PCSClient connection;

   protected String provider = "pcs";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiVersion;

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = checkNotNull(System.getProperty("test." + provider + ".credential"), "test." + provider
            + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint");
     apiVersion = System.getProperty("test." + provider + ".api-version");
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(provider + ".identity", identity);
      overrides.setProperty(provider + ".credential", credential);
      if (endpoint != null)
         overrides.setProperty(provider + ".endpoint", endpoint);
      if (apiVersion != null)
         overrides.setProperty(provider + ".api-version", apiVersion);
      return overrides;
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();

      context = new RestContextFactory().createContext(provider, ImmutableSet.<Module> of(new Log4JLoggingModule()),
            overrides);

      connection = context.getApi();
      ContainerList response = connection.list();
      for (ResourceInfo resource : response) {
         if (resource.getType() == StorageType.FOLDER && resource.getName().startsWith(containerPrefix)) {
            System.err.printf("*** deleting container %s...%n", resource.getName());
            connection.deleteContainer(resource.getUrl());
         }
      }

   }

   private String containerPrefix = BaseBlobStoreIntegrationTest.CONTAINER_PREFIX;

   @Test
   public void testListContainers() throws Exception {
      ContainerList response = connection.list();
      URI rootUrl = response.getUrl();
      String name = "/";
      validateContainerList(response, rootUrl, name);

      long initialContainerCount = response.size();
      assertTrue(initialContainerCount >= 0);

      // Create test containers
      for (String container : new String[] { containerPrefix + ".testListOwnedContainers1",
            containerPrefix + ".testListOwnedContainers2" }) {
         URI containerURI = connection.createContainer(container);
         connection.putMetadataItem(containerURI, "name", container);
         response = connection.list(containerURI);
         validateContainerList(response, rootUrl, container);

         assertEquals(response.getMetadataItems().get("name"),
               URI.create(containerURI.toASCIIString() + "/metadata/name"));

         validateMetadataItemNameEquals(containerURI, container);

         connection.deleteContainer(containerURI);
      }
   }

   private void validateMetadataItemNameEquals(URI resource, String name) throws InterruptedException,
         ExecutionException, TimeoutException {
      Map<String, String> metadata = Maps.newHashMap();
      connection.addMetadataItemToMap(resource, "name", metadata);
      assertEquals(metadata.get("name"), name);
   }

   private void validateContainerList(ContainerList response, URI parent, String name) {
      assertNotNull(response.getMetadataItems());
      validateResource(response, parent, name);
   }

   private void validateFileInfo(FileInfoWithMetadata response, URI parent, String name, Long size, String mimeType) {
      assertNotNull(response.getMetadataItems());
      assertFalse(response.isPublic());
      assertEquals(response.getBytes(), size);
      assertEquals(response.getMimeType(), mimeType);
      assertNotNull(response.getContent());
      assertNotNull(response.getPermissions());
      assertNotNull(response.getThumbnail());
      validateResource(response, parent, name);
   }

   private void validateResource(ResourceInfo response, URI parent, String name) {
      assertNotNull(response);
      assertNotNull(response.getAccessed());
      assertNotNull(response.getBytes());
      assertNotNull(response.getCreated());
      assertNotNull(response.getMetadata());
      assertNotNull(response.getModified());
      assertEquals(response.getName(), name);
      assertEquals(response.getOwner(), identity);
      assertEquals(response.getParent(), parent);
      assertNotNull(response.getTags());
      assertNotNull(response.getType());
      assertNotNull(response.getUrl());
      assertNotNull(response.getVersion());
   }

   @Test
   public void testObjectOperations() throws Exception {
      String containerName = containerPrefix + ".testObjectOperations";
      String data = "Here is my data";

      URI container = connection.createContainer(containerName);

      // Test PUT with string data, ETag hash, and a piece of metadata
      PCSFile object = connection.newFile();
      object.getMetadata().setName("object");
      object.getMetadata().setMimeType("text/plain");
      object.setPayload(data);
      URI objectURI = connection.uploadFile(container, object);
      connection.putMetadataItem(objectURI, "name", "object");

      try {
         connection.downloadFile(uriBuilderProvider.get().uri(objectURI).path("sad").build());
         fail("Expected KeyNotFoundException");
      } catch (KeyNotFoundException e) {
      }
      // Test GET of object (including updated metadata)
      InputStream file = connection.downloadFile(objectURI);
      assertEquals(Strings2.toStringAndClose(file), data);
      validateFileInfoAndNameIsInMetadata(container, objectURI, "object", Long.valueOf(data.length()));

      try {
         connection.uploadFile(container, object);
      } catch (Throwable e) {
         assertEquals(e.getCause().getClass(), HttpResponseException.class);
         assertEquals(((HttpResponseException) e.getCause()).getResponse().getStatusCode(), 422);
      }

      connection.deleteFile(objectURI);
      try {
         connection.getFileInfo(objectURI);
      } catch (Throwable e) {
         assertEquals(e.getClass(), KeyNotFoundException.class);
      }

      String name = "sad";
      // try sending it in 2 parts
      object.getMetadata().setName(name);
      objectURI = connection.createFile(container, object);
      validateFileInfoAndNameIsInMetadata(container, objectURI, name, Long.valueOf(0));

      object.setPayload(data.substring(0, 2));
      connection.uploadBlock(objectURI, object, range(0, 2));
      validateFileInfoAndNameIsInMetadata(container, objectURI, name, Long.valueOf(2));

      object.setPayload(data.substring(2));
      connection.uploadBlock(objectURI, object, range(2, data.getBytes().length));
      validateFileInfoAndNameIsInMetadata(container, objectURI, name, Long.valueOf(data.length()));

      file = connection.downloadFile(objectURI);
      assertEquals(Strings2.toStringAndClose(file), data);

      // change data in an existing file
      data = "Here is my datum";
      object.setPayload(data.substring(2));
      connection.uploadBlock(objectURI, object, range(2, data.getBytes().length));
      validateFileInfoAndNameIsInMetadata(container, objectURI, name, Long.valueOf(data.length()));

      file = connection.downloadFile(objectURI);
      assertEquals(Strings2.toStringAndClose(file), data);

      connection.deleteFile(objectURI);
      connection.deleteContainer(container);
   }

   private FileInfoWithMetadata validateFileInfoAndNameIsInMetadata(URI container, URI objectURI, String name, Long size)
         throws InterruptedException, ExecutionException, TimeoutException {
      FileInfoWithMetadata response;
      connection.putMetadataItem(objectURI, "name", name);

      response = connection.getFileInfo(objectURI);
      validateFileInfo(response, container, name, size, "text/plain");

      assertEquals(response.getMetadataItems().get("name"), URI.create(objectURI.toASCIIString() + "/metadata/name"));

      validateMetadataItemNameEquals(objectURI, name);
      return response;
   }

}
