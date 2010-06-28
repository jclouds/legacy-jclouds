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
package org.jclouds.vcloud.hostingdotcom.compute;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Properties;

import org.jclouds.compute.domain.OsFamily;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.vcloud.compute.VCloudComputeClientLiveTest;
import org.jclouds.vcloud.hostingdotcom.HostingDotComVCloudClient;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.ImmutableMap;

/**
 * Tests behavior of {@code HostingDotComVCloudComputeClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, sequential = true, testName = "vcloud.HostingDotComVCloudComputeClientLiveTest")
public class HostingDotComVCloudComputeClientLiveTest extends VCloudComputeClientLiveTest {

   @BeforeGroups(groups = { "live" })
   @Override
   public void setupClient() {
      String identity = checkNotNull(System.getProperty("jclouds.test.identity"), "jclouds.test.identity");
      String credential = checkNotNull(System.getProperty("jclouds.test.credential"), "jclouds.test.credential");
      Injector injector = new RestContextFactory().createContextBuilder("hostingdotcom", identity,
               credential, ImmutableSet.<Module> of(new Log4JLoggingModule()), new Properties())
               .buildInjector();

      computeClient = injector.getInstance(HostingDotComVCloudComputeClient.class);
      client = injector.getInstance(HostingDotComVCloudClient.class);
      addressTester = injector.getInstance(Key.get(new TypeLiteral<Predicate<String>>() {
      }));
      expectationMap = ImmutableMap.<OsFamily, Expectation> builder().put(OsFamily.CENTOS,
               new Expectation(4194304 / 2 * 10, "Red Hat Enterprise Linux 5 (64-bit)")).build();
      service = "vcloudtest";
      templateId = "3";
   }

}
