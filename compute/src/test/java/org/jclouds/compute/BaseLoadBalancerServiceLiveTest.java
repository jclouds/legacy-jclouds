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

package org.jclouds.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.ssh.SshClient;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "compute.BaseLoadBalancerServiceTest")
public abstract class BaseLoadBalancerServiceLiveTest {
   @BeforeClass
   abstract public void setServiceDefaults();

   protected String provider;
   protected SshClient.Factory sshFactory;
   protected String tag;

   protected RetryablePredicate<IPSocket> socketTester;
   protected SortedSet<NodeMetadata> nodes;
   protected ComputeServiceContext context;
   protected ComputeService client;
   protected LoadBalancerService lbClient;
   protected String identity;
   protected String credential;
   protected Template template;
   protected Map<String, String> keyPair;
   protected Set<String> loadbalancers;

   @BeforeGroups(groups = { "live" })
   public void setupClient() throws InterruptedException, ExecutionException, TimeoutException,
            IOException, RunNodesException {
      if (tag == null)
         tag = checkNotNull(provider, "provider") + "lb";
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = checkNotNull(System.getProperty("test." + provider + ".credential"), "test." + provider + ".credential");

      initializeContextAndClient();

      Injector injector = Guice.createInjector(getSshModule());
      sshFactory = injector.getInstance(SshClient.Factory.class);
      SocketOpen socketOpen = injector.getInstance(SocketOpen.class);
      socketTester = new RetryablePredicate<IPSocket>(socketOpen, 60, 1, TimeUnit.SECONDS);
      injector.injectMembers(socketOpen); // add logger

      Template template = client.templateBuilder().build();

      nodes = Sets.newTreeSet(client.runNodesWithTag(tag, 2, template));
   }

   private void initializeContextAndClient() throws IOException {
      if (context != null)
         context.close();
      context = new ComputeServiceContextFactory().createContext(provider, identity, credential,
               ImmutableSet.of(new Log4JLoggingModule(), getSshModule()));
      client = context.getComputeService();
      lbClient = context.getLoadBalancerService();
   }

   abstract protected Module getSshModule();

   protected Template buildTemplate(TemplateBuilder templateBuilder) {
      return templateBuilder.build();
   }

   @Test(enabled = true)
   public void testLoadBalanceNodesMatching() throws Exception {

      // create load balancers
      loadbalancers = lbClient.loadBalanceNodesMatching(NodePredicates.withTag(tag), tag, "HTTP",
               80, 80);
      assertNotNull(loadbalancers);
      validateNodesInLoadBalancer();

   }

   // TODO create a LoadBalancerService method for this.
   protected abstract void validateNodesInLoadBalancer();

   @Test(enabled = true, dependsOnMethods = "testLoadBalanceNodesMatching")
   public void testDestroyLoadBalancers() throws Exception {
      for (String lb : loadbalancers) {
         lbClient.destroyLoadBalancer(lb);
      }
   }

   @AfterTest
   protected void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      if (nodes != null) {
         client.destroyNodesMatching(NodePredicates.withTag(tag));
         for (NodeMetadata node : Iterables.filter(client.listNodesDetailsMatching(NodePredicates
                  .all()), NodePredicates.withTag(tag))) {
            assert node.getState() == NodeState.TERMINATED : node;
         }
      }
      if (loadbalancers != null) {
         client.destroyNodesMatching(NodePredicates.withTag(tag));
         for (NodeMetadata node : Iterables.filter(client.listNodesDetailsMatching(NodePredicates
                  .all()), NodePredicates.withTag(tag))) {
            assert node.getState() == NodeState.TERMINATED : node;
         }
      }
      context.close();
   }

}
