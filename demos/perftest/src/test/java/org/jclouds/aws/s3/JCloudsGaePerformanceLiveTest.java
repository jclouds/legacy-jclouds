/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.aws.s3;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.gae.config.GoogleAppEngineConfigurationModule;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.s3.S3AsyncClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalURLFetchServiceTestConfig;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * This test is disabled due to timeout limitations in the google app engine sdk
 * 
 * @author Adrian Cole
 */
@Test(enabled = false, sequential = true, groups = { "disabled" })
public class JCloudsGaePerformanceLiveTest extends BaseJCloudsPerformanceLiveTest {

   @Override
   @Test(enabled = false)
   public void testPutBytesParallel() throws InterruptedException, ExecutionException, TimeoutException {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutBytesSerial() throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutFileParallel() throws InterruptedException, ExecutionException, TimeoutException {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutFileSerial() throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutInputStreamParallel() throws InterruptedException, ExecutionException, TimeoutException {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutInputStreamSerial() throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutStringParallel() throws InterruptedException, ExecutionException, TimeoutException {
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
      // TODO sdk 1.2.3 should give the ability to set a higher timeout then 5
      // seconds allowing this
      // to be removed
      loopCount = 5;
   }

   @Override
   protected Future<?> putByteArray(String bucket, String key, byte[] data, String contentType) {
      setupApiProxy();
      return super.putByteArray(bucket, key, data, contentType);
   }

   @Override
   protected Future<?> putFile(String bucket, String key, File data, String contentType) {
      setupApiProxy();
      return super.putFile(bucket, key, data, contentType);
   }

   @Override
   protected Future<?> putInputStream(String bucket, String key, InputStream data, String contentType) {
      setupApiProxy();
      return super.putInputStream(bucket, key, data, contentType);
   }

   @Override
   protected Future<?> putString(String bucket, String key, String data, String contentType) {
      setupApiProxy();
      return super.putString(bucket, key, data, contentType);
   }

   @BeforeMethod
   void setupApiProxy() {
      new LocalServiceTestHelper(new LocalURLFetchServiceTestConfig()).setUp();
   }

   private BlobStoreContext perfContext;

   @BeforeClass(groups = { "live" })
   void setup() {
      String accesskeyid = System.getProperty("jclouds.test.identity");
      String secretkey = System.getProperty("jclouds.test.credential");
      Properties overrides = new Properties();
      String contextName = "gae";
      overrideWithSysPropertiesAndPrint(overrides, contextName);
      context = new BlobStoreContextFactory().createContext("aws-s3", accesskeyid, secretkey, ImmutableSet.of(
            new NullLoggingModule(), new GoogleAppEngineConfigurationModule()), overrides);
   }

   @AfterClass(groups = { "live" })
   void tearDown() {
      if (perfContext != null)
         perfContext.close();
   }

   @Override
   public S3AsyncClient getApi() {
      return (S3AsyncClient) perfContext.getProviderSpecificContext().getAsyncApi();
   }
}