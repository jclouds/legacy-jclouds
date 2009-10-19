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
package org.jclouds.nirvanix.sdn;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.blobstore.internal.BlobImpl;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.nirvanix.sdn.domain.UploadInfo;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code SDNConnection}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "sdn.SDNConnectionLiveTest")
public class SDNConnectionLiveTest {

   protected SDNConnection connection;
   private String containerPrefix = BaseBlobStoreIntegrationTest.CONTAINER_PREFIX;

   URI container1;
   URI container2;

   @BeforeGroups(groups = { "live" })
   public void setupConnection() {
      String appname = checkNotNull(System.getProperty("jclouds.test.appname"),
               "jclouds.test.appname");
      String appid = checkNotNull(System.getProperty("jclouds.test.appid"), "jclouds.test.appid");
      String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

      connection = new SDNContextBuilder(appid, appname, user, password).withModules(
               new Log4JLoggingModule()).buildContext().getApi();
   }

   public void testUploadToken() throws InterruptedException, ExecutionException, TimeoutException {
      String containerName = containerPrefix + ".testObjectOperations";
      long size = 1024;
      UploadInfo uploadInfo = connection.getStorageNode(containerName, size);
      assertNotNull(uploadInfo.getHost());
      assertNotNull(uploadInfo.getToken());

      connection.upload(uploadInfo.getHost(), uploadInfo.getToken(), containerName,
               new BlobImpl<BlobMetadata>("test.txt", "value")).get(30, TimeUnit.SECONDS);

      String metadataS = connection.getMetadata(containerName + "/test.txt").get(30,
               TimeUnit.SECONDS);
      System.err.println(metadataS);

      String content = connection.getFile(containerName + "/test.txt").get(30, TimeUnit.SECONDS);
      assertEquals(content, "value");

       Map<String, String> metadata = ImmutableMap.of("chef", "sushi", "foo", "bar");
       connection.setMetadata(containerName+"/test.txt", metadata).get(30, TimeUnit.SECONDS);

   }
}
