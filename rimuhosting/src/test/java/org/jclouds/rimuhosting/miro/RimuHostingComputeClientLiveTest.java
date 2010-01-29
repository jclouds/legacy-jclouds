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
package org.jclouds.rimuhosting.miro;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.AddressReachable;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code TerremarkVCloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "rimuhosting.RimuHostingComputeClientLiveTest")
public class RimuHostingComputeClientLiveTest {
   RimuHostingComputeClient client;
   RimuHostingClient rhClient;

   private Long id;

   private InetAddress publicIp;
   private Predicate<InetAddress> addressTester;

   @Test
   public void testPowerOn() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      String imageId = "lenny";
      String serverName = "test.compute.jclouds.org";
      String planId = "MIRO1B";

      id = client.start(serverName, planId, imageId);
      Server server = rhClient.getServer(id);
      assertEquals(imageId, server.getImageId());
      assertEquals(serverName, server.getName());
      assertEquals(new Integer(160), server.getInstanceParameters().getRam());
   }

   @AfterTest
   void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      if (id != null)
         client.destroy(id);
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      Injector injector = new RimuHostingContextBuilder(new RimuHostingPropertiesBuilder(key)
               .relaxSSLHostname(true).build()).withModules(new Log4JLoggingModule(),
               new JschSshClientModule(), new AbstractModule() {

                  @Override
                  protected void configure() {
                  }

                  @SuppressWarnings("unused")
                  @Provides
                  private Predicate<InetAddress> addressTester(AddressReachable reachable) {
                     return new RetryablePredicate<InetAddress>(reachable, 60, 5, TimeUnit.SECONDS);
                  }

               }).buildInjector();
      client = injector.getInstance(RimuHostingComputeClient.class);
      rhClient = injector.getInstance(RimuHostingClient.class);
      addressTester = injector.getInstance(Key.get(new TypeLiteral<Predicate<InetAddress>>() {
      }));
   }

}