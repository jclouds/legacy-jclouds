/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.gae;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.gae.config.AsyncGoogleAppEngineConfigurationModule;
import org.jclouds.http.BaseHttpCommandExecutorServiceIntegrationTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalURLFetchServiceTestConfig;
import com.google.inject.Module;

/**
 * 
 * Integration test for the URLFetchService
 * 
 * @author Adrian Cole
 */
@Test(threadPoolSize = 10, groups = "integration", sequential = true)
public class AsyncGaeHttpCommandExecutorServiceIntegrationTest extends BaseHttpCommandExecutorServiceIntegrationTest {

   @Override
   @Test(invocationCount = 50, timeOut = 3000)
   public void testKillRobotSlowly() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      setupApiProxy();
      super.testKillRobotSlowly();
   }

   @Override
   @Test(invocationCount = 50, timeOut = 3000)
   public void testPostAsInputStream() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      setupApiProxy();
      super.testPostAsInputStream();
   }

   @Override
   @Test(dependsOnMethods = "testPostAsInputStream")
   public void testPostResults() {
      // GAE converts everything to byte arrays and so failures are not gonna
      // happen
      assertEquals(postFailures.get(), 0);
   }

   @Override
   @Test(invocationCount = 50, timeOut = 3000)
   public void testPostBinder() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      setupApiProxy();
      super.testPostBinder();
   }

   @BeforeMethod
   void setupApiProxy() {
      new LocalServiceTestHelper(new LocalURLFetchServiceTestConfig()).setUp();
   }

   @Override
   @Test(invocationCount = 50, timeOut = 3000)
   public void testGetAndParseSax() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      setupApiProxy();
      super.testGetAndParseSax();
   }

   @Override
   @Test(invocationCount = 50, timeOut = 3000)
   public void testGetString() throws MalformedURLException, ExecutionException, InterruptedException, TimeoutException {
      setupApiProxy();
      super.testGetString();
   }

   @Override
   @Test(invocationCount = 50, timeOut = 3000, dataProvider = "gets")
   public void testGetStringSynch(String path) throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      setupApiProxy();
      super.testGetStringSynch(path);
   }

   @Override
   @Test(invocationCount = 50, timeOut = 3000)
   public void testGetStringRedirect() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      setupApiProxy();
      super.testGetStringRedirect();
   }

   @Override
   @Test(invocationCount = 50, timeOut = 3000)
   public void testGetException() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      setupApiProxy();
      super.testGetException();
   }

   @Override
   @Test(invocationCount = 50, timeOut = 3000)
   public void testGetStringPermanentRedirect() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      setupApiProxy();
      super.testGetStringPermanentRedirect();
   }

   @Override
   @Test(invocationCount = 50, timeOut = 3000)
   public void testGetSynchException() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      setupApiProxy();
      super.testGetSynchException();
   }

   @Override
   @Test(invocationCount = 50, timeOut = 3000)
   public void testPost() throws MalformedURLException, ExecutionException, InterruptedException, TimeoutException {
      setupApiProxy();
      super.testPost();
   }

   @Override
   @Test(invocationCount = 50, timeOut = 3000)
   public void testPut() throws MalformedURLException, ExecutionException, InterruptedException, TimeoutException {
      setupApiProxy();
      super.testPut();
   }

   @Override
   @Test(invocationCount = 50, timeOut = 3000)
   public void testPutRedirect() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      setupApiProxy();
      super.testPutRedirect();
   }

   @Override
   @Test(invocationCount = 50, timeOut = 3000)
   public void testGetStringWithHeader() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      setupApiProxy();
      super.testGetStringWithHeader();
   }

   @Override
   @Test(invocationCount = 50, timeOut = 3000)
   public void testHead() throws MalformedURLException, ExecutionException, InterruptedException, TimeoutException {
      setupApiProxy();
      super.testHead();
   }

   @Override
   @Test(invocationCount = 50, timeOut = 3000)
   public void testRequestFilter() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      setupApiProxy();
      super.testRequestFilter();
   }

   protected Module createConnectionModule() {
      return new AsyncGoogleAppEngineConfigurationModule();
   }

   @Override
   protected void addConnectionProperties(Properties props) {
   }

   @Override
   @Test(enabled = false)
   public void testGetBigFile() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      // disabled since test data is too big
   }

   @Override
   @Test(enabled = false)
   public void testUploadBigFile() throws IOException {
      // disabled since test data is too big
   }

}