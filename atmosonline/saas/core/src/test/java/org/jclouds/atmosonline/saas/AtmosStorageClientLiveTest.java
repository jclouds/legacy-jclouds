/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.atmosonline.saas;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.security.SecureRandom;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.atmosonline.saas.domain.DirectoryEntry;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code AtmosStorageClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "emcsaas.AtmosStorageClientLiveTest")
public class AtmosStorageClientLiveTest {

   protected AtmosStorageClient connection;
   private String containerPrefix = BaseBlobStoreIntegrationTest.CONTAINER_PREFIX;

   URI container1;
   URI container2;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String uid = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

      connection = new AtmosStorageContextBuilder(new AtmosStoragePropertiesBuilder(uid, key)
               .build()).withModules(new Log4JLoggingModule()).buildContext().getApi();
   }

   @Test
   public void testListDirectorys() throws Exception {
      SortedSet<DirectoryEntry> response = connection.listDirectories();
      assert null != response;
   }

   String privateDirectory;
   String publicDirectory;
   String account;

   @Test(timeOut = 5 * 60 * 1000)
   public void testCreateDirectory() throws Exception {
      boolean created = false;
      while (!created) {
         privateDirectory = containerPrefix + new SecureRandom().nextInt();
         try {
            created = connection.createDirectory(privateDirectory) != null;
         } catch (UndeclaredThrowableException e) {
            HttpResponseException htpe = (HttpResponseException) e.getCause().getCause();
            if (htpe.getResponse().getStatusCode() == 409)
               continue;
            throw e;
         }
      }
      SortedSet<DirectoryEntry> response = connection.listDirectories();
      assert response.size() > 0;
      for (DirectoryEntry id : response) {
         SortedSet<DirectoryEntry> r2 = connection.listDirectory(id.getObjectName());
         assert r2 != null;
      }
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreateDirectory" })
   public void testFileOperations() throws Exception {
      String data = "Here is my data";

      // Test PUT with string data, ETag hash, and a piece of metadata
      AtmosObject object = connection.newObject();
      object.getContentMetadata().setName("object");
      object.setData(data);
      object.getContentMetadata().setContentLength(data.length());
      object.generateMD5();
      object.getContentMetadata().setContentType("text/plain");
      object.getUserMetadata().getMetadata().put("Metadata", "metadata-value");
      URI uri = connection.createFile(privateDirectory, object).get(30, TimeUnit.SECONDS);

      AtmosObject getBlob = connection.readFile(privateDirectory + "/object").get(120,
               TimeUnit.SECONDS);
      assertEquals(IOUtils.toString((InputStream) getBlob.getData()), data);
      // TODO assertEquals(getBlob.getName(), object.getName());
      assertEquals(getBlob.getContentMetadata().getContentLength(), new Long(data.length()));
      assert getBlob.getContentMetadata().getContentType().startsWith("text/plain");
      assertEquals(getBlob.getUserMetadata().getMetadata().get("Metadata"), "metadata-value");
      try {
         Utils.toStringAndClose(uri.toURL().openStream());
         assert false : "shouldn't have worked, since it is private";
      } catch (IOException e) {

      }

   }
}
