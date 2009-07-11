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
package org.jclouds.http;

import static org.testng.Assert.assertEquals;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.http.options.GetOptions;
import org.testng.annotations.Test;

/**
 * Tests for functionality all HttpCommandExecutorServices must express. These tests will operate
 * against an in-memory http engine, so as to ensure end-to-end functionality works.
 * 
 * @author Adrian Cole
 */
@Test(threadPoolSize = 10)
public abstract class BaseHttpCommandExecutorServiceTest extends BaseJettyTest {

   @Test(invocationCount = 50, timeOut = 5000)
   public void testRequestFilter() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      Future<String> get = client.downloadFilter("", "filterme");
      assertEquals(get.get(10, TimeUnit.SECONDS).trim(), "test");
   }

   @Test(invocationCount = 50, timeOut = 5000)
   public void testGetStringWithHeader() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      Future<String> get = client.download("", "test");
      assertEquals(get.get(10, TimeUnit.SECONDS).trim(), "test");
   }

   @Test(invocationCount = 50, timeOut = 5000)
   public void testGetString() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      Future<String> get = client.download("");
      assertEquals(get.get(10, TimeUnit.SECONDS).trim(), XML);
   }

   @Test(invocationCount = 50, timeOut = 5000)
   public void testGetStringSynch() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      // TODO why need trim?
      assertEquals(client.synch("").trim(), XML);
   }

   @Test(invocationCount = 50, timeOut = 5000)
   public void testGetException() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      Future<String> get = client.downloadException("", GetOptions.Builder.tail(1));
      assertEquals(get.get(10, TimeUnit.SECONDS).trim(), "foo");
   }

   @Test(invocationCount = 50, timeOut = 5000)
   public void testGetSynchException() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      assertEquals(client.synchException("", "").trim(), "foo");
   }

   @Test(invocationCount = 50, timeOut = 5000)
   public void testGetStringRedirect() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      Future<String> get = client.download("redirect");
      assertEquals(get.get(60, TimeUnit.SECONDS).trim(), XML2);
   }

   @Test(enabled = false, invocationCount = 50, timeOut = 5000)
   public void testGetStringPermanentRedirect() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      // GetString get = factory.createGetString("permanentredirect");
      // assert get != null;
      // client.submit(get);
      // assertEquals(get.get(10, TimeUnit.SECONDS).trim(), XML2);
      // TODO assert misses are only one, as permanent redirects paths should be remembered.
   }

   @Test(invocationCount = 50, timeOut = 5000)
   public void testPut() throws MalformedURLException, ExecutionException, InterruptedException,
            TimeoutException {
      Future<Boolean> put = client.upload("", "foo");
      assertEquals(put.get(10, TimeUnit.SECONDS), new Boolean(true));
   }

   @Test(invocationCount = 50, timeOut = 5000)
   public void testPutRedirect() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      Future<Boolean> put = client.upload("redirect", "foo");
      assertEquals(put.get(10, TimeUnit.SECONDS), new Boolean(true));
   }

   @Test(invocationCount = 50, timeOut = 5000)
   public void testHead() throws MalformedURLException, ExecutionException, InterruptedException,
            TimeoutException {
      assert client.exists("");
   }

   @Test(invocationCount = 50, timeOut = 5000)
   public void testGetAndParseSax() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      Future<String> getAndParseSax = client.downloadAndParse("");
      assertEquals(getAndParseSax.get(10, TimeUnit.SECONDS), "whoppers");
   }
}
