/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.trmk.vcloud_0_8.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.ssh.SshClient.Factory;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudClient;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, singleThreaded = true)
public abstract class BaseTerremarkClientLiveTest <T extends TerremarkVCloudClient>{
   protected String prefix = System.getProperty("user.name");

   protected ComputeService client;

   protected String provider = "trmk-ecloud";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   protected RetryablePredicate<IPSocket> socketTester;
   protected Factory sshFactory;

   @SuppressWarnings("unchecked")
   protected T getApi() {
      return (T) client.getContext().getProviderSpecificContext().getApi();
   }

   @BeforeClass
   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = System.getProperty("test." + provider + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint");
      apiversion = System.getProperty("test." + provider + ".apiversion");
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(provider + ".identity", identity);
      if (credential != null)
         overrides.setProperty(provider + ".credential", credential);
      if (endpoint != null)
         overrides.setProperty(provider + ".endpoint", endpoint);
      if (apiversion != null)
         overrides.setProperty(provider + ".apiversion", apiversion);
      return overrides;
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();
      client = new ComputeServiceContextFactory().createContext(provider,
               ImmutableSet.<Module> of(new Log4JLoggingModule()), overrides).getComputeService();
      socketTester = new RetryablePredicate<IPSocket>(new InetSocketAddressConnect(), 300, 1, TimeUnit.SECONDS);
      sshFactory = Guice.createInjector(getSshModule()).getInstance(Factory.class);
   }

   protected Properties setupRestProperties() {
      return RestContextFactory.getPropertiesFromResource("/rest.properties");
   }

   protected JschSshClientModule getSshModule() {
      return new JschSshClientModule();
   }

   @AfterGroups(groups = { "live" })
   protected void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      client.getContext().close();
   }
}