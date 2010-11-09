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

package org.jclouds.compute.util;

import static com.google.common.base.Throwables.getRootCause;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.jclouds.compute.util.ComputeServiceUtils.findReachableSocketOnNode;
import static org.jclouds.concurrent.FutureIterables.awaitCompletion;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.callables.InitAndStartScriptOnNode;
import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.RetryIfSocketNotYetOpen;
import org.jclouds.compute.predicates.ScriptStatusReturnsZero.CommandUsingClient;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.util.ComputeServiceUtils.SshCallable;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.InitBuilder;
import org.jclouds.scriptbuilder.domain.AuthorizeRSAPublicKey;
import org.jclouds.scriptbuilder.domain.InstallRSAPrivateKey;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
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
   protected final Function<NodeMetadata, SshClient> sshFactory;
   protected final Predicate<CommandUsingClient> runScriptNotRunning;
   protected final Provider<RetryIfSocketNotYetOpen> socketTester;
   protected final ExecutorService executor;
   protected final Predicate<NodeMetadata> nodeRunning;
   protected final GetNodeMetadataStrategy getNode;
   protected final Timeouts timeouts;

   @Inject
   public ComputeUtils(Provider<RetryIfSocketNotYetOpen> socketTester, Function<NodeMetadata, SshClient> sshFactory,
         @Named("SCRIPT_COMPLETE") Predicate<CommandUsingClient> runScriptNotRunning, GetNodeMetadataStrategy getNode,
         Timeouts timeouts, @Named("NODE_RUNNING") Predicate<NodeMetadata> nodeRunning,
         @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.sshFactory = sshFactory;
      this.nodeRunning = nodeRunning;
      this.timeouts = timeouts;
      this.getNode = getNode;
      this.socketTester = socketTester;
      this.runScriptNotRunning = runScriptNotRunning;
      this.executor = executor;
   }

   public Map<?, Future<Void>> runOptionsOnNodesAndAddToGoodSetOrPutExceptionIntoBadMap(final TemplateOptions options,
         Iterable<NodeMetadata> runningNodes, final Set<NodeMetadata> goodNodes,
         final Map<NodeMetadata, Exception> badNodes) {
      Map<NodeMetadata, Future<Void>> responses = newHashMap();
      for (final NodeMetadata node : runningNodes) {
         responses.put(node, executor.submit(runOptionsOnNodeAndAddToGoodSetOrPutExceptionIntoBadMap(node, badNodes,
               goodNodes, options)));
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
               logger.error(e, "<< problem applying options to node(%s): ", node.getId(), getRootCause(e).getMessage());
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
         node = NodeMetadataBuilder.fromNodeMetadata(getNode.getNode(node.getId())).credentials(node.getCredentials())
               .build();
      else
         throw new IllegalStateException(String.format(
               "node didn't achieve the state running on node %s within %d seconds, final state: %s", node.getId(),
               timeouts.nodeRunning / 1000, node.getState()));
      List<Statement> bootstrap = newArrayList();
      if (options.getPublicKey() != null)
         bootstrap.add(new AuthorizeRSAPublicKey(options.getPublicKey()));
      if (options.getRunScript() != null)
         bootstrap.add(options.getRunScript());
      if (options.getPrivateKey() != null)
         bootstrap.add(new InstallRSAPrivateKey(options.getPrivateKey()));
      if (bootstrap.size() >= 1) {
         if (options.getTaskName() == null && !(options.getRunScript() instanceof InitBuilder))
            options.nameTask("bootstrap");
         runScriptOnNode(node, bootstrap.size() == 1 ? bootstrap.get(0) : new StatementList(bootstrap), options);
      }
      return node;
   }

   public ExecResponse runScriptOnNode(NodeMetadata node, Statement runScript, RunScriptOptions options) {
      InitAndStartScriptOnNode callable = generateScript(node, runScript, options);
      ExecResponse response;
      SshClient ssh = sshFactory.apply(node);
      try {
         ssh.connect();
         callable.setConnection(ssh, logger);
         response = callable.call();
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
      if (options.getPort() > 0) {
         findReachableSocketOnNode(socketTester.get().seconds(options.getSeconds()), node, options.getPort());
      }
      return response;
   }

   public InitAndStartScriptOnNode generateScript(NodeMetadata node, Statement script, RunScriptOptions options) {
      return options.shouldBlockOnComplete() ? new RunScriptOnNode(runScriptNotRunning, node, options.getTaskName(),
            script, options.shouldRunAsRoot()) : new InitAndStartScriptOnNode(node, options.getTaskName(), script,
            options.shouldRunAsRoot());
   }

   public Map<SshCallable<?>, ?> runCallablesOnNode(NodeMetadata node, Iterable<SshCallable<?>> parallel,
         @Nullable SshCallable<?> last) {
      SshClient ssh = sshFactory.apply(node);
      try {
         ssh.connect();
         return runTasksUsingSshClient(parallel, last, ssh);
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   private Map<SshCallable<?>, ?> runTasksUsingSshClient(Iterable<SshCallable<?>> parallel, SshCallable<?> last,
         SshClient ssh) {
      Map<SshCallable<?>, Object> responses = newHashMap();
      if (size(parallel) > 0) {
         responses.putAll(runCallablesUsingSshClient(parallel, ssh));
      }
      if (last != null) {
         last.setConnection(ssh, logger);
         try {
            responses.put(last, last.call());
         } catch (Exception e) {
            propagate(e);
         }
      }
      return responses;
   }

   // TODO refactor
   private Map<SshCallable<?>, Object> runCallablesUsingSshClient(Iterable<SshCallable<?>> parallel, SshClient ssh) {
      Map<SshCallable<?>, Future<?>> parallelResponses = newHashMap();

      for (SshCallable<?> callable : parallel) {
         callable.setConnection(ssh, logger);
         parallelResponses.put(callable, executor.submit(callable));
      }

      Map<SshCallable<?>, Exception> exceptions = awaitCompletion(parallelResponses, executor, null, logger, "ssh");
      if (exceptions.size() > 0)
         throw new RuntimeException(String.format("error invoking callables on nodes: %s", exceptions));
      Map<SshCallable<?>, Object> newresponses = transform(parallelResponses);
      return newresponses;
   }

   @SuppressWarnings("unchecked")
   public <T> Map<SshCallable<?>, T> transform(Map<SshCallable<?>, Future<?>> responses) {
      Map<SshCallable<?>, T> actualResponses = newHashMap();
      for (Map.Entry<SshCallable<?>, Future<?>> entry : responses.entrySet()) {
         try {
            actualResponses.put(entry.getKey(), (T) entry.getValue().get());
         } catch (InterruptedException e) {
            throw propagate(e);
         } catch (ExecutionException e) {
            throw propagate(e);
         }
      }
      return actualResponses;
   }

}
