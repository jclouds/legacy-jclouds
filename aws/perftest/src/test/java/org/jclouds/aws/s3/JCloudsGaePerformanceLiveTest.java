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
package org.jclouds.aws.s3;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.gae.config.GaeHttpCommandExecutorServiceModule;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.v6.Maps;

import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;
import com.google.inject.Module;

/**
 * 
 * This test is disabled due to timeout limitations in the google app engine sdk
 * 
 * @author Adrian Cole
 */
@Test(enabled = false, sequential = true, testName = "perftest.JCloudsGaePerformanceLiveTest", groups = { "disabled" })
public class JCloudsGaePerformanceLiveTest extends BaseJCloudsPerformanceLiveTest {

   @Override
   @Test(enabled = false)
   public void testPutBytesParallel() throws InterruptedException, ExecutionException,
            TimeoutException {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutBytesParallelEU() throws InterruptedException, ExecutionException,
            TimeoutException {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutBytesSerial() throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutBytesSerialEU() throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutFileParallel() throws InterruptedException, ExecutionException,
            TimeoutException {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutFileSerial() throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutInputStreamParallel() throws InterruptedException, ExecutionException,
            TimeoutException {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutInputStreamSerial() throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutStringParallel() throws InterruptedException, ExecutionException,
            TimeoutException {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutStringSerial() throws Exception {
      throw new UnsupportedOperationException();
   }

   public JCloudsGaePerformanceLiveTest() {
      super();
      // otherwise, we'll get timeout errors
      // TODO sdk 1.2.3 should give the ability to set a higher timeout then 5 seconds allowing this
      // to be removed
      loopCount = 5;
   }

   @Override
   protected boolean putByteArray(String bucket, String key, byte[] data, String contentType)
            throws Exception {
      setupApiProxy();
      return super.putByteArray(bucket, key, data, contentType);
   }

   @Override
   protected boolean putFile(String bucket, String key, File data, String contentType)
            throws Exception {
      setupApiProxy();
      return super.putFile(bucket, key, data, contentType);
   }

   @Override
   protected boolean putInputStream(String bucket, String key, InputStream data, String contentType)
            throws Exception {
      setupApiProxy();
      return super.putInputStream(bucket, key, data, contentType);
   }

   @Override
   protected boolean putString(String bucket, String key, String data, String contentType)
            throws Exception {
      setupApiProxy();
      return super.putString(bucket, key, data, contentType);
   }

   @BeforeMethod
   void setupApiProxy() {
      ApiProxy.setEnvironmentForCurrentThread(new TestEnvironment());
      ApiProxy.setDelegate(new ApiProxyLocalImpl(new File(".")) {
      });
   }

   class TestEnvironment implements ApiProxy.Environment {
      public String getAppId() {
         return "Unit Tests";
      }

      public String getVersionId() {
         return "1.0";
      }

      public void setDefaultNamespace(String s) {
      }

      public String getRequestNamespace() {
         return null;
      }

      public String getDefaultNamespace() {
         return null;
      }

      public String getAuthDomain() {
         return null;
      }

      public boolean isLoggedIn() {
         return false;
      }

      public String getEmail() {
         return null;
      }

      public boolean isAdmin() {
         return false;
      }

      public Map<String, Object> getAttributes() {
         return Maps.newHashMap();
      }
   }

   @Override
   protected Module createHttpModule() {
      return new GaeHttpCommandExecutorServiceModule();
   }
}