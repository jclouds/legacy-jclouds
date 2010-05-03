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
package org.jclouds.vcloud.hostingdotcom;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.vcloud.VCloudClientLiveTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.inject.Injector;

/**
 * Tests behavior of {@code HostingDotComVCloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "vcloud.HostingDotComVCloudClientLiveTest")
public class HostingDotComVCloudClientLiveTest extends VCloudClientLiveTest {

   @BeforeGroups(groups = { "live" })
   @Override
   public void setupClient() {
      account = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      Injector injector = new HostingDotComVCloudContextBuilder("hostingdotcom",
               new HostingDotComVCloudPropertiesBuilder(account, key).build())
               .withModules(new Log4JLoggingModule(), new JschSshClientModule()).buildInjector();

      connection = injector.getInstance(HostingDotComVCloudClient.class);
   }

}
