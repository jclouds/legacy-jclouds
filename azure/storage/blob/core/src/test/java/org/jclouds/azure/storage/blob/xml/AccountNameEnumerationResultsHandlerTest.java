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
package org.jclouds.azure.storage.blob.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.SortedSet;

import org.jclouds.azure.storage.blob.domain.ListableContainerProperties;
import org.jclouds.azure.storage.blob.domain.internal.ListableContainerPropertiesImpl;
import org.jclouds.azure.storage.domain.BoundedSortedSet;
import org.jclouds.azure.storage.domain.internal.BoundedTreeSet;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.util.DateService;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

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

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/test_list_containers.xml");
      SortedSet<ListableContainerProperties> contents = Sets.newTreeSet();
      contents.add(new ListableContainerPropertiesImpl(URI
               .create("http://myaccount.blob.core.windows.net/audio"), dateService
               .rfc822DateParse("Wed, 13 Aug 2008 20:39:39 GMT"), "0x8CACB9BD7C6B1B2"));
      contents.add(new ListableContainerPropertiesImpl(URI
               .create("http://myaccount.blob.core.windows.net/images"), dateService
               .rfc822DateParse("Wed, 14 Aug 2008 20:39:39 GMT"), "0x8CACB9BD7C1EEEC"));
      contents.add(new ListableContainerPropertiesImpl(URI
               .create("http://myaccount.blob.core.windows.net/textfiles"), dateService
               .rfc822DateParse("Wed, 15 Aug 2008 20:39:39 GMT"), "0x8CACB9BD7BACAC3"));
      BoundedSortedSet<ListableContainerProperties> list = new BoundedTreeSet<ListableContainerProperties>(
               contents, URI.create("http://myaccount.blob.core.windows.net/"), null, null, 3,
               "video");

      BoundedSortedSet<ListableContainerProperties> result = (BoundedSortedSet<ListableContainerProperties>) factory
               .create(injector.getInstance(AccountNameEnumerationResultsHandler.class)).parse(is);

      assertEquals(result, list);
   }

   public void testApplyInputStreamWithOptions() {
      SortedSet<ListableContainerProperties> contents = Sets.newTreeSet();
      contents.add(new ListableContainerPropertiesImpl(URI
               .create("http://myaccount.blob.core.windows.net/audio"), dateService
               .rfc822DateParse("Wed, 13 Aug 2008 20:39:39 GMT"), "0x8CACB9BD7C6B1B2"));
      contents.add(new ListableContainerPropertiesImpl(URI
               .create("http://myaccount.blob.core.windows.net/images"), dateService
               .rfc822DateParse("Wed, 14 Aug 2008 20:39:39 GMT"), "0x8CACB9BD7C1EEEC"));
      contents.add(new ListableContainerPropertiesImpl(URI
               .create("http://myaccount.blob.core.windows.net/textfiles"), dateService
               .rfc822DateParse("Wed, 15 Aug 2008 20:39:39 GMT"), "0x8CACB9BD7BACAC3"));
      InputStream is = getClass().getResourceAsStream("/test_list_containers_options.xml");
      BoundedSortedSet<ListableContainerProperties> list = new BoundedTreeSet<ListableContainerProperties>(
               contents, URI.create("http://myaccount.blob.core.windows.net"), "prefix", "marker",
               1, "video");
      BoundedSortedSet<ListableContainerProperties> result = (BoundedSortedSet<ListableContainerProperties>) factory
               .create(injector.getInstance(AccountNameEnumerationResultsHandler.class)).parse(is);
      assertEquals(result, list);
   }
}
