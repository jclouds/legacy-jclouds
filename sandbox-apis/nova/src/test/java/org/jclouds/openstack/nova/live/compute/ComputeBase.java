package org.jclouds.openstack.nova.live.compute;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.ssh.SshException;
import org.jclouds.ssh.jsch.JschSshClient;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.BeforeTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Sets.filter;
import static org.jclouds.compute.predicates.NodePredicates.*;
import static org.jclouds.openstack.nova.live.PropertyHelper.*;
import static org.testng.Assert.assertEquals;

/**
 * @author Victor Galkin
 */
public class ComputeBase {
   protected ComputeServiceContext context;
   protected ComputeService computeService;

   protected String provider = "nova";


   protected Map<String, String> keyPair;
   protected Properties overrides;

   @BeforeTest
   public void before() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      Properties properties = setupProperties(this.getClass());
      setupOverrides(properties);
      overrides = properties;
      keyPair = setupKeyPair(properties);
      initializeContextAndComputeService(properties);

   }


   private RetryablePredicate<IPSocket> buildSocket() {
      SocketOpen socketOpen = Guice.createInjector(getSshModule()).getInstance(SocketOpen.class);
      return new RetryablePredicate<IPSocket>(socketOpen, 60, 1, TimeUnit.SECONDS);
   }

   private JschSshClientModule getSshModule() {
      return new JschSshClientModule();
   }

   protected TemplateBuilder getDefaultTemplateBuilder() {
      return computeService.templateBuilder().imageId("95").options(getDefaultTemplateOptions());
   }

   private TemplateOptions getDefaultTemplateOptions() {
      return TemplateOptions.Builder.blockUntilRunning(false);
      //.installPrivateKey(Payloads.newStringPayload(keyPair.get("private")));
   }

   protected NodeMetadata getDefaultNodeImmediately(String group) throws RunNodesException {
      for (ComputeMetadata node : computeService.listNodes()) {
         if (((NodeMetadata) node).getGroup() != null)
            if (((NodeMetadata) node).getGroup().equals(group))
               if (((NodeMetadata) node).getState().equals(NodeState.PENDING)
                     || ((NodeMetadata) node).getState().equals(NodeState.RUNNING)) return (NodeMetadata) node;
      }
      return createDefaultNode(group);
   }

   protected NodeMetadata createDefaultNode(TemplateOptions options, String group) throws RunNodesException {
      return computeService.createNodesInGroup(group, 1, getDefaultTemplateBuilder().options(options).build())
            .iterator().next();
   }

   protected NodeMetadata createDefaultNode(String group) throws RunNodesException {
      return createDefaultNode(getDefaultTemplateOptions(), group);
   }


   protected void initializeContextAndComputeService(Properties properties) throws IOException {
      if (context != null)
         context.close();
      context = new ComputeServiceContextFactory().createContext(provider, ImmutableSet.of(
            new SLF4JLoggingModule(), getSshModule()), properties);
      computeService = context.getComputeService();
   }

   protected String awaitForPublicAddressAssigned(String nodeId) throws InterruptedException {
      while (true) {
         Set<String> addresses = computeService.getNodeMetadata(nodeId).getPublicAddresses();
         System.out.println(addresses);
         System.out.println(computeService.getNodeMetadata(nodeId).getState());
         if (addresses != null)
            if (!addresses.isEmpty()) return addresses.iterator().next();
         Thread.sleep(1000);
      }
   }

   protected Set<? extends NodeMetadata> getFreshNodes(String group) {
      return filter(computeService.listNodesDetailsMatching(all()), and(inGroup(group), not(TERMINATED)));
   }

   protected void awaitForSshPort(String address, Credentials credentials) throws URISyntaxException {
      IPSocket socket = new IPSocket(address, 22);

      JschSshClient ssh = new JschSshClient(
            new BackoffLimitedRetryHandler(), socket, 10000, credentials.identity, null, credentials.credential.getBytes());
      while (true) {
         try {
            System.out.println("ping: " + socket);
            ssh.connect();
            return;
         } catch (SshException ignore) {
         }
      }
   }

   protected void assertLocationSameOrChild(Location test, Location expected) {
      if (!test.equals(expected)) {
         assertEquals(test.getParent().getId(), expected.getId());
      } else {
         assertEquals(test, expected);
      }
   }

}
