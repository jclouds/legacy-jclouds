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
package org.jclouds.http;

import java.util.Properties;

import org.testng.annotations.Test;

/**
 * Tests behavior of modules configured in HttpPropertiesBuilder<String>
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "rest.HttpPropertiesBuilderTest")
public class HttpPropertiesBuilderTest {

   public void testBuilder() {
      int httpMaxRetries = 9875;
      int poolIoWorkerThreads = 2727;
      int poolMaxClientReuse = 3932;
      int poolMaxClients = 3382;
      int poolMaxSessionFailures = 857;

      HttpPropertiesBuilder builder = new HttpPropertiesBuilder(new Properties());
      builder.withHttpMaxRetries(httpMaxRetries);

      builder.withPoolIoWorkerThreads(poolIoWorkerThreads);
      builder.withPoolMaxClientReuse(poolMaxClientReuse);
      builder.withPoolMaxClients(poolMaxClients);
      builder.withPoolMaxSessionFailures(poolMaxSessionFailures);
   }
}
