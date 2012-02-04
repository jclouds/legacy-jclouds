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
package org.jclouds.compute.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Sets.filter;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static com.google.common.util.concurrent.Futures.immediateFuture;
import static org.jclouds.compute.predicates.NodePredicates.TERMINATED;
import static org.jclouds.compute.predicates.NodePredicates.all;
import static org.jclouds.concurrent.FutureIterables.awaitCompletion;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.RunScriptOnNodesException;
import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.InitializeRunScriptOnNodeOrPlaceInBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.ResumeNodeStrategy;
import org.jclouds.compute.strategy.RunScriptOnNodeAndAddToGoodMapOrPutExceptionIntoBadMap;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.domain.LoginCredentials.Builder;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.scriptbuilder.functions.InitAdminAccess;
import org.jclouds.util.Maps2;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BaseComputeService implements ComputeService {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final ComputeServiceContext context;
   protected final Map<String, Credentials> credentialStore;

   private final Supplier<Set<? extends Image>> images;
   private final Supplier<Set<? extends Hardware>> hardwareProfiles;
   private final Supplier<Set<? extends Location>> locations;
   private final ListNodesStrategy listNodesStrategy;
   private final GetNodeMetadataStrategy getNodeMetadataStrategy;
   private final CreateNodesInGroupThenAddToSet runNodesAndAddToSetStrategy;
   private final RebootNodeStrategy rebootNodeStrategy;
   private final DestroyNodeStrategy destroyNodeStrategy;
   private final ResumeNodeStrategy resumeNodeStrategy;
   private final SuspendNodeStrategy suspendNodeStrategy;
   private final Provider<TemplateBuilder> templateBuilderProvider;
   private final Provider<TemplateOptions> templateOptionsProvider;
   private final Predicate<NodeMetadata> nodeRunning;
   private final Predicate<NodeMetadata> nodeTerminated;
   private final Predicate<NodeMetadata> nodeSuspended;
   private final InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory;
   private final Timeouts timeouts;
   private final InitAdminAccess initAdminAccess;
   private final PersistNodeCredentials persistNodeCredentials;
   private final RunScriptOnNode.Factory runScriptOnNodeFactory;
   private final ExecutorService executor;

   @Inject
   protected BaseComputeService(ComputeServiceContext context, Map<String, Credentials> credentialStore,
         @Memoized Supplier<Set<? extends Image>> images, @Memoized Supplier<Set<? extends Hardware>> hardwareProfiles,
         @Memoized Supplier<Set<? extends Location>> locations, ListNodesStrategy listNodesStrategy,
         GetNodeMetadataStrategy getNodeMetadataStrategy, CreateNodesInGroupThenAddToSet runNodesAndAddToSetStrategy,
         RebootNodeStrategy rebootNodeStrategy, DestroyNodeStrategy destroyNodeStrategy,
         ResumeNodeStrategy resumeNodeStrategy, SuspendNodeStrategy suspendNodeStrategy,
         Provider<TemplateBuilder> templateBuilderProvider, Provider<TemplateOptions> templateOptionsProvider,
         @Named("NODE_RUNNING") Predicate<NodeMetadata> nodeRunning,
         @Named("NODE_TERMINATED") Predicate<NodeMetadata> nodeTerminated,
         @Named("NODE_SUSPENDED") Predicate<NodeMetadata> nodeSuspended,
         InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory, InitAdminAccess initAdminAccess,
         RunScriptOnNode.Factory runScriptOnNodeFactory, PersistNodeCredentials persistNodeCredentials,
         Timeouts timeouts, @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.context = checkNotNull(context, "context");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.images = checkNotNull(images, "images");
      this.hardwareProfiles = checkNotNull(hardwareProfiles, "hardwareProfiles");
      this.locations = checkNotNull(locations, "locations");
      this.listNodesStrategy = checkNotNull(listNodesStrategy, "listNodesStrategy");
      this.getNodeMetadataStrategy = checkNotNull(getNodeMetadataStrategy, "getNodeMetadataStrategy");
      this.runNodesAndAddToSetStrategy = checkNotNull(runNodesAndAddToSetStrategy, "runNodesAndAddToSetStrategy");
      this.rebootNodeStrategy = checkNotNull(rebootNodeStrategy, "rebootNodeStrategy");
      this.resumeNodeStrategy = checkNotNull(resumeNodeStrategy, "resumeNodeStrategy");
      this.suspendNodeStrategy = checkNotNull(suspendNodeStrategy, "suspendNodeStrategy");
      this.destroyNodeStrategy = checkNotNull(destroyNodeStrategy, "destroyNodeStrategy");
      this.templateBuilderProvider = checkNotNull(templateBuilderProvider, "templateBuilderProvider");
      this.templateOptionsProvider = checkNotNull(templateOptionsProvider, "templateOptionsProvider");
      this.nodeRunning = checkNotNull(nodeRunning, "nodeRunning");
      this.nodeTerminated = checkNotNull(nodeTerminated, "nodeTerminated");
      this.nodeSuspended = checkNotNull(nodeSuspended, "nodeSuspended");
      this.initScriptRunnerFactory = checkNotNull(initScriptRunnerFactory, "initScriptRunnerFactory");
      this.timeouts = checkNotNull(timeouts, "timeouts");
      this.initAdminAccess = checkNotNull(initAdminAccess, "initAdminAccess");
      this.runScriptOnNodeFactory = checkNotNull(runScriptOnNodeFactory, "runScriptOnNodeFactory");
      this.persistNodeCredentials = checkNotNull(persistNodeCredentials, "persistNodeCredentials");
      this.executor = checkNotNull(executor, "executor");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ComputeServiceContext getContext() {
      return context;
   }

   @Override
   public Set<? extends NodeMetadata> createNodesInGroup(String group, int count, Template template)
         throws RunNodesException {
      checkNotNull(group, "group cannot be null");
      checkNotNull(template.getLocation(), "location");
      logger.debug(">> running %d node%s group(%s) location(%s) image(%s) hardwareProfile(%s) options(%s)", count,
            count > 1 ? "s" : "", group, template.getLocation().getId(), template.getImage().getId(), template
                  .getHardware().getId(), template.getOptions());
      Set<NodeMetadata> goodNodes = newLinkedHashSet();
      Map<NodeMetadata, Exception> badNodes = newLinkedHashMap();
      Multimap<NodeMetadata, CustomizationResponse> customizationResponses = LinkedHashMultimap.create();

      if (template.getOptions().getRunScript() != null)
         initAdminAccess.visit(template.getOptions().getRunScript());

      Map<?, Future<Void>> responses = runNodesAndAddToSetStrategy.execute(group, count, template, goodNodes, badNodes,
            customizationResponses);
      Map<?, Exception> executionExceptions = awaitCompletion(responses, executor, null, logger, "createNodesInGroup("
            + group + ")");
      Function<NodeMetadata, NodeMetadata> fn = persistNodeCredentials.always(template.getOptions().getRunScript());
      badNodes = Maps2.transformKeys(badNodes, fn);
      goodNodes = ImmutableSet.copyOf(Iterables.transform(goodNodes, fn));
      if (executionExceptions.size() > 0 || badNodes.size() > 0) {
         throw new RunNodesException(group, count, template, goodNodes, executionExceptions, badNodes);
      }
      return goodNodes;
   }

   @Override
   public Set<? extends NodeMetadata> createNodesInGroup(String group, int count, TemplateOptions templateOptions)
         throws RunNodesException {
      return createNodesInGroup(group, count, templateBuilder().any().options(templateOptions).build());
   }

   @Override
   public Set<? extends NodeMetadata> createNodesInGroup(String group, int count) throws RunNodesException {
      return createNodesInGroup(group, count, templateOptions());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void destroyNode(final String id) {
      NodeMetadata destroyedNode = doDestroyNode(id);
      cleanUpIncidentalResourcesOfDeadNodes(Collections.singleton(destroyedNode));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<? extends NodeMetadata> destroyNodesMatching(Predicate<NodeMetadata> filter) {
      logger.debug(">> destroying nodes matching(%s)", filter);
      Set<NodeMetadata> set = newLinkedHashSet(transformParallel(nodesMatchingFilterAndNotTerminated(filter),
            new Function<NodeMetadata, Future<NodeMetadata>>() {

               // TODO make an async interface instead of re-wrapping
               @Override
               public Future<NodeMetadata> apply(final NodeMetadata from) {
                  return executor.submit(new Callable<NodeMetadata>() {

                     @Override
                     public NodeMetadata call() throws Exception {
                        doDestroyNode(from.getId());
                        return from;
                     }

                     @Override
                     public String toString() {
                        return "destroyNode(" + from.getId() + ")";
                     }
                  });
               }

            }, executor, null, logger, "destroyNodesMatching(" + filter + ")"));
      logger.debug("<< destroyed(%d)", set.size());
      
      cleanUpIncidentalResourcesOfDeadNodes(set);
      return set;
   }

   protected NodeMetadata doDestroyNode(final String id) {
      checkNotNull(id, "id");
      logger.debug(">> destroying node(%s)", id);
      final AtomicReference<NodeMetadata> node = new AtomicReference<NodeMetadata>();
      RetryablePredicate<String> tester = new RetryablePredicate<String>(new Predicate<String>() {

         @Override
         public boolean apply(String input) {
            try {
               NodeMetadata md = destroyNodeStrategy.destroyNode(id);
               if (md != null)
                  node.set(md);
               return true;
            } catch (IllegalStateException e) {
               logger.warn("<< illegal state destroying node(%s)", id);
               return false;
            }
         }

      }, timeouts.nodeRunning, 1000, TimeUnit.MILLISECONDS);
      
      boolean successful = tester.apply(id) && (node.get() == null || nodeTerminated.apply(node.get()));
      if (successful)
         credentialStore.remove("node#" + id);
      logger.debug("<< destroyed node(%s) success(%s)", id, successful);
      return node.get();
   }

   protected void cleanUpIncidentalResourcesOfDeadNodes(Set<? extends NodeMetadata> deadNodes) {
      // no-op; to be overridden
   }
   
   Iterable<? extends NodeMetadata> nodesMatchingFilterAndNotTerminated(Predicate<NodeMetadata> filter) {
      return filter(detailsOnAllNodes(), and(checkNotNull(filter, "filter"), not(TERMINATED)));
   }

   /**
    * @throws NoSuchElementException
    *            if none found
    */
   Iterable<? extends NodeMetadata> nodesMatchingFilterAndNotTerminatedExceptionIfNotFound(
         Predicate<NodeMetadata> filter) {
      Iterable<? extends NodeMetadata> nodes = nodesMatchingFilterAndNotTerminated(filter);
      if (Iterables.size(nodes) == 0)
         throw new NoSuchElementException("no nodes matched filter: " + filter);
      return nodes;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<ComputeMetadata> listNodes() {
      logger.debug(">> listing nodes");
      Set<ComputeMetadata> set = newLinkedHashSet(listNodesStrategy.listNodes());
      logger.debug("<< list(%d)", set.size());
      return set;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<? extends NodeMetadata> listNodesDetailsMatching(Predicate<ComputeMetadata> filter) {
      checkNotNull(filter, "filter");
      logger.debug(">> listing node details matching(%s)", filter);
      Set<NodeMetadata> set = newLinkedHashSet(listNodesStrategy.listDetailsOnNodesMatching(filter));
      logger.debug("<< list(%d)", set.size());
      return set;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<? extends Hardware> listHardwareProfiles() {
      return hardwareProfiles.get();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<? extends Image> listImages() {
      return images.get();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<? extends Location> listAssignableLocations() {
      return locations.get();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder templateBuilder() {
      return templateBuilderProvider.get();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NodeMetadata getNodeMetadata(String id) {
      checkNotNull(id, "id");
      return getNodeMetadataStrategy.getNode(id);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void rebootNode(String id) {
      checkNotNull(id, "id");
      logger.debug(">> rebooting node(%s)", id);
      NodeMetadata node = rebootNodeStrategy.rebootNode(id);
      boolean successful = nodeRunning.apply(node);
      logger.debug("<< rebooted node(%s) success(%s)", id, successful);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void rebootNodesMatching(Predicate<NodeMetadata> filter) {
      logger.debug(">> rebooting nodes matching(%s)", filter);
      transformParallel(nodesMatchingFilterAndNotTerminatedExceptionIfNotFound(filter),
            new Function<NodeMetadata, Future<Void>>() {
               // TODO use native async
               @Override
               public Future<Void> apply(NodeMetadata from) {
                  rebootNode(from.getId());
                  return immediateFuture(null);
               }

            }, executor, null, logger, "rebootNodesMatching(" + filter + ")");
      logger.debug("<< rebooted");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void resumeNode(String id) {
      checkNotNull(id, "id");
      logger.debug(">> resuming node(%s)", id);
      NodeMetadata node = resumeNodeStrategy.resumeNode(id);
      boolean successful = nodeRunning.apply(node);
      logger.debug("<< resumed node(%s) success(%s)", id, successful);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void resumeNodesMatching(Predicate<NodeMetadata> filter) {
      logger.debug(">> resuming nodes matching(%s)", filter);
      transformParallel(nodesMatchingFilterAndNotTerminatedExceptionIfNotFound(filter),
            new Function<NodeMetadata, Future<Void>>() {
               // TODO use native async
               @Override
               public Future<Void> apply(NodeMetadata from) {
                  resumeNode(from.getId());
                  return immediateFuture(null);
               }

            }, executor, null, logger, "resumeNodesMatching(" + filter + ")");
      logger.debug("<< resumed");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void suspendNode(String id) {
      checkNotNull(id, "id");
      logger.debug(">> suspending node(%s)", id);
      NodeMetadata node = suspendNodeStrategy.suspendNode(id);
      boolean successful = nodeSuspended.apply(node);
      logger.debug("<< suspended node(%s) success(%s)", id, successful);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void suspendNodesMatching(Predicate<NodeMetadata> filter) {
      logger.debug(">> suspending nodes matching(%s)", filter);
      transformParallel(nodesMatchingFilterAndNotTerminatedExceptionIfNotFound(filter),
            new Function<NodeMetadata, Future<Void>>() {
               // TODO use native async
               @Override
               public Future<Void> apply(NodeMetadata from) {
                  suspendNode(from.getId());
                  return immediateFuture(null);
               }

            }, executor, null, logger, "suspendNodesMatching(" + filter + ")");
      logger.debug("<< suspended");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Map<NodeMetadata, ExecResponse> runScriptOnNodesMatching(Predicate<NodeMetadata> filter, String runScript)
         throws RunScriptOnNodesException {
      return runScriptOnNodesMatching(filter, Statements.exec(checkNotNull(runScript, "runScript")));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Map<NodeMetadata, ExecResponse> runScriptOnNodesMatching(Predicate<NodeMetadata> filter, Statement runScript)
         throws RunScriptOnNodesException {
      return runScriptOnNodesMatching(filter, runScript, RunScriptOptions.NONE);
   }

   @Override
   public Map<? extends NodeMetadata, ExecResponse> runScriptOnNodesMatching(Predicate<NodeMetadata> filter,
         String runScript, RunScriptOptions options) throws RunScriptOnNodesException {
      return runScriptOnNodesMatching(filter, Statements.exec(checkNotNull(runScript, "runScript")), options);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Map<NodeMetadata, ExecResponse> runScriptOnNodesMatching(Predicate<NodeMetadata> filter, Statement runScript,
         RunScriptOptions options) throws RunScriptOnNodesException {

      checkNotNull(filter, "filter");
      checkNotNull(runScript, "runScript");
      checkNotNull(options, "options");

      Map<NodeMetadata, ExecResponse> goodNodes = newLinkedHashMap();
      Map<NodeMetadata, Exception> badNodes = newLinkedHashMap();
      Map<NodeMetadata, Future<ExecResponse>> responses = newLinkedHashMap();
      Map<?, Exception> exceptions = ImmutableMap.<Object, Exception> of();

      initAdminAccess.visit(runScript);

      Iterable<? extends RunScriptOnNode> scriptRunners = transformNodesIntoInitializedScriptRunners(
            nodesMatchingFilterAndNotTerminatedExceptionIfNotFound(filter), runScript, options, badNodes);
      if (Iterables.size(scriptRunners) > 0) {
         for (RunScriptOnNode runner : scriptRunners) {
            responses.put(runner.getNode(), executor.submit(new RunScriptOnNodeAndAddToGoodMapOrPutExceptionIntoBadMap(
                  runner, goodNodes, badNodes)));
         }
         exceptions = awaitCompletion(responses, executor, null, logger, "runScriptOnNodesMatching(" + filter + ")");
      }

      Function<NodeMetadata, NodeMetadata> fn = persistNodeCredentials.ifAdminAccess(runScript);
      badNodes = Maps2.transformKeys(badNodes, fn);
      goodNodes = Maps2.transformKeys(goodNodes, fn);

      if (exceptions.size() > 0 || badNodes.size() > 0) {
         throw new RunScriptOnNodesException(runScript, options, goodNodes, exceptions, badNodes);
      }
      return goodNodes;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ExecResponse runScriptOnNode(String id, String runScript) {
      return runScriptOnNode(id, runScript, RunScriptOptions.NONE);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ExecResponse runScriptOnNode(String id, String runScript, RunScriptOptions options) {
      return runScriptOnNode(id, Statements.exec(checkNotNull(runScript, "runScript")), options);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ExecResponse runScriptOnNode(String id, Statement runScript) {
      return runScriptOnNode(id, runScript, RunScriptOptions.NONE);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ExecResponse runScriptOnNode(String id, Statement runScript, RunScriptOptions options) {
      NodeMetadata node = this.getNodeMetadata(id);
      if (node == null)
         throw new NoSuchElementException(id);
      if (node.getState() != NodeState.RUNNING)
         throw new IllegalStateException("node " + id
               + " needs to be running before executing a script on it. current state: " + node.getState());
      initAdminAccess.visit(runScript);
      node = updateNodeWithCredentialsIfPresent(node, options);
      ExecResponse response = runScriptOnNodeFactory.create(node, runScript, options).init().call();
      persistNodeCredentials.ifAdminAccess(runScript).apply(node);
      return response;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListenableFuture<ExecResponse> submitScriptOnNode(String id, final Statement runScript,
         RunScriptOptions options) {
      NodeMetadata node = this.getNodeMetadata(id);
      if (node == null)
         throw new NoSuchElementException(id);
      if (node.getState() != NodeState.RUNNING)
         throw new IllegalStateException("node " + id
               + " needs to be running before executing a script on it. current state: " + node.getState());
      initAdminAccess.visit(runScript);
      final NodeMetadata node1 = updateNodeWithCredentialsIfPresent(node, options);
      ListenableFuture<ExecResponse> response = runScriptOnNodeFactory.submit(node1, runScript, options);
      response.addListener(new Runnable() {

         @Override
         public void run() {
            persistNodeCredentials.ifAdminAccess(runScript).apply(node1);
         }

      }, executor);
      return response;
   }

   private Iterable<? extends RunScriptOnNode> transformNodesIntoInitializedScriptRunners(
         Iterable<? extends NodeMetadata> nodes, Statement script, RunScriptOptions options,
         Map<NodeMetadata, Exception> badNodes) {
      return filter(
            transformParallel(nodes, new TransformNodesIntoInitializedScriptRunners(script, options, badNodes),
                  executor, null, logger, "initialize script runners"), notNull());
   }

   private Set<? extends NodeMetadata> detailsOnAllNodes() {
      return newLinkedHashSet(listNodesStrategy.listDetailsOnNodesMatching(all()));
   }

   @Override
   public TemplateOptions templateOptions() {
      return templateOptionsProvider.get();
   }

   protected NodeMetadata updateNodeWithCredentialsIfPresent(NodeMetadata node, RunScriptOptions options) {
      checkNotNull(node, "node");
      Builder builder = LoginCredentials.builder(node.getCredentials());
      if (options.getLoginUser() != null)
         builder.user(options.getLoginUser());
      if (options.hasLoginPasswordOption()) {
          if (options.hasLoginPassword()) {
             builder.password(options.getLoginPassword());
          } else {
             builder.noPassword();
          }
      }
      if (options.hasLoginPrivateKeyOption()) {
          if (options.hasLoginPrivateKey()) {
             builder.privateKey(options.getLoginPrivateKey());
          } else {
             builder.noPrivateKey();
          }
      }
      if (options.shouldAuthenticateSudo() != null)
         builder.authenticateSudo(true);
      return NodeMetadataBuilder.fromNodeMetadata(node).credentials(builder.build()).build();
   }

   private final class TransformNodesIntoInitializedScriptRunners implements
         Function<NodeMetadata, Future<RunScriptOnNode>> {
      private final Map<NodeMetadata, Exception> badNodes;
      private final Statement script;
      private final RunScriptOptions options;

      private TransformNodesIntoInitializedScriptRunners(Statement script, RunScriptOptions options,
            Map<NodeMetadata, Exception> badNodes) {
         this.badNodes = checkNotNull(badNodes, "badNodes");
         this.script = checkNotNull(script, "script");
         this.options = checkNotNull(options, "options");
      }

      @Override
      public Future<RunScriptOnNode> apply(NodeMetadata node) {
         node = updateNodeWithCredentialsIfPresent(node, options);
         return executor.submit(initScriptRunnerFactory.create(node, script, options, badNodes));
      }

   }

}