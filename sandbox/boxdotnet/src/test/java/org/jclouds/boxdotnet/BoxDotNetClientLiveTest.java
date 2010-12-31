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

package org.jclouds.boxdotnet;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertNotNull;

import java.util.Properties;

import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code BoxDotNetClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BoxDotNetClientLiveTest {

   private BoxDotNetClient connection;
   private RestContext<BoxDotNetClient, BoxDotNetAsyncClient> context;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String identity = checkNotNull(System.getProperty("jclouds.test.identity"), "jclouds.test.identity");
      String credential = checkNotNull(System.getProperty("jclouds.test.credential"), "jclouds.test.credential");

      Properties restProperties = new Properties();
      restProperties.setProperty("boxdotnet.contextbuilder", "org.jclouds.boxdotnet.BoxDotNetContextBuilder");
      restProperties.setProperty("boxdotnet.propertiesbuilder", "org.jclouds.boxdotnet.BoxDotNetPropertiesBuilder");

      context = new RestContextFactory(restProperties).createContext("boxdotnet", identity, credential,
            ImmutableSet.<Module> of(new Log4JLoggingModule()));

      connection = context.getApi();
   }

   @AfterGroups(groups = "live")
   void tearDown() {
      if (context != null)
         context.close();
   }

   @Test
   public void testList() throws Exception {
      String response = connection.list();
      assertNotNull(response);
   }

   @Test
   public void testGet() throws Exception {
      String response = connection.get(1l);
      assertNotNull(response);
   }

   /*
    * TODO: add tests for BoxDotNet interface methods
    */
}
