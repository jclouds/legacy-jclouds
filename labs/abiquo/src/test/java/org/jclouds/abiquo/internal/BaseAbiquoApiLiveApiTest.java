/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.internal;

import org.jclouds.abiquo.environment.CloudTestEnvironment;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 * Base class for live and domain tests.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "api", testName = "BaseAbiquoApiLiveApiTest", singleThreaded = true)
public abstract class BaseAbiquoApiLiveApiTest extends BaseAbiquoLiveApiTest {
   /** The test environment. */
   protected static CloudTestEnvironment env;

   @Override
   @BeforeSuite(groups = "api")
   public void setupContext() {
      super.setupContext();
      setupEnvironment();
   }

   // @BeforeSuite(groups = "ucs", dependsOnMethods = "setupContext")
   protected void setupUcsEnvironment() throws Exception {
      if (env != null) {
         env.createUcsRack();
      }
   }

   @Override
   @AfterSuite(groups = "api")
   protected void tearDownContext() {
      try {
         tearDownEnvironment();
      } finally {
         // Make sure we close the context
         super.tearDownContext();
      }
   }

   protected void setupEnvironment() {
      if (env == null) {
         try {
            env = new CloudTestEnvironment(view);
            env.setup();
         } catch (Exception ex) {
            super.tearDownContext(); // Make sure we close the context setup
                                     // fails
            throw new RuntimeException("Could not create environment", ex);
         }
      }
   }

   protected void tearDownEnvironment() {
      if (env != null) {
         try {
            env.tearDown();

            // Wait a bit before closing context, to avoid executor shutdown
            // while
            // there are still open threads
            Thread.sleep(1000L);
         } catch (Exception ex) {
            throw new RuntimeException("Could not tear down environment", ex);
         }
      }
   }
}
