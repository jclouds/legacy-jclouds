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
package org.jclouds.loadbalancer;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertNotNull;

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
import org.jclouds.domain.LoginCredentials;
import org.jclouds.domain.LoginCredentials.Builder;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.internal.BaseContextLiveTest;
import org.jclouds.ssh.SshClient;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true)
public abstract class BaseLoadBalancerServiceLiveTest<S, A, C extends LoadBalancerServiceContext<S, A>> extends BaseContextLiveTest<C> {

   protected String imageId;
   protected String loginUser;
   protected String authenticateSudo;
   protected LoginCredentials loginCredentials = LoginCredentials.builder().user("root").build();
   
    protected Properties setupComputeProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      computeProvider = setIfTestSystemPropertyPresent(overrides, provider + ".compute.provider");
      computeIdentity = setIfTestSystemPropertyPresent(overrides, provider + ".compute.identity");
      computeCredential = setIfTestSystemPropertyPresent(overrides, provider + ".compute.credential");
      computeEndpoint = setIfTestSystemPropertyPresent(overrides, provider + ".compute.endpoint");
      computeApiversion = setIfTestSystemPropertyPresent(overrides, provider + ".compute.api-version");
      computeBuildversion = setIfTestSystemPropertyPresent(overrides, provider + ".compute.build-version");
      imageId = setIfTestSystemPropertyPresent(overrides, provider + ".compute.image-id");
      loginUser = setIfTestSystemPropertyPresent(overrides, provider + ".compute.image.login-user");
      authenticateSudo = setIfTestSystemPropertyPresent(overrides, provider + ".compute.image.authenticate-sudo");

      if (loginUser != null) {
         Iterable<String> userPass = Splitter.on(':').split(loginUser);
         Builder loginCredentialsBuilder = LoginCredentials.builder();
         loginCredentialsBuilder.user(Iterables.get(userPass, 0));
         if (Iterables.size(userPass) == 2)
            loginCredentialsBuilder.password(Iterables.get(userPass, 1));
         if (authenticateSudo != null)
            loginCredentialsBuilder.authenticateSudo(Boolean.valueOf(authenticateSudo));
         loginCredentials = loginCredentialsBuilder.build();
      }
      return overrides;
   }

   protected SshClient.Factory sshFactory;
   protected String group;

   protected RetryablePredicate<IPSocket> socketTester;
   protected Set<? extends NodeMetadata> nodes;
   protected Template template;
   protected Map<String, String> keyPair;
   protected LoadBalancerMetadata loadbalancer;

   protected LoadBalancerServiceContext<S, A> context;

   protected String computeProvider;
   protected String computeIdentity;
   protected String computeCredential;
   protected String computeEndpoint;
   protected String computeApiversion;
   protected String computeBuildversion;
   protected ComputeServiceContext<?, ?> computeContext;

   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      setServiceDefaults();
      if (group == null)
         group = checkNotNull(provider, "provider");
      // groups need to work with hyphens in them, so let's make sure there is
      // one!
      if (group.indexOf('-') == -1)
         group = group + "-";
      super.setupContext();
      initializeComputeContext();
      buildSocketTester();
   }

   public void setServiceDefaults() {

   }
   
   protected void initializeComputeContext() {
      if (computeContext != null)
         computeContext.close();
      computeContext = new ComputeServiceContextFactory().createContext(computeProvider, setupModules(),
            setupComputeProperties());
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
