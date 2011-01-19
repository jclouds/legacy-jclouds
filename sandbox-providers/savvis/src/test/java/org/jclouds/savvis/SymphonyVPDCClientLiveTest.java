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

package org.jclouds.savvis;

import java.util.Properties;

import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.vcloud.VCloudExpressClientLiveTest;
import org.testng.annotations.BeforeGroups;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

public class SymphonyVPDCClientLiveTest extends VCloudExpressClientLiveTest {
   public SymphonyVPDCClientLiveTest() {
      provider = "savvis-symphony-vpdc";
   }

   @BeforeGroups(groups = { "live" })
   @Override
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();

      // TODO remove these lines when this is registered under jclouds-core/rest.properties
      Properties restProperties = new Properties();
      restProperties
            .setProperty("savvis-symphony-vpdc.contextbuilder", "org.jclouds.savvis.SymphonyVPDCContextBuilder");
      restProperties.setProperty("savvis-symphony-vpdc.propertiesbuilder",
            "org.jclouds.savvis.SymphonyVPDCPropertiesBuilder");

      context = new ComputeServiceContextFactory(restProperties).createContext(provider,
            ImmutableSet.<Module> of(new Log4JLoggingModule()), overrides).getProviderSpecificContext();

      System.out.println(context);
      
      connection = context.getApi();
      System.out.println(connection);
   }
}