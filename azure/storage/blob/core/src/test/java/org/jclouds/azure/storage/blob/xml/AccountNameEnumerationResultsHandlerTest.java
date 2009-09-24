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

import org.jclouds.azure.storage.blob.domain.ContainerMetadata;
import org.jclouds.azure.storage.domain.ArrayBoundedList;
import org.jclouds.azure.storage.domain.BoundedList;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.util.DateService;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ParseFlavorListFromGsonResponseTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "azureblob.AccountNameEnumerationResultsHandlerTest")
public class AccountNameEnumerationResultsHandlerTest extends BaseHandlerTest {

   private DateService dateService;

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
      dateService = injector.getInstance(DateService.class);
      assert dateService != null;
   }

   @SuppressWarnings("unchecked")
   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/test_list_containers.xml");
      BoundedList<ContainerMetadata> list = new ArrayBoundedList<ContainerMetadata>(ImmutableList
               .of(new ContainerMetadata(
                        URI.create("http://myaccount.blob.core.windows.net/audio"), dateService
                                 .rfc822DateParse("Wed, 13 Aug 2008 20:39:39 GMT"), HttpUtils
                                 .fromHexString("0x8CACB9BD7C6B1B2")), new ContainerMetadata(URI
                        .create("http://myaccount.blob.core.windows.net/images"), dateService
                        .rfc822DateParse("Wed, 14 Aug 2008 20:39:39 GMT"), HttpUtils
                        .fromHexString("0x8CACB9BD7C1EEEC")), new ContainerMetadata(URI
                        .create("http://myaccount.blob.core.windows.net/textfiles"), dateService
                        .rfc822DateParse("Wed, 15 Aug 2008 20:39:39 GMT"), HttpUtils
                        .fromHexString("0x8CACB9BD7BACAC3"))

               ), null, null, 3, "video");

      BoundedList<ContainerMetadata> result = (BoundedList<ContainerMetadata>) factory.create(
               injector.getInstance(AccountNameEnumerationResultsHandler.class)).parse(is);

      assertEquals(result, list);
   }

   @SuppressWarnings("unchecked")
   public void testApplyInputStreamWithOptions() {
      InputStream is = getClass().getResourceAsStream("/test_list_containers_options.xml");
      BoundedList<ContainerMetadata> list = new ArrayBoundedList<ContainerMetadata>(ImmutableList
               .of(new ContainerMetadata(
                        URI.create("http://myaccount.blob.core.windows.net/audio"), dateService
                                 .rfc822DateParse("Wed, 13 Aug 2008 20:39:39 GMT"), HttpUtils
                                 .fromHexString("0x8CACB9BD7C6B1B2")), new ContainerMetadata(URI
                        .create("http://myaccount.blob.core.windows.net/images"), dateService
                        .rfc822DateParse("Wed, 14 Aug 2008 20:39:39 GMT"), HttpUtils
                        .fromHexString("0x8CACB9BD7C1EEEC")), new ContainerMetadata(URI
                        .create("http://myaccount.blob.core.windows.net/textfiles"), dateService
                        .rfc822DateParse("Wed, 15 Aug 2008 20:39:39 GMT"), HttpUtils
                        .fromHexString("0x8CACB9BD7BACAC3"))

               ), "prefix", "marker", 1, "video");
      BoundedList<ContainerMetadata> result = (BoundedList<ContainerMetadata>) factory.create(
               injector.getInstance(AccountNameEnumerationResultsHandler.class)).parse(is);
      assertEquals(result, list);
   }
}
