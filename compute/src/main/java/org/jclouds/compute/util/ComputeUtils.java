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
package org.jclouds.compute.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.compute.util.ComputeServiceUtils.installNewCredentials;
import static org.jclouds.compute.util.ComputeServiceUtils.isKeyAuth;
import static org.jclouds.concurrent.ConcurrentUtils.awaitCompletion;
import static org.jclouds.concurrent.ConcurrentUtils.makeListenable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.callables.AuthorizeRSAPublicKey;
import org.jclouds.compute.callables.InstallRSAPrivateKey;
import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.ScriptStatusReturnsZero.CommandUsingClient;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.util.ComputeServiceUtils.SshCallable;
import org.jclouds.concurrent.ConcurrentUtils;
import org.jclouds.io.Payload;
import org.jclouds.logging.Logger;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.ssh.SshClient;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ComputeUtils {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   @Inject(optional = true)
   private SshClient.Factory sshFactory;
   protected final Predicate<CommandUsingClient> runScriptNotRunning;
   private final Predicate<IPSocket> socketTester;
   private final ExecutorService executor;
   protected final Predicate<NodeMetadata> nodeRunning;
   private final GetNodeMetadataStrategy getNode;
   private final Timeouts timeouts;

   @Inject
   public ComputeUtils(Predicate<IPSocket> socketTester,
         @Named("SCRIPT_COMPLETE") Predicate<CommandUsingClient> runScriptNotRunning, GetNodeMetadataStrategy getNode,
         Timeouts timeouts, @Named("NODE_RUNNING") Predicate<NodeMetadata> nodeRunning,
         @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.nodeRunning = nodeRunning;
      this.timeouts = timeouts;
      this.getNode = getNode;
      this.socketTester = socketTester;
      this.runScriptNotRunning = runScriptNotRunning;
      this.executor = executor;
   }

   public Map<?, ListenableFuture<Void>> runOptionsOnNodesAndAddToGoodSetOrPutExceptionIntoBadMap(
         final TemplateOptions options, Iterable<NodeMetadata> runningNodes, final Set<NodeMetadata> goodNodes,
         final Map<NodeMetadata, Exception> badNodes) {
      Map<NodeMetadata, ListenableFuture<Void>> responses = Maps.newHashMap();
      for (final NodeMetadata node : runningNodes) {
         responses.put(node, makeListenable(executor.submit(runOptionsOnNodeAndAddToGoodSetOrPutExceptionIntoBadMap(
               node, badNodes, goodNodes, options)), executor));
      }
      return responses;
   }

   public Callable<Void> runOptionsOnNodeAndAddToGoodSetOrPutExceptionIntoBadMap(final NodeMetadata node,
         final Map<NodeMetadata, Exception> badNodes, final Set<NodeMetadata> goodNodes, final TemplateOptions options) {
      return new Callable<Void>() {
         @Override
         public Void call() throws Exception {
            try {
               NodeMetadata node1 = runOptionsOnNode(node, options);
               logger.debug("<< options applied node(%s)", node1.getId());
               goodNodes.add(node1);
            } catch (Exception e) {
               logger.error(e, "<< problem applying options to node(%s): ", node.getId(), Throwables.getRootCause(e)
                     .getMessage());
               badNodes.put(node, e);
            }
            return null;
         }
      };
   }

   public NodeMetadata runOptionsOnNode(NodeMetadata node, TemplateOptions options) {
      if (!options.shouldBlockUntilRunning())
         return node;

      if (nodeRunning.apply(node))
         node = installNewCredentials(getNode.execute(node.getId()), node.getCredentials());
      else
         throw new IllegalStateException(String.format(
               "node didn't achieve the state running on node %s within %d seconds, final state: %s", node.getId(),
               timeouts.nodeRunning / 1000, node.getState()));

      List<SshCallable<?>> callables = Lists.newArrayList();
      if (options.getRunScript() != null) {
         callables.add(runScriptOnNode(node, "runscript", options.getRunScript()));
      }
      if (options.getPublicKey() != null) {
         callables.add(authorizeKeyOnNode(node, options.getPublicKey()));
      }

      // changing the key "MUST" come last or else the other commands may
      // fail.
      if (callables.size() > 0 || options.getPrivateKey() != null) {
         runCallablesOnNode(node, callables, options.getPrivateKey() != null ? installKeyOnNode(node, options
               .getPrivateKey()) : null);
      }

      if (options.getPort() > 0) {
         checkNodeHasPublicIps(node);
         blockUntilPortIsListeningOnPublicIp(options.getPort(), options.getSeconds(), Iterables.get(node
               .getPublicAddresses(), 0));
      }
      return node;
   }

   private void checkNodeHasPublicIps(NodeMetadata node) {
      checkState(node.getPublicAddresses().size() > 0, "node does not have IP addresses configured: " + node);
   }

   private void blockUntilPortIsListeningOnPublicIp(int port, int seconds, String inetAddress) {
      logger.debug(">> blocking on port %s:%d for %d seconds", inetAddress, port, seconds);
      RetryablePredicate<IPSocket> tester = new RetryablePredicate<IPSocket>(socketTester, seconds, 1, TimeUnit.SECONDS);
      IPSocket socket = new IPSocket(inetAddress, port);
      boolean passed = tester.apply(socket);
      if (passed)
         logger.debug("<< port %s:%d opened", inetAddress, port);
      else
         logger.warn("<< port %s:%d didn't open after %d seconds", inetAddress, port, seconds);
   }

   public InstallRSAPrivateKey installKeyOnNode(NodeMetadata node, Payload privateKey) {
      return new InstallRSAPrivateKey(node, privateKey);
   }

   public AuthorizeRSAPublicKey authorizeKeyOnNode(NodeMetadata node, Payload publicKey) {
      return new AuthorizeRSAPublicKey(node, publicKey);
   }

   public RunScriptOnNode runScriptOnNode(NodeMetadata node, String scriptName, Payload script) {
      return new RunScriptOnNode(runScriptNotRunning, node, scriptName, script);
   }

   public RunScriptOnNode runScriptOnNodeAsDefaultUser(NodeMetadata node, String scriptName, Payload script) {
      return new RunScriptOnNode(runScriptNotRunning, node, scriptName, script, false);
   }

   public Map<SshCallable<?>, ?> runCallablesOnNode(NodeMetadata node, Iterable<? extends SshCallable<?>> parallel,
         @Nullable SshCallable<?> last) {
      checkState(this.sshFactory != null, "runScript requested, but no SshModule configured");
      checkNodeHasPublicIps(node);
      checkNotNull(node.getCredentials().credential, "credentials.key for node " + node.getId());
      SshClient ssh = createSshClientOncePortIsListeningOnNode(node);
      try {
         ssh.connect();
         return runTasksUsingSshClient(parallel, last, ssh);
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   private Map<SshCallable<?>, ?> runTasksUsingSshClient(Iterable<? extends SshCallable<?>> parallel,
         SshCallable<?> last, SshClient ssh) {
      Map<SshCallable<?>, Object> responses = Maps.newHashMap();
      if (Iterables.size(parallel) > 0) {
         responses.putAll(runCallablesUsingSshClient(parallel, ssh));
      }
      if (last != null) {
         last.setConnection(ssh, logger);
         try {
            responses.put(last, last.call());
         } catch (Exception e) {
            Throwables.propagate(e);
         }
      }
      return responses;
   }

   public SshClient createSshClientOncePortIsListeningOnNode(NodeMetadata node) {
      IPSocket socket = new IPSocket(Iterables.get(node.getPublicAddresses(), 0), 22);
      socketTester.apply(socket);
      SshClient ssh = isKeyAuth(node) ? sshFactory.create(socket, node.getCredentials().identity,
            node.getCredentials().credential.getBytes()) : sshFactory.create(socket, node.getCredentials().identity,
            node.getCredentials().credential);
      return ssh;
   }

   private Map<SshCallable<?>, Object> runCallablesUsingSshClient(Iterable<? extends SshCallable<?>> parallel,
         SshClient ssh) {
      Map<SshCallable<?>, ListenableFuture<?>> parallelResponses = Maps.newHashMap();

      for (SshCallable<?> callable : parallel) {
         callable.setConnection(ssh, logger);
         parallelResponses.put(callable, ConcurrentUtils.makeListenable(executor.submit(callable), executor));
      }

      Map<SshCallable<?>, Exception> exceptions = awaitCompletion(parallelResponses, executor, null, logger, "ssh");
      if (exceptions.size() > 0)
         throw new RuntimeException(String.format("error invoking callables on nodes: %s", exceptions));
      Map<SshCallable<?>, Object> newresponses = transform(parallelResponses);
      return newresponses;
   }

   @SuppressWarnings("unchecked")
   public <T> Map<SshCallable<?>, T> transform(Map<SshCallable<?>, ListenableFuture<?>> responses) {
      Map<SshCallable<?>, T> actualResponses = Maps.newHashMap();
      for (Map.Entry<SshCallable<?>, ListenableFuture<?>> entry : responses.entrySet()) {
         try {
            actualResponses.put(entry.getKey(), (T) entry.getValue().get());
         } catch (InterruptedException e) {
            throw Throwables.propagate(e);
         } catch (ExecutionException e) {
            throw Throwables.propagate(e);
         }
      }
      return actualResponses;
   }

}
