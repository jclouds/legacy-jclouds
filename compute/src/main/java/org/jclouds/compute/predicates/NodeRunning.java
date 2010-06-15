package org.jclouds.compute.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * 
 * Tests to see if a node is active.
 * 
 * @author Adrian Cole
 */
@Singleton
public class NodeRunning implements Predicate<NodeMetadata> {

   private final ComputeService client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public NodeRunning(ComputeService client) {
      this.client = client;
   }

   public boolean apply(NodeMetadata node) {
      logger.trace("looking for state on node %s", checkNotNull(node, "node"));
      node = refresh(node);
      if (node == null)
         return false;
      logger.trace("%s: looking for node state %s: currently: %s",
            node.getId(), NodeState.RUNNING, node.getState());
      if (node.getState() == NodeState.ERROR)
         throw new IllegalStateException("node " + node.getId()
               + " in location " + node.getLocation() + " is in error state");
      return node.getState() == NodeState.RUNNING;
   }

   private NodeMetadata refresh(NodeMetadata node) {
      return client.getNodeMetadata(node.getId());
   }
}
