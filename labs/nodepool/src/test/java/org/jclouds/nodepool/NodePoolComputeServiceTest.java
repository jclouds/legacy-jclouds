package org.jclouds.nodepool;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.nodepool.internal.EagerNodePoolComputeService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "NodePoolComputeServiceTest")
public class NodePoolComputeServiceTest {

   private EagerNodePoolComputeService pooledComputeService;

   @BeforeClass
   public void setUp() {
      ComputeServiceContext stubCtx = ContextBuilder.newBuilder("stub").buildView(ComputeServiceContext.class);
      this.pooledComputeService = new EagerNodePoolComputeService(stubCtx, "pool", 10, 5, true, stubCtx
               .getComputeService().templateBuilder().build());
   }

   public void testStartPool() throws InterruptedException, ExecutionException, RunNodesException {
      this.pooledComputeService.startPool();
      assertEquals(pooledComputeService.idleNodes(), 5);
      assertEquals(pooledComputeService.currentSize(), 5);
      assertEquals(pooledComputeService.maxNodes(), 10);
   }

   @Test(dependsOnMethods = "testStartPool", groups = { "unit", "poolStarted" })
   public void testAllocateMinNodes() throws RunNodesException {
      this.pooledComputeService.createNodesInGroup("1", 5);
      // this pool is not supposed to add nodes past min until we request them
      assertEquals(pooledComputeService.idleNodes(), 0);
      assertEquals(pooledComputeService.currentSize(), 5);
   }

   @Test(dependsOnMethods = "testAllocateMinNodes", groups = { "unit", "poolStarted" })
   public void testAllocateUpToMaxNodes() throws RunNodesException {
      this.pooledComputeService.createNodesInGroup("2", 5);
      assertEquals(pooledComputeService.idleNodes(), 0);
      assertEquals(pooledComputeService.currentSize(), 10);
   }

   @Test(dependsOnMethods = "testAllocateUpToMaxNodes", groups = { "unit", "poolStarted" }, expectedExceptions = RunNodesException.class)
   public void testAllocateMoreNodesFails() throws RunNodesException {
      this.pooledComputeService.createNodesInGroup("3", 5);
      System.out.println(this.pooledComputeService.currentSize());
   }

   @Test(dependsOnMethods = "testAllocateUpToMaxNodes", groups = { "unit", "poolStarted" })
   public void testDeallocatingNodesAndReallocating() throws RunNodesException {
      this.pooledComputeService.destroyNodesMatching(NodePredicates.inGroup("2"));
      assertEquals(pooledComputeService.idleNodes(), 5);
      this.pooledComputeService.createNodesInGroup("2", 5);
   }

   @Test(dependsOnGroups = "poolStarted")
   public void testClose() throws IOException {
      this.pooledComputeService.close();
      assertEquals(pooledComputeService.currentSize(), 0);
   }

}
