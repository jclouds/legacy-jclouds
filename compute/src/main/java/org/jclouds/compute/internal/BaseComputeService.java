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
import static org.jclouds.compute.util.ComputeServiceUtils.installNewCredentials;
import static org.jclouds.concurrent.FutureIterables.awaitCompletion;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.RunScriptOnNodesException;
import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.RunNodesAndAddToSetStrategy;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.domain.Location;
import org.jclouds.io.Payload;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;

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
   protected final Supplier<Set<? extends Image>> images;
   protected final Supplier<Set<? extends Size>> sizes;
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
   protected final ExecutorService executor;

   @Inject
   protected BaseComputeService(ComputeServiceContext context, Supplier<Set<? extends Image>> images,
            Supplier<Set<? extends Size>> sizes, Supplier<Set<? extends Location>> locations,
            ListNodesStrategy listNodesStrategy, GetNodeMetadataStrategy getNodeMetadataStrategy,
            RunNodesAndAddToSetStrategy runNodesAndAddToSetStrategy, RebootNodeStrategy rebootNodeStrategy,
            DestroyNodeStrategy destroyNodeStrategy, Provider<TemplateBuilder> templateBuilderProvider,
            Provider<TemplateOptions> templateOptionsProvider,
            @Named("NODE_RUNNING") Predicate<NodeMetadata> nodeRunning,
            @Named("NODE_TERMINATED") Predicate<NodeMetadata> nodeTerminated, ComputeUtils utils,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.context = checkNotNull(context, "context");
      this.images = checkNotNull(images, "images");
      this.sizes = checkNotNull(sizes, "sizes");
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
      logger.debug(">> running %d node%s tag(%s) location(%s) image(%s) size(%s) options(%s)", count, count > 1 ? "s"
               : "", tag, template.getLocation().getId(), template.getImage().getId(), template.getSize().getId(),
               template.getOptions());
      Set<NodeMetadata> nodes = Sets.newHashSet();
      Map<NodeMetadata, Exception> badNodes = Maps.newLinkedHashMap();
      Map<?, Future<Void>> responses = runNodesAndAddToSetStrategy.execute(tag, count, template, nodes, badNodes);
      Map<?, Exception> executionExceptions = awaitCompletion(responses, executor, null, logger, "starting nodes");
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
   public void destroyNode(String id) {
      checkNotNull(id, "id");
      logger.debug(">> destroying node(%s)", id);
      NodeMetadata node = destroyNodeStrategy.execute(id);
      boolean successful = node == null ? true : nodeTerminated.apply(node);
      logger.debug("<< destroyed node(%s) success(%s)", id, successful);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<? extends NodeMetadata> destroyNodesMatching(Predicate<NodeMetadata> filter) {
      logger.debug(">> destroying nodes matching(%s)", filter);
      Set<NodeMetadata> set = Sets.newLinkedHashSet(transformParallel(nodesMatchingFilterAndNotTerminated(filter),
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
      return Iterables.filter(detailsOnAllNodes(), Predicates.and(filter, Predicates.not(NodePredicates.TERMINATED)));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<ComputeMetadata> listNodes() {
      logger.debug(">> listing nodes");
      Set<ComputeMetadata> set = Sets.newLinkedHashSet(listNodesStrategy.list());
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
      Set<NodeMetadata> set = Sets.newLinkedHashSet(listNodesStrategy.listDetailsOnNodesMatching(filter));
      logger.debug("<< list(%d)", set.size());
      return set;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<? extends Size> listSizes() {
      return sizes.get();
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
      return getNodeMetadataStrategy.execute(id);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void rebootNode(String id) {
      checkNotNull(id, "id");
      logger.debug(">> rebooting node(%s)", id);
      NodeMetadata node = rebootNodeStrategy.execute(id);
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
            return Futures.immediateFuture(null);
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
      Iterable<NodeMetadata> nodes = verifyParametersAndListNodes(filter, runScript, (options != null) ? options
               : RunScriptOptions.NONE);

      final Map<NodeMetadata, ExecResponse> execs = Maps.newHashMap();

      final Map<NodeMetadata, Exception> badNodes = Maps.newLinkedHashMap();

      Map<NodeMetadata, Future<Void>> responses = Maps.newHashMap();

      for (final NodeMetadata node : nodes) {

         responses.put(node, executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
               try {
                  RunScriptOnNode callable;
                  if (options.isRunAsRoot())
                     callable = utils.runScriptOnNode(node, "computeserv", runScript);
                  else
                     callable = utils.runScriptOnNodeAsDefaultUser(node, "computeserv", runScript);
                  SshClient ssh = utils.createSshClientOncePortIsListeningOnNode(node);
                  try {
                     ssh.connect();
                     callable.setConnection(ssh, logger);
                     execs.put(node, callable.call());
                  } finally {
                     if (ssh != null)
                        ssh.disconnect();
                  }
               } catch (Exception e) {
                  badNodes.put(node, e);

               }
               return null;
            }
         }));

      }
      Map<?, Exception> exceptions = awaitCompletion(responses, executor, null, logger, "starting nodes");
      if (exceptions.size() > 0 || badNodes.size() > 0) {
         throw new RunScriptOnNodesException(runScript, options, execs, exceptions, badNodes);
      }
      return execs;

   }

   private Iterable<NodeMetadata> verifyParametersAndListNodes(Predicate<NodeMetadata> filter, Payload runScript,
            final RunScriptOptions options) {
      checkNotNull(filter, "Filter must be provided");
      checkNotNull(runScript, "The script (represented by bytes array - use \"script\".getBytes() must be provided");
      checkNotNull(options, "options");

      Iterable<? extends NodeMetadata> nodes = Iterables.filter(detailsOnAllNodes(), filter);
      // TODO parallel
      return Iterables.transform(nodes, new Function<NodeMetadata, NodeMetadata>() {

         @Override
         public NodeMetadata apply(NodeMetadata node) {

            checkArgument(node.getPublicAddresses().size() > 0, "no public ip addresses on node: " + node);
            if (options.getOverrideCredentials() != null) {
               // override the credentials with provided to this
               // method
               node = installNewCredentials(node, options.getOverrideCredentials());
            } else {
               // don't override
               checkNotNull(node.getCredentials(), "If the default credentials need to be used, they can't be null");
               checkNotNull(node.getCredentials().identity, "Account name for ssh authentication must be "
                        + "specified. Try passing RunScriptOptions with new credentials");
               checkNotNull(node.getCredentials().credential, "Key or password for ssh authentication must be "
                        + "specified. Try passing RunScriptOptions with new credentials");
            }
            return node;
         }
      });
   }

   private Set<? extends NodeMetadata> detailsOnAllNodes() {
      return Sets.newLinkedHashSet(listNodesStrategy.listDetailsOnNodesMatching(NodePredicates.all()));
   }

   @Override
   public TemplateOptions templateOptions() {
      return templateOptionsProvider.get();
   }
}