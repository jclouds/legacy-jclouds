/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.loadbalancer;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.reflect.Reflection2.typeToken;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertNotNull;

import java.util.Properties;
import java.util.Set;

import org.jclouds.Constants;
import org.jclouds.ContextBuilder;
import org.jclouds.apis.BaseViewLiveTest;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.TemplateBuilderSpec;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.domain.LoginCredentials.Builder;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.predicates.SocketOpen;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.net.HostAndPort;
import com.google.common.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true)
public abstract class BaseLoadBalancerServiceLiveTest extends BaseViewLiveTest<LoadBalancerServiceContext> {

   protected TemplateBuilderSpec template;
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
      String spec = setIfTestSystemPropertyPresent(overrides, provider + ".compute.template");
      if (spec != null) {
         template = TemplateBuilderSpec.parse(spec);
         if (template.getLoginUser() != null) {
            Iterable<String> userPass = Splitter.on(':').split(template.getLoginUser());
            Builder loginCredentialsBuilder = LoginCredentials.builder();
            loginCredentialsBuilder.user(Iterables.get(userPass, 0));
            if (Iterables.size(userPass) == 2)
               loginCredentialsBuilder.password(Iterables.get(userPass, 1));
            if (template.getAuthenticateSudo() != null)
               loginCredentialsBuilder.authenticateSudo(template.getAuthenticateSudo());
            loginCredentials = loginCredentialsBuilder.build();
         }
      }
      return overrides;
   }

   protected String group;

   protected Predicate<HostAndPort> socketTester;
   protected Set<? extends NodeMetadata> nodes;
   protected LoadBalancerMetadata loadbalancer;

   protected String computeProvider;
   protected String computeIdentity;
   protected String computeCredential;
   protected String computeEndpoint;
   protected String computeApiversion;
   protected String computeBuildversion;
   protected ComputeServiceContext computeContext;

   @BeforeClass(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      if (group == null)
         group = checkNotNull(provider, "provider");
      // groups need to work with hyphens in them, so let's make sure there is
      // one, without making it the first or last character
      if (group.indexOf('-') == -1)
         group = new StringBuilder(group).insert(1, "-").toString();
      initializeComputeContext();
      buildSocketTester();
   }

   protected void initializeComputeContext() {
      if (computeContext != null)
         computeContext.close();
      Properties overrides = setupComputeProperties();
      ContextBuilder builder = ContextBuilder.newBuilder(computeProvider)
               .credentials(computeIdentity, computeCredential).overrides(overrides).modules(setupModules());
      if (computeApiversion != null)
         builder.apiVersion(computeApiversion);
      if (computeBuildversion != null)
         builder.buildVersion(computeBuildversion);

      computeContext = builder.buildView(ComputeServiceContext.class);
   }

   protected void buildSocketTester() {
      SocketOpen socketOpen = Guice.createInjector(getSshModule()).getInstance(SocketOpen.class);
      socketTester = retry(socketOpen, 60, 1, SECONDS);
   }

   abstract protected Module getSshModule();

   @BeforeClass(groups = { "integration", "live" }, dependsOnMethods = "setupContext")
   public void createNodes() throws RunNodesException {
      try {
         TemplateBuilder builder = computeContext.getComputeService().templateBuilder();
         if (template != null)
            builder.from(template);
         nodes = computeContext.getComputeService().createNodesInGroup(group, 2, builder.build());
      } catch (RunNodesException e) {
         nodes = e.getSuccessfulNodes();
         throw e;
      }
   }

   @Test(enabled = true)
   public void testLoadBalanceNodesMatching() throws Exception {

      // create load balancers
      loadbalancer = view.getLoadBalancerService().createLoadBalancerInLocation(null, group, "HTTP", 80, 80, nodes);
      assertNotNull(loadbalancer);
      validateNodesInLoadBalancer();

   }

   // TODO create a LoadBalancerService method for this.
   protected abstract void validateNodesInLoadBalancer();

   @Test(enabled = true, dependsOnMethods = "testLoadBalanceNodesMatching")
   public void testDestroyLoadBalancers() throws Exception {
      view.getLoadBalancerService().destroyLoadBalancer(loadbalancer.getId());
   }
   
   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDownContext() {
      if (loadbalancer != null) {
         view.getLoadBalancerService().destroyLoadBalancer(loadbalancer.getId());
      }
      if (nodes != null) {
         computeContext.getComputeService().destroyNodesMatching(NodePredicates.inGroup(group));
      }
      computeContext.close();
      super.tearDownContext();
   }

   @Override
   protected TypeToken<LoadBalancerServiceContext> viewType() {
      return typeToken(LoadBalancerServiceContext.class);
   }
}
