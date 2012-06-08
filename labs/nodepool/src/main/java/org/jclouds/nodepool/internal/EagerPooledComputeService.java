package org.jclouds.nodepool.internal;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.removeIf;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.nodepool.PooledComputeServiceConstants.NODEPOOL_BACKING_GROUP_PROPERTY;
import static org.jclouds.nodepool.PooledComputeServiceConstants.NODEPOOL_BACKING_TEMPLATE_PROPERTY;
import static org.jclouds.nodepool.PooledComputeServiceConstants.NODEPOOL_MAX_SIZE_PROPERTY;
import static org.jclouds.nodepool.PooledComputeServiceConstants.NODEPOOL_MIN_SIZE_PROPERTY;
import static org.jclouds.nodepool.PooledComputeServiceConstants.NODEPOOL_REMOVE_DESTROYED_PROPERTY;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.concurrent.Futures;
import org.jclouds.logging.Logger;
import org.jclouds.nodepool.PooledComputeService;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * An eager {@link PooledComputeService}. Eagerly build and maitains a pool of nodes. It's only
 * "started" after min nodes are allocated and available.
 * 
 * @author David Alves
 * 
 */
public class EagerPooledComputeService extends BasePooledComputeService {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final AtomicBoolean started = new AtomicBoolean(false);
   private final int maxSize;
   private final boolean reuseDestroyed;
   private final int minSize;
   private final Template template;
   private final ExecutorService executor;

   // assignments of nodes to group names
   private Multimap<String, NodeMetadata> assignments = HashMultimap.create();

   // set of available nodes
   private Set<NodeMetadata> available = Sets.newHashSet();

   // lock associated with changes to the pool since they happen asynchronously
   private final Lock lock = new ReentrantLock();
   private final Set<NodeMetadata> poolNodes = Sets.newLinkedHashSet();

   @Inject
   public EagerPooledComputeService(ComputeService backingComputeService,
            @Named(NODEPOOL_BACKING_GROUP_PROPERTY) String poolGroupPrefix,
            @Named(NODEPOOL_MAX_SIZE_PROPERTY) int maxSize, @Named(NODEPOOL_MIN_SIZE_PROPERTY) int minSize,
            @Named(NODEPOOL_REMOVE_DESTROYED_PROPERTY) boolean readdDestroyed,
            @Nullable @Named(NODEPOOL_BACKING_TEMPLATE_PROPERTY) Template backingTemplate,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      super(backingComputeService, poolGroupPrefix);
      this.maxSize = maxSize;
      this.minSize = minSize;
      this.reuseDestroyed = readdDestroyed;
      this.template = backingTemplate == null ? backingComputeService.templateBuilder().build() : backingTemplate;
      this.executor = executor;
   }

   @Override
   public ListenableFuture<Void> startPool() throws RunNodesException {
      return increasePoolSize(minSize);
   }

   @Override
   public synchronized Set<? extends NodeMetadata> createNodesInGroup(String group, int count) throws RunNodesException {
      checkState(started.get(), "pool is not started");
      try {
         return assignPoolNodes(group, count);
      } catch (Exception e) {
         // TODO propagate a better exception?
         throw Throwables.propagate(e);
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
      Iterable<Map.Entry<String, NodeMetadata>> poolNodesToUnassign = filterBasedOnUserPredicate(filter);

      // TODO this should be done in parallel since it can take quite a while, moreover the contract
      // for any destroy node action should probably be that the pool has at least minSize nodes
      // before it returns. need to think it through a bit better.
      for (Map.Entry<String, NodeMetadata> poolNode : poolNodesToUnassign) {
         destroyNode(poolNode.getValue().getId());
      }

      return Sets.newHashSet(transform(poolNodesToUnassign,
               new Function<Map.Entry<String, NodeMetadata>, PoolNodeMetadata>() {
                  @Override
                  public PoolNodeMetadata apply(final Map.Entry<String, NodeMetadata> input) {
                     assignments.remove(input.getKey(), input.getValue());
                     return new PoolNodeMetadata(input.getValue(), input.getKey());
                  }
               }));

   }

   @Override
   public void rebootNodesMatching(final Predicate<NodeMetadata> filter) {
      transform(filterBasedOnUserPredicate(filter), new Function<Map.Entry<String, NodeMetadata>, NodeMetadata>() {
         @Override
         public NodeMetadata apply(Entry<String, NodeMetadata> input) {
            backingComputeService.rebootNode(input.getValue().getId());
            return null;
         }
      });
   }

   @Override
   public void resumeNodesMatching(Predicate<NodeMetadata> filter) {
      transform(filterBasedOnUserPredicate(filter), new Function<Map.Entry<String, NodeMetadata>, NodeMetadata>() {
         @Override
         public NodeMetadata apply(Entry<String, NodeMetadata> input) {
            backingComputeService.resumeNode(input.getValue().getId());
            return null;
         }
      });

   }

   @Override
   public void suspendNodesMatching(Predicate<NodeMetadata> filter) {
      transform(filterBasedOnUserPredicate(filter), new Function<Map.Entry<String, NodeMetadata>, NodeMetadata>() {
         @Override
         public NodeMetadata apply(Entry<String, NodeMetadata> input) {
            backingComputeService.suspendNode(input.getValue().getId());
            return null;
         }
      });
   }

   @Override
   public Set<? extends ComputeMetadata> listNodes() {
      return Sets.newHashSet(transform(assignments.entries(),
               new Function<Map.Entry<String, NodeMetadata>, PoolNodeMetadata>() {
                  @Override
                  public PoolNodeMetadata apply(Map.Entry<String, NodeMetadata> input) {
                     return new PoolNodeMetadata(input.getValue(), input.getKey());
                  }
               }));
   }

   @Override
   public Set<? extends NodeMetadata> listNodesDetailsMatching(Predicate filter) {
      return Sets.newHashSet(transform(filterBasedOnUserPredicate(filter),
               new Function<Map.Entry<String, NodeMetadata>, NodeMetadata>() {
                  @Override
                  public NodeMetadata apply(Entry<String, NodeMetadata> input) {
                     return new PoolNodeMetadata(input.getValue(), input.getKey());
                  }
               }));
   }

   @Override
   public NodeMetadata getNodeMetadata(String id) {
      Map.Entry<String, NodeMetadata> assigmentEntry = findAssigmentEntry(id);
      return new PoolNodeMetadata(assigmentEntry.getValue(), assigmentEntry.getKey());
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
   public boolean isStarted() {
      return started.get();
   }

   @Override
   public int getReady() {
      return poolNodes.size();
   }

   @Override
   public int size() {
      return maxSize;
   }

   /**
    * Because a lot of predicates are based on group info we need that to check wether the predicate
    * matches.
    */
   private Iterable<Map.Entry<String, NodeMetadata>> filterBasedOnUserPredicate(final Predicate<NodeMetadata> userFilter) {
      return filter(assignments.entries(), new Predicate<Map.Entry<String, NodeMetadata>>() {
         @Override
         public boolean apply(Entry<String, NodeMetadata> input) {
            return userFilter.apply(new PoolNodeMetadata(input.getValue(), input.getKey()));
         }
      });
   }

   private Map.Entry<String, NodeMetadata> findAssigmentEntry(final String id) {
      // TODO reverse lookup data structure would be faster but will pools be that big ?
      return find(assignments.entries(), new Predicate<Map.Entry<String, NodeMetadata>>() {
         @Override
         public boolean apply(Entry<String, NodeMetadata> entry) {
            return entry.getValue().getId().equals(id);
         }
      });
   }

   /**
    * Adds nodes to the pool, using the pool's group name. Lock the pool so that no-one tries to
    * increase/decrease until we're finished but we'll return from the method well before the pool
    * as enough nodes.
    */
   private ListenableFuture<Void> increasePoolSize(int size) {
      lock.lock();
      return Futures.makeListenable(executor.submit(new Callable<Void>() {
         @Override
         public Void call() throws Exception {
            try {
               Set<? extends NodeMetadata> original = backingComputeService.createNodesInGroup(poolGroupName, minSize,
                        template);
               poolNodes.addAll(original);
               started.set(true);
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
      Map.Entry<String, NodeMetadata> assignmentEntry = find(assignments.entries(),
               new Predicate<Map.Entry<String, NodeMetadata>>() {
                  @Override
                  public boolean apply(Entry<String, NodeMetadata> entry) {
                     return entry.getValue().getId().equals(nodeId);
                  }
               });

      assignments.remove(assignmentEntry.getKey(), assignmentEntry.getValue());
      // if we're reusing destroyed simply add to the available nodes
      if (reuseDestroyed) {
         available.add(assignmentEntry.getValue());
         return assignmentEntry.getValue();
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
      return assignmentEntry.getValue();
   }

   /**
    * Used to assign size pool nodes to a group. If not enough nodes are available we check if we
    * can increase the pool and if that is enough, otherwise we complain.
    */
   private Set<? extends NodeMetadata> assignPoolNodes(String groupName, int size) throws InterruptedException,
            ExecutionException {
      if (available.size() < size) {
         if (available.size() + maxSize - poolNodes.size() > size) {
            // TODO think of a better exception
            throw new IllegalStateException(
                     "not enough node available to fulfill request and cannot add enough nodes to pool");
         }
         increasePoolSize(size - available.size()).get();
      }
      Set<PoolNodeMetadata> groupNodes = Sets.newHashSet();
      Iterator<NodeMetadata> iter = available.iterator();
      for (int i = 0; i < size && iter.hasNext(); i++) {
         NodeMetadata node = iter.next();
         assignments.put(groupName, node);
         iter.remove();
         groupNodes.add(new PoolNodeMetadata(node, groupName));
      }
      return groupNodes;
   }

}
