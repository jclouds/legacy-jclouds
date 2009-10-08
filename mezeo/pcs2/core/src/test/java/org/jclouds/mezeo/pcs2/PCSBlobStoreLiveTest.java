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

import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.mezeo.pcs2.domain.ContainerMetadata;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code PCSDiscovery}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "pcs2.PCSConnectionLiveTest")
public class PCSBlobStoreLiveTest {

   private BlobStore<ContainerMetadata, FileMetadata, PCSFile> connection;

   @BeforeGroups(groups = { "live" })
   public void setupConnection() {
      String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      URI endpoint = URI.create(checkNotNull(System.getProperty("jclouds.test.endpoint"),
               "jclouds.test.endpoint"));

      connection = PCSContextFactory.createContext(endpoint, user, password,
               new Log4JLoggingModule()).getBlobStore();

   }

   private String containerPrefix = BaseBlobStoreIntegrationTest.CONTAINER_PREFIX;

   public void testObjectOperations() throws Exception {
      String containerName = containerPrefix + ".testObjectOperations";
      String data = "Here is my data";

      connection.createContainer(containerName).get(10, TimeUnit.SECONDS);

      PCSFile object = new PCSFile("path/object");
      object.setData(data);
      object.setContentLength(data.length());
      connection.putBlob(containerName, object).get(60, TimeUnit.SECONDS);

      InputStream file = (InputStream) connection.getBlob(containerName, "path/object").get(30,
               TimeUnit.SECONDS).getData();
      assertEquals(IOUtils.toString(file), data);

      connection.removeBlob(containerName, "path/object").get(10, TimeUnit.SECONDS);
      connection.deleteContainer(containerName).get(10, TimeUnit.SECONDS);
   }

}
