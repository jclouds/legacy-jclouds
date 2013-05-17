/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.azureblob.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.Set;

import org.jclouds.azureblob.domain.BlobProperties;
import org.jclouds.azureblob.domain.BlobType;
import org.jclouds.azureblob.domain.LeaseStatus;
import org.jclouds.azureblob.domain.ListBlobsResponse;
import org.jclouds.azureblob.domain.internal.BlobPropertiesImpl;
import org.jclouds.azureblob.domain.internal.HashSetListBlobsResponse;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code ContainerNameEnumerationResultsHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ContainerNameEnumerationResultsHandlerTest")
public class ContainerNameEnumerationResultsHandlerTest extends BaseHandlerTest {
   private DateService dateService;

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
      dateService = injector.getInstance(DateService.class);
      assert dateService != null;
   }

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/test_list_blobs.xml");
      Set<BlobProperties> contents = ImmutableSet.<BlobProperties> of(
            new BlobPropertiesImpl(BlobType.BLOCK_BLOB, "blob1.txt", "mycontainer", URI
                  .create("http://myaccount.blob.core.windows.net/mycontainer/blob1.txt"), dateService
                  .rfc822DateParse("Thu, 18 Sep 2008 18:41:57 GMT"), "0x8CAE7D55D050B8B", 8,
                  "text/plain; charset=UTF-8", null, null, null, null, LeaseStatus.UNLOCKED, ImmutableMap
                        .<String, String> of()),
            new BlobPropertiesImpl(BlobType.BLOCK_BLOB, "blob2.txt", "mycontainer", URI
                  .create("http://myaccount.blob.core.windows.net/mycontainer/blob2.txt"), dateService
                  .rfc822DateParse("Thu, 18 Sep 2008 18:41:57 GMT"), "0x8CAE7D55CF6C339", 14,
                  "text/plain; charset=UTF-8", null, null, null, null, LeaseStatus.UNLOCKED, ImmutableMap
                        .<String, String> of()),
            new BlobPropertiesImpl(BlobType.PAGE_BLOB, "newblob1.txt", "mycontainer", URI
                  .create("http://myaccount.blob.core.windows.net/mycontainer/newblob1.txt"), dateService
                  .rfc822DateParse("Thu, 18 Sep 2008 18:41:57 GMT"), "0x8CAE7D55CF6C339", 25,
                  "text/plain; charset=UTF-8", null, null, null, null, LeaseStatus.UNLOCKED, ImmutableMap
                        .<String, String> of()));

      ListBlobsResponse list = new HashSetListBlobsResponse(contents,
            URI.create("http://myaccount.blob.core.windows.net/mycontainer"), "myfolder/", null, 4, "newblob2.txt",
            null, ImmutableSet.<String> of("myfolder/"));

      ListBlobsResponse result = factory.create(
            injector.getInstance(ContainerNameEnumerationResultsHandler.class)).parse(is);

      assertEquals(result, list);
   }

   public void testOptions() {
      InputStream is = getClass().getResourceAsStream("/test_list_blobs_options.xml");
      Set<BlobProperties> contents = ImmutableSet.<BlobProperties> of(new BlobPropertiesImpl(BlobType.BLOCK_BLOB, "a",
            "adriancole-blobstore3", URI.create("https://jclouds.blob.core.windows.net/adriancole-blobstore3/a"),
            dateService.rfc822DateParse("Sat, 30 Jan 2010 17:46:15 GMT"), "0x8CC6FEB41736428", 8,
            "application/octet-stream", null, null, null, null, LeaseStatus.UNLOCKED, ImmutableMap.<String, String> of()));

      ListBlobsResponse list = new HashSetListBlobsResponse(contents,
            URI.create("https://jclouds.blob.core.windows.net/adriancole-blobstore3"),

            null, null, 1, "2!68!MDAwMDA2IWFwcGxlcyEwMDAwMjghOTk5OS0xMi0zMVQyMzo1OTo1OS45OTk5OTk5WiE-", "/",
            Sets.<String> newTreeSet());

      ListBlobsResponse result = factory.create(
            injector.getInstance(ContainerNameEnumerationResultsHandler.class)).parse(is);

      assertEquals(result, list);
   }
}
