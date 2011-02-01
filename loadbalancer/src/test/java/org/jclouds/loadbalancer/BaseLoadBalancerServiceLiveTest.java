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

package org.jclouds.loadbalancer;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.ssh.SshClient;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true)
public abstract class BaseLoadBalancerServiceLiveTest {

   protected SshClient.Factory sshFactory;
   protected String group;

   protected RetryablePredicate<IPSocket> socketTester;
   protected Set<? extends NodeMetadata> nodes;
   protected Template template;
   protected Map<String, String> keyPair;
   protected LoadBalancerMetadata loadbalancer;

   protected String provider;
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;
   protected LoadBalancerServiceContext context;

   protected String computeProvider;
   protected String computeIdentity;
   protected String computeCredential;
   protected String computeEndpoint;
   protected String computeApiversion;
   protected ComputeServiceContext computeContext;

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = System.getProperty("test." + provider + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint");
      apiversion = System.getProperty("test." + provider + ".apiversion");
      computeProvider = checkNotNull(System.getProperty("test." + provider + ".compute.provider"), "test." + provider
            + ".compute.provider");
      computeIdentity = checkNotNull(System.getProperty("test." + provider + ".compute.identity"), "test." + provider + ".compute.identity");
      computeCredential = System.getProperty("test." + provider + ".compute.credential");
      computeEndpoint = System.getProperty("test." + provider + ".compute.endpoint");
      computeApiversion = System.getProperty("test." + provider + ".compute.apiversion");
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

   protected Properties setupComputeProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(computeProvider + ".identity", computeIdentity);
      if (computeCredential != null)
         overrides.setProperty(computeProvider + ".credential", computeCredential);
      if (computeEndpoint != null)
         overrides.setProperty(computeProvider + ".endpoint", computeEndpoint);
      if (computeApiversion != null)
         overrides.setProperty(computeProvider + ".apiversion", computeApiversion);
      return overrides;
   }

   @BeforeGroups(groups = { "integration", "live" })
   public void setupClient() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      setServiceDefaults();
      if (group == null)
         group = checkNotNull(provider, "provider");
      setupCredentials();
      initializeContext();
      initializeComputeContext();
      buildSocketTester();
   }

   public void setServiceDefaults() {

   }

   private void initializeContext() throws IOException {
      if (context != null)
         context.close();
      context = new LoadBalancerServiceContextFactory(getRestProperties()).createContext(provider,
            ImmutableSet.of(new Log4JLoggingModule()), setupProperties());
   }

   private void initializeComputeContext() throws IOException {
      if (computeContext != null)
         computeContext.close();
      computeContext = new ComputeServiceContextFactory(getRestProperties()).createContext(computeProvider,
            ImmutableSet.of(new Log4JLoggingModule(), getSshModule()), setupComputeProperties());
   }

   protected Properties getRestProperties() {
      return RestContextFactory.getPropertiesFromResource("/rest.properties");
   }

   protected void buildSocketTester() {
      SocketOpen socketOpen = Guice.createInjector(getSshModule()).getInstance(SocketOpen.class);
      socketTester = new RetryablePredicate<IPSocket>(socketOpen, 60, 1, TimeUnit.SECONDS);
   }

   abstract protected Module getSshModule();

   @BeforeGroups(groups = { "integration", "live" }, dependsOnMethods = "setupClient")
   public void createNodes() throws RunNodesException {
      try {
         nodes = computeContext.getComputeService().createNodesInGroup(group, 2);
      } catch (RunNodesException e) {
         nodes = e.getSuccessfulNodes();
         throw e;
      }
   }

   @Test(enabled = true)
   public void testLoadBalanceNodesMatching() throws Exception {

      // create load balancers
      loadbalancer = context.getLoadBalancerService().createLoadBalancerInLocation(null, group, "HTTP", 80, 80, nodes);
      assertNotNull(loadbalancer);
      validateNodesInLoadBalancer();

   }

   // TODO create a LoadBalancerService method for this.
   protected abstract void validateNodesInLoadBalancer();

   @Test(enabled = true, dependsOnMethods = "testLoadBalanceNodesMatching")
   public void testDestroyLoadBalancers() throws Exception {
      context.getLoadBalancerService().destroyLoadBalancer(loadbalancer.getId());
   }

   @AfterTest
   protected void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      if (loadbalancer != null) {
         context.getLoadBalancerService().destroyLoadBalancer(loadbalancer.getId());
      }
      if (nodes != null) {
         computeContext.getComputeService().destroyNodesMatching(NodePredicates.inGroup(group));
      }
      computeContext.close();
      context.close();
   }

}
