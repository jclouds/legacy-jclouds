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
package org.jclouds.http.httpnio.pool;

import static org.jclouds.Constants.PROPERTY_IO_WORKER_THREADS;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;

import java.net.MalformedURLException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.http.BaseHttpCommandExecutorServiceIntegrationTest;
import org.jclouds.http.httpnio.config.NioTransformingHttpCommandExecutorServiceModule;
import org.testng.annotations.Test;

import com.google.inject.Module;

/**
 * Tests for {@link HttpNioConnectionPoolListenableFutureCommandClient}.
 * 
 * @author Adrian Cole
 */
@Test(threadPoolSize = 10, sequential = true)
public class NioTransformingHttpCommandExecutorServiceTest extends
         BaseHttpCommandExecutorServiceIntegrationTest {

   @Override
   @Test(enabled = false)
   public void testPostAsInputStream() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      // TODO when these fail, we hang
   }

   @Override
   @Test(enabled = false)
   public void testPostResults() {
      // see above
   }

   protected Module createConnectionModule() {
      return new NioTransformingHttpCommandExecutorServiceModule();
   }

   @Override
   protected void addConnectionProperties(Properties props) {
      props.setProperty(PROPERTY_MAX_CONNECTIONS_PER_HOST, 12 + "");
      props.setProperty(PROPERTY_IO_WORKER_THREADS, 3 + "");
      props.setProperty(PROPERTY_USER_THREADS, 0 + "");
   }

   @Override
   @Test(enabled = false)
   public void testGetBigFile() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      // disabled since test data is too big
   }
}