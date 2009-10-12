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
package org.jclouds.azure.storage.blob.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.azure.storage.blob.AzureBlobUtil;
import org.jclouds.azure.storage.blob.domain.BlobMetadata;
import org.jclouds.azure.storage.blob.domain.ListBlobsResponse;
import org.jclouds.azure.storage.blob.domain.TreeSetListBlobsResponse;
import org.jclouds.azure.storage.domain.BoundedSortedSet;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.util.DateService;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedSet;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Tests behavior of {@code AddMD5ToListBlobsResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "azureblob.AddMD5ToListBlobsResponseTest")
public class AddMD5ToListBlobsResponseTest extends BaseHandlerTest {
   private DateService dateService;

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
      injector = injector.createChildInjector(new AbstractModule() {

         @Override
         protected void configure() {
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         AzureBlobUtil getAzureBlobUtil() {
            return new AzureBlobUtil() {

               public byte[] getMD5(URI container, String key) {
                  if (key.equals("blob1.txt")) {
                     return HttpUtils.fromHexString("01");
                  } else if (key.equals("blob2.txt")) {
                     return HttpUtils.fromHexString("02");
                  } else if (key.equals("newblob1.txt")) {
                     return HttpUtils.fromHexString("03");
                  }
                  return null;
               }
            };
         }
      });
      dateService = injector.getInstance(DateService.class);
      assert dateService != null;
   }

   @SuppressWarnings("unchecked")
   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/test_list_blobs.xml");
      ListBlobsResponse list = new TreeSetListBlobsResponse(
               URI.create("http://myaccount.blob.core.windows.net/mycontainer"),
               ImmutableSortedSet
                        .of(
                                 new BlobMetadata(
                                          "blob1.txt",
                                          URI
                                                   .create("http://myaccount.blob.core.windows.net/mycontainer/blob1.txt"),
                                          dateService
                                                   .rfc822DateParse("Thu, 18 Sep 2008 18:41:57 GMT"),
                                          "0x8CAE7D55D050B8B", 8, "text/plain; charset=UTF-8",
                                          HttpUtils.fromHexString("01"), null, null),
                                 new BlobMetadata(
                                          "blob2.txt",
                                          URI
                                                   .create("http://myaccount.blob.core.windows.net/mycontainer/blob2.txt"),
                                          dateService
                                                   .rfc822DateParse("Thu, 18 Sep 2008 18:41:57 GMT"),
                                          "0x8CAE7D55CF6C339", 14, "text/plain; charset=UTF-8",
                                          HttpUtils.fromHexString("02"), null, null),
                                 new BlobMetadata(
                                          "newblob1.txt",
                                          URI
                                                   .create("http://myaccount.blob.core.windows.net/mycontainer/newblob1.txt"),
                                          dateService
                                                   .rfc822DateParse("Thu, 18 Sep 2008 18:41:57 GMT"),
                                          "0x8CAE7D55CF6C339", 25, "text/plain; charset=UTF-8",
                                          HttpUtils.fromHexString("03"), null, null)

                        ), null, null, 4, "newblob2.txt", null, "myfolder/");

      BoundedSortedSet<ListBlobsResponse> result = (BoundedSortedSet<ListBlobsResponse>) factory
               .create(injector.getInstance(ContainerNameEnumerationResultsHandler.class))
               .parse(is);

      assertEquals(result, list);
   }
}
