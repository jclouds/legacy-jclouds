package org.jclouds.nodepool.internal;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.removeIf;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.nodepool.config.NodePoolComputeServiceProperties.BACKING_GROUP_PROPERTY;
import static org.jclouds.nodepool.config.NodePoolComputeServiceProperties.BACKING_TEMPLATE_PROPERTY;
import static org.jclouds.nodepool.config.NodePoolComputeServiceProperties.MAX_SIZE_PROPERTY;
import static org.jclouds.nodepool.config.NodePoolComputeServiceProperties.MIN_SIZE_PROPERTY;
import static org.jclouds.nodepool.config.NodePoolComputeServiceProperties.REMOVE_DESTROYED_PROPERTY;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.concurrent.Futures;
import org.jclouds.logging.Logger;
import org.jclouds.nodepool.NodePoolComputeService;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * An eager {@link NodePoolComputeService}. Eagerly builds and maintains a pool of nodes. It's only
 * "started" after min nodes are allocated and available.
 * 
 * @author David Alves
 * 
 */
public class EagerNodePoolComputeService extends BaseNodePoolComputeService {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final int maxSize;
   private final boolean reuseDestroyed;
   private final int minSize;
   private final ExecutorService executor;

   // set of available nodes
   private Set<NodeMetadata> available = Sets.newHashSet();

   // lock associated with changes to the pool since they happen asynchronously
   private final Lock lock = new ReentrantLock();

   // all the nodes in the pool (associated or not)
   private final Set<NodeMetadata> poolNodes = Sets.newLinkedHashSet();

   @Inject
   public EagerNodePoolComputeService(ComputeServiceContext backingComputeServiceContext,
            @Named(BACKING_GROUP_PROPERTY) String poolGroupPrefix,
            @Named(MAX_SIZE_PROPERTY) int maxSize, @Named(MIN_SIZE_PROPERTY) int minSize,
            @Named(REMOVE_DESTROYED_PROPERTY) boolean readdDestroyed,
            @Nullable @Named(BACKING_TEMPLATE_PROPERTY) Template backingTemplate,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      super(backingComputeServiceContext, poolGroupPrefix, backingTemplate);
      this.maxSize = maxSize;
      this.minSize = minSize;
      this.reuseDestroyed = readdDestroyed;
      this.executor = executor;
   }

   @Override
   public synchronized Set<? extends NodeMetadata> createNodesInGroup(String group, int count) throws RunNodesException {
      checkState(started.get(), "pool is not started");
      try {
         return assignPoolNodes(group, count);
      } catch (Exception e) {
         Set<NodeMetadata> nodes = Collections.emptySet();
         Map<String, Exception> executionExceptions = ImmutableMap.of("poolnode", e);
         Map<NodeMetadata, Exception> failedNodes = ImmutableMap.of(new NodeMetadataBuilder().id("poolnode").status(
                  Status.ERROR).build(), e);
         throw new RunNodesException(group, count, template, nodes, executionExceptions, failedNodes);
      }
   }

   @Override
   public synchronized void destroyNode(String id) {
      checkState(started.get(), "pool is not started");
      unassignNode(id);
   }

   @Override
   public synchronized Set<? extends NodeMetadata> destroyNodesMatching(Predicate<NodeMetadata> filter) {
      checkState(started.get(), "pool is not started");
      // copy the set of nodes to unassign because we'll be altering the assignments map.
      Set<Map.Entry<String, NodeMetadata>> poolNodesToUnassign = Sets
               .newHashSet(filterAssignmentsBasedOnUserPredicate(filter));
      // TODO this should be done in parallel since it can take quite a while, moreover the contract
      // for any destroy node action should probably be that the pool has at least minSize nodes
      // before it returns. need to think it through a bit better.
      for (Map.Entry<String, NodeMetadata> poolNode : poolNodesToUnassign) {
         unassignNode(poolNode.getValue().getId());
      }
      return Sets.newHashSet(transform(poolNodesToUnassign,
               new Function<Map.Entry<String, NodeMetadata>, NodeMetadata>() {
                  @Override
                  public NodeMetadata apply(final Map.Entry<String, NodeMetadata> input) {
                     assignments.remove(input.getKey(), input.getValue());
                     return toFrontendNodemetadata(input.getValue(), input.getKey());
                  }
               }));

   }

   /**
    * Adds nodes to the pool, using the pool's group name. Lock the pool so that no-one tries to
    * increase/decrease until we're finished but we'll return from the method well before the pool
    * as enough nodes.
    */
   private ListenableFuture<Void> increasePoolSize(final int size) {
      lock.lock();
      logger.debug(">> increasing pool size,  available: %s total: %s min; %s max: %s increasing to: %s", available
               .size(), poolNodes.size(), minSize, maxSize, size);
      return Futures.makeListenable(executor.submit(new Callable<Void>() {
         @Override
         public Void call() throws Exception {
            try {
               Set<? extends NodeMetadata> original = backingComputeService.createNodesInGroup(poolGroupName, size,
                        template);
               poolNodes.addAll(original);
               available.addAll(original);
               logger.debug("<< pool size increased, available: %s total: %s min; %s max: %s increasing to: %s",
                        available.size(), poolNodes.size(), minSize, maxSize, size);
               if (started.compareAndSet(false, true)) {
                  logger.info("pool started, status: %s min; %s max: %s", available.size(), minSize, maxSize);
               }
               return null;
            } finally {
               lock.unlock();
            }
         }
      }), executor);
   }

   /**
    * Unassigns the node with the provided id. If the we're set to reuse the nodes it adds it to the
    * available pool, if not is destroys the backing node, removes if from the poll and increases
    * the pool size by one.
    */
   private NodeMetadata unassignNode(final String nodeId) {
      Map.Entry<String, NodeMetadata> entry = findAssigmentEntry(nodeId);
      assignments.remove(entry.getKey(), entry.getValue());
      // if we're reusing destroyed simply add to the available nodes
      if (reuseDestroyed) {
         available.add(entry.getValue());
         return entry.getValue();
      }
      // if not we need to destroy the backing node
      lock.lock();
      try {
         backingComputeService.destroyNode(nodeId);
         removeIf(poolNodes, new Predicate<NodeMetadata>() {
            @Override
            public boolean apply(NodeMetadata input) {
               return input.getId().equals(nodeId);
            }
         });
         if (poolNodes.size() < minSize) {
            increasePoolSize(1);
         }
      } finally {
         lock.unlock();
      }
      return entry.getValue();
   }

   /**
    * Used to assign size pool nodes to a group. If not enough nodes are available we check if we
    * can increase the pool if that is enough, otherwise we complain.
    */
   private Set<? extends NodeMetadata> assignPoolNodes(String groupName, int size) throws InterruptedException,
            ExecutionException {
      if (available.size() < size) {
         if (poolNodes.size() + size > maxSize) {
            // TODO think of a better exception
            throw new IllegalStateException(
                     "not enough nodes available  and cannot add enough nodes to pool [available: " + available.size()
                              + " total: " + poolNodes.size() + " min: " + minSize + " max: " + maxSize
                              + " requested: " + size + "]");
         }
         increasePoolSize(size - available.size()).get();
      }
      Set<NodeMetadata> groupNodes = Sets.newHashSet();
      Iterator<NodeMetadata> iter = available.iterator();
      for (int i = 0; i < size && iter.hasNext(); i++) {
         NodeMetadata node = iter.next();
         assignments.put(groupName, node);
         iter.remove();
         groupNodes.add(toFrontendNodemetadata(node, groupName));
      }
      return groupNodes;
   }

   @Override
   public ListenableFuture<Void> startPool() {
      return increasePoolSize(minSize);
   }

   @Override
   public void close() {
      // lock just to make sure we have the correct pool size
      if (started.compareAndSet(true, false)) {
         logger.info("Closing pooled compute service with {} nodes", size());
         available.clear();
         assignments.clear();
         backingComputeService.destroyNodesMatching(NodePredicates.inGroup(poolGroupName));
      }
   }

   @Override
   public int ready() {
      return available.size();
   }

   @Override
   public int size() {
      return poolNodes.size();
   }

   @Override
   public int maxSize() {
      return maxSize;
   }

}
