/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.mezeo.pcs2;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.InputStream;
import java.net.URI;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.IOUtils;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.mezeo.pcs2.domain.ContainerMetadata;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code PCSDiscovery}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "pcs2.PCSConnectionLiveTest")
public class PCSConnectionLiveTest {

   private PCSConnection connection;

   @BeforeGroups(groups = { "live" })
   public void setupConnection() {
      String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      URI endpoint = URI.create(checkNotNull(System.getProperty("jclouds.test.endpoint"),
               "jclouds.test.endpoint"));

      connection = PCSContextFactory.createContext(endpoint, user, password,
               new Log4JLoggingModule()).getApi();

   }

   private String containerPrefix = BaseBlobStoreIntegrationTest.CONTAINER_PREFIX;

   URI container1;
   URI container2;

   @Test
   public void testListContainers() throws Exception {
      SortedSet<ContainerMetadata> response = connection.listContainers();
      assertNotNull(response);
      long initialContainerCount = response.size();
      assertTrue(initialContainerCount >= 0);

      // Create test containers
      String[] containerJsr330 = new String[] { containerPrefix + ".testListOwnedContainers1",
               containerPrefix + ".testListOwnedContainers2" };
      container1 = connection.createContainer(containerJsr330[0]).get(10, TimeUnit.SECONDS);
      container2 = connection.createContainer(containerJsr330[1]).get(10, TimeUnit.SECONDS);

      // Test default listing
      response = connection.listContainers();

      connection.listFiles(container1).get(10, TimeUnit.SECONDS);
      connection.listFiles(container2).get(10, TimeUnit.SECONDS);
      connection.deleteContainer(container1).get(10, TimeUnit.SECONDS);
      connection.deleteContainer(container2).get(10, TimeUnit.SECONDS);

      response = connection.listContainers();
      // assertEquals(response.size(), initialContainerCount + 2);// if the containers already
      // exist, this will fail
   }

   @Test
   public void testObjectOperations() throws Exception {
      String containerName = containerPrefix + ".testObjectOperations";
      String data = "Here is my data";
      
      URI container = connection.createContainer(containerName).get(10, TimeUnit.SECONDS);

      // Test PUT with string data, ETag hash, and a piece of metadata
      PCSFile object = new PCSFile("object");
      object.setData(data);
      object.setContentLength(data.length());
      URI objectURI = connection.uploadFile(container, object).get(30, TimeUnit.SECONDS);

      try {
         connection.downloadFile(UriBuilder.fromUri(objectURI).path("sad").build()).get(10,
                  TimeUnit.SECONDS);
         assert false;
      } catch (KeyNotFoundException e) {
      }
      // Test GET of object (including updated metadata)
      InputStream file = connection.downloadFile(objectURI).get(120, TimeUnit.SECONDS);
      assertEquals(IOUtils.toString(file), data);

      try {
         connection.uploadFile(container, object).get(10, TimeUnit.SECONDS);
      } catch (Throwable e) {
         assertEquals(e.getCause().getClass(), HttpResponseException.class);
         assertEquals(((HttpResponseException) e.getCause()).getResponse().getStatusCode(), 422);
      }

      connection.deleteFile(objectURI).get(10, TimeUnit.SECONDS);
      connection.deleteContainer(container).get(10, TimeUnit.SECONDS);
   }

}
