/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.http.ning;

import static org.jclouds.Constants.PROPERTY_CONNECTION_TIMEOUT;
import static org.jclouds.Constants.PROPERTY_IO_WORKER_THREADS;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_CONTEXT;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST;
import static org.jclouds.Constants.PROPERTY_SO_TIMEOUT;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.http.BaseHttpCommandExecutorServiceIntegrationTest;
import org.jclouds.http.ning.config.NingHttpCommandExecutorServiceModule;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.inject.Module;

/**
 * Tests the functionality of the {@link ApacheHCHttpCommandExecutorService}
 * 
 * @author Adrian Cole
 */
public class NingHttpCommandExecutorServiceTest extends
         BaseHttpCommandExecutorServiceIntegrationTest {
   static {
      System.setProperty("http.conn-manager.timeout", 1000 + "");
   }

   @DataProvider(name = "gets")
   @Override
   // ning doesn't support spaces
   public Object[][] createData() {
      return new Object[][] { { "object" }, { "/path" }, { "unic₪de" }, { "qu?stion" } };
   }

   protected Module createConnectionModule() {
      return new NingHttpCommandExecutorServiceModule();
   }

   protected void addConnectionProperties(Properties props) {
      props.setProperty(PROPERTY_MAX_CONNECTIONS_PER_CONTEXT, 20 + "");
      props.setProperty(PROPERTY_MAX_CONNECTIONS_PER_HOST, 0 + "");
      props.setProperty(PROPERTY_CONNECTION_TIMEOUT, 100 + "");
      props.setProperty(PROPERTY_SO_TIMEOUT, 100 + "");
      props.setProperty(PROPERTY_IO_WORKER_THREADS, 3 + "");
      props.setProperty(PROPERTY_USER_THREADS, 0 + "");
   }

   // ning doesn't support spaces
   @Test(invocationCount = 1, expectedExceptions = RuntimeException.class)
   public void testSpaceInUri() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      assertEquals(client.synch("sp ace").trim(), XML);
   }

   // OOM
   @Test(enabled = false, invocationCount = 1, timeOut = 5000)
   public void testGetBigFile() throws ExecutionException, InterruptedException, TimeoutException,
            IOException {
   }

}