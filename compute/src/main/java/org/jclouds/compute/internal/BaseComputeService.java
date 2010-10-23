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

package org.jclouds.compute.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Sets.filter;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static com.google.common.util.concurrent.Futures.immediateFuture;
import static org.jclouds.compute.predicates.NodePredicates.TERMINATED;
import static org.jclouds.compute.predicates.NodePredicates.all;
import static org.jclouds.concurrent.FutureIterables.awaitCompletion;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;
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
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.RunNodesAndAddToSetStrategy;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.io.Payload;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.scriptbuilder.InitBuilder;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.util.Utils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

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
   protected final Supplier<Set<? extends Image>> images;
   protected final Supplier<Set<? extends Hardware>> hardwareProfiles;
   protected final Supplier<Set<? extends Location>> locations;
   protected final ListNodesStrategy listNodesStrategy;
   protected final GetNodeMetadataStrategy getNodeMetadataStrategy;
   protected final RunNodesAndAddToSetStrategy runNodesAndAddToSetStrategy;
   protected final RebootNodeStrategy rebootNodeStrategy;
   protected final DestroyNodeStrategy destroyNodeStrategy;
   protected final Provider<TemplateBuilder> templateBuilderProvider;
   protected final Provider<TemplateOptions> templateOptionsProvider;
   protected final Predicate<NodeMetadata> nodeRunning;
   protected final Predicate<NodeMetadata> nodeTerminated;
   protected final ComputeUtils utils;
   protected final Timeouts timeouts;
   protected final ExecutorService executor;

   @Inject
   protected BaseComputeService(ComputeServiceContext context, Map<String, Credentials> credentialStore,
            @Memoized Supplier<Set<? extends Image>> images,
            @Memoized Supplier<Set<? extends Hardware>> hardwareProfiles,
            @Memoized Supplier<Set<? extends Location>> locations, ListNodesStrategy listNodesStrategy,
            GetNodeMetadataStrategy getNodeMetadataStrategy, RunNodesAndAddToSetStrategy runNodesAndAddToSetStrategy,
            RebootNodeStrategy rebootNodeStrategy, DestroyNodeStrategy destroyNodeStrategy,
            Provider<TemplateBuilder> templateBuilderProvider, Provider<TemplateOptions> templateOptionsProvider,
            @Named("NODE_RUNNING") Predicate<NodeMetadata> nodeRunning,
            @Named("NODE_TERMINATED") Predicate<NodeMetadata> nodeTerminated, ComputeUtils utils, Timeouts timeouts,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.context = checkNotNull(context, "context");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.images = checkNotNull(images, "images");
      this.hardwareProfiles = checkNotNull(hardwareProfiles, "hardwareProfiles");
      this.locations = checkNotNull(locations, "locations");
      this.listNodesStrategy = checkNotNull(listNodesStrategy, "listNodesStrategy");
      this.getNodeMetadataStrategy = checkNotNull(getNodeMetadataStrategy, "getNodeMetadataStrategy");
      this.runNodesAndAddToSetStrategy = checkNotNull(runNodesAndAddToSetStrategy, "runNodesAndAddToSetStrategy");
      this.rebootNodeStrategy = checkNotNull(rebootNodeStrategy, "rebootNodeStrategy");
      this.destroyNodeStrategy = checkNotNull(destroyNodeStrategy, "destroyNodeStrategy");
      this.templateBuilderProvider = checkNotNull(templateBuilderProvider, "templateBuilderProvider");
      this.templateOptionsProvider = checkNotNull(templateOptionsProvider, "templateOptionsProvider");
      this.nodeRunning = checkNotNull(nodeRunning, "nodeRunning");
      this.nodeTerminated = checkNotNull(nodeTerminated, "nodeTerminated");
      this.utils = checkNotNull(utils, "utils");
      this.timeouts = checkNotNull(timeouts, "timeouts");
      this.executor = checkNotNull(executor, "executor");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ComputeServiceContext getContext() {
      return context;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<? extends NodeMetadata> runNodesWithTag(String tag, int count, Template template)
            throws RunNodesException {
      checkArgument(tag.indexOf('-') == -1, "tag cannot contain hyphens");
      checkNotNull(template.getLocation(), "location");
      if (template.getOptions().getTaskName() == null && template.getOptions().getRunScript() != null
               && !(template.getOptions().getRunScript() instanceof InitBuilder))
         template.getOptions().nameTask("bootstrap");
      logger.debug(">> running %d node%s tag(%s) location(%s) image(%s) hardwareProfile(%s) options(%s)", count,
               count > 1 ? "s" : "", tag, template.getLocation().getId(), template.getImage().getId(), template
                        .getHardware().getId(), template.getOptions());
      Set<NodeMetadata> nodes = newHashSet();
      Map<NodeMetadata, Exception> badNodes = newLinkedHashMap();
      Map<?, Future<Void>> responses = runNodesAndAddToSetStrategy.execute(tag, count, template, nodes, badNodes);
      Map<?, Exception> executionExceptions = awaitCompletion(responses, executor, null, logger, "starting nodes");
      for (NodeMetadata node : concat(nodes, badNodes.keySet()))
         if (node.getCredentials() != null)
            credentialStore.put("node/" + node.getId(), node.getCredentials());
      if (executionExceptions.size() > 0 || badNodes.size() > 0) {
         throw new RunNodesException(tag, count, template, nodes, executionExceptions, badNodes);
      }
      return nodes;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<? extends NodeMetadata> runNodesWithTag(String tag, int count, TemplateOptions templateOptions)
            throws RunNodesException {
      return runNodesWithTag(tag, count, templateBuilder().any().options(templateOptions).build());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<? extends NodeMetadata> runNodesWithTag(String tag, int count) throws RunNodesException {
      return runNodesWithTag(tag, count, templateOptions());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void destroyNode(final String id) {
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
         credentialStore.remove("node/" + id);
      logger.debug("<< destroyed node(%s) success(%s)", id, successful);
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
                           destroyNode(from.getId());
                           return from;
                        }

                     });
                  }

               }, executor, null, logger, "destroying nodes"));
      logger.debug("<< destroyed(%d)", set.size());
      return set;
   }

   private Iterable<? extends NodeMetadata> nodesMatchingFilterAndNotTerminated(Predicate<NodeMetadata> filter) {
      return filter(detailsOnAllNodes(), and(filter, not(TERMINATED)));
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
      transformParallel(nodesMatchingFilterAndNotTerminated(filter), new Function<NodeMetadata, Future<Void>>() {
         // TODO use native async
         @Override
         public Future<Void> apply(NodeMetadata from) {
            rebootNode(from.getId());
            return immediateFuture(null);
         }

      }, executor, null, logger, "rebooting nodes");
      logger.debug("<< rebooted");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Map<NodeMetadata, ExecResponse> runScriptOnNodesMatching(Predicate<NodeMetadata> filter, Payload runScript)
            throws RunScriptOnNodesException {
      return runScriptOnNodesMatching(filter, runScript, RunScriptOptions.NONE);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Map<NodeMetadata, ExecResponse> runScriptOnNodesMatching(Predicate<NodeMetadata> filter,
            final Payload runScript, @Nullable final RunScriptOptions options) throws RunScriptOnNodesException {

      checkNotNull(filter, "Filter must be provided");
      checkNotNull(runScript, "runScript");
      checkNotNull(options, "options");
      if (options.getTaskName() == null)
         options.nameTask("jclouds-script-" + System.currentTimeMillis());

      Iterable<? extends NodeMetadata> nodes = filter(detailsOnAllNodes(), filter);

      final Map<NodeMetadata, ExecResponse> execs = newHashMap();
      final Map<NodeMetadata, Future<Void>> responses = newHashMap();
      final Map<NodeMetadata, Exception> badNodes = newLinkedHashMap();
      nodes = filterNodesWhoCanRunScripts(nodes, badNodes, options.getOverrideCredentials());

      for (final NodeMetadata node : nodes) {

         responses.put(node, executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
               try {
                  ExecResponse response = utils.runScriptOnNode(node, Statements.exec(Utils.toStringAndClose(runScript
                           .getInput())), options);
                  if (response != null)
                     execs.put(node, response);
               } catch (Exception e) {
                  badNodes.put(node, e);
               }
               return null;
            }

         }));

      }
      Map<?, Exception> exceptions = awaitCompletion(responses, executor, null, logger, "running script on nodes");
      if (exceptions.size() > 0 || badNodes.size() > 0) {
         throw new RunScriptOnNodesException(runScript, options, execs, exceptions, badNodes);
      }

      return execs;

   }

   private Iterable<? extends NodeMetadata> filterNodesWhoCanRunScripts(Iterable<? extends NodeMetadata> nodes,
            final Map<NodeMetadata, Exception> badNodes, final @Nullable Credentials overridingCredentials) {
      nodes = filter(transform(nodes, new Function<NodeMetadata, NodeMetadata>() {

         @Override
         public NodeMetadata apply(NodeMetadata node) {
            try {
               checkArgument(node.getPublicAddresses().size() > 0, "no public ip addresses on node: " + node);
               if (overridingCredentials != null) {
                  node = NodeMetadataBuilder.fromNodeMetadata(node).credentials(overridingCredentials).build();
               } else {
                  checkNotNull(node.getCredentials(), "If the default credentials need to be used, they can't be null");
                  checkNotNull(node.getCredentials().identity, "Account name for ssh authentication must be "
                           + "specified. Try passing RunScriptOptions with new credentials");
                  checkNotNull(node.getCredentials().credential, "Key or password for ssh authentication must be "
                           + "specified. Try passing RunScriptOptions with new credentials");
               }
               return node;
            } catch (Exception e) {
               badNodes.put(node, e);
               return null;
            }
         }
      }), notNull());
      return nodes;
   }

   private Set<? extends NodeMetadata> detailsOnAllNodes() {
      return newLinkedHashSet(listNodesStrategy.listDetailsOnNodesMatching(all()));
   }

   @Override
   public TemplateOptions templateOptions() {
      return templateOptionsProvider.get();
   }
}