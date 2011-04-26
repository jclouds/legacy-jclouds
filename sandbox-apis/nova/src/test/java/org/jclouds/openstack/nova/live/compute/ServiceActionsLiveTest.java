package org.jclouds.openstack.nova.live.compute;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.jclouds.compute.predicates.NodePredicates.inGroup;

/**
 * @author Victor Galkin
 */
public class ServiceActionsLiveTest extends ComputeBase {

   static private String group = "ServiceActionsLiveTest";

   @BeforeTest
   @Override
   public void before() throws IOException, ExecutionException, TimeoutException, InterruptedException {
      super.before();
      computeService.destroyNodesMatching(inGroup(group));
   }

   @Test
   public void testReboot() throws Exception {
      getDefaultNodeImmediately(group);
      computeService.rebootNodesMatching(inGroup(group));// TODO test
      Thread.sleep(5000);
      //        // validation
      //testGetNodeMetadata();
   }

   @Test
   public void testSuspendResume() throws Exception {
      getDefaultNodeImmediately(group);
      computeService.suspendNodesMatching(inGroup(group));

      Set<? extends NodeMetadata> stoppedNodes = getFreshNodes(group);

      assert Iterables.all(stoppedNodes, new Predicate<NodeMetadata>() {

         @Override
         public boolean apply(NodeMetadata input) {
            boolean returnVal = input.getState() == NodeState.SUSPENDED;
            if (!returnVal)
               System.err.printf("warning: node %s in state %s%n", input.getId(), input.getState());
            return returnVal;
         }

      }) : stoppedNodes;

      computeService.resumeNodesMatching(inGroup(group));
      //testGetNodeMetadata();
   }

   @AfterTest
   protected void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      computeService.destroyNodesMatching(inGroup(group));
      context.close();
   }

}
