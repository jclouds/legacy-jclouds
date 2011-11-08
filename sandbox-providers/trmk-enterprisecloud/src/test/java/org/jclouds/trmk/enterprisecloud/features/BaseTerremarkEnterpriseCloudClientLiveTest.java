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
package org.jclouds.trmk.enterprisecloud.features;

import java.util.Properties;

import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.trmk.enterprisecloud.TerremarkEnterpriseCloudAsyncClient;
import org.jclouds.trmk.enterprisecloud.TerremarkEnterpriseCloudClient;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code TerremarkEnterpriseCloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseTerremarkEnterpriseCloudClientLiveTest {

   protected RestContext<TerremarkEnterpriseCloudClient, TerremarkEnterpriseCloudAsyncClient> context;
   protected Module module;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      // TODO organize this like other compute tests
      String identity = System.getProperty("test.trmk-enterprisecloud.identity", "readonly@terremark.com");
      String credential = System.getProperty("test.trmk-enterprisecloud.credential", "T3rr3m@rk");

      Properties props = new Properties();

      context = new RestContextFactory().createContext("trmk-enterprisecloud", identity, credential,
            ImmutableSet.<Module> of(new Log4JLoggingModule(), new SshjSshClientModule()), props);
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (context != null)
         context.close();
   }

}
