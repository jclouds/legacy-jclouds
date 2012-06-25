package org.jclouds.nodepool;

import org.jclouds.compute.StubComputeServiceIntegrationTest;
import org.jclouds.compute.stub.config.StubComputeServiceContextModule;
import org.jclouds.filesystem.config.FilesystemBlobStoreContextModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

@Test(groups = "unit", testName = "NodePoolComputeServiceTest")
public class NodePoolComputeServiceStubTest extends StubComputeServiceIntegrationTest {

   public NodePoolComputeServiceStubTest() {
      provider = "nodepool";
   }

   @Override
   protected Iterable<Module> setupModules() {
      return ImmutableSet.<Module> of(new StubComputeServiceContextModule(), new FilesystemBlobStoreContextModule());
   }

   // public void testStartPool() throws InterruptedException, ExecutionException, RunNodesException
   // {
   // NodePoolStats stats = nodePoolComputeServiceContext.getPoolStats();
   //
   // assertEquals(stats.idleNodes(), 5);
   // assertEquals(stats.currentSize(), 5);
   // assertEquals(stats.maxNodes(), 10);
   // }
   //
   // @Test(dependsOnMethods = "testStartPool", groups = { "unit", "poolStarted" })
   // public void testAllocateMinNodes() throws RunNodesException {
   // this.nodePoolComputeService.createNodesInGroup("1", 5);
   // NodePoolStats stats = nodePoolComputeServiceContext.getPoolStats();
   // // this pool is not supposed to add nodes past min until we request them
   // assertEquals(stats.idleNodes(), 0);
   // assertEquals(stats.currentSize(), 5);
   // }
   //
   // @Test(dependsOnMethods = "testAllocateMinNodes", groups = { "unit", "poolStarted" })
   // public void testAllocateUpToMaxNodes() throws RunNodesException {
   // this.nodePoolComputeService.createNodesInGroup("2", 5);
   // NodePoolStats stats = nodePoolComputeServiceContext.getPoolStats();
   // assertEquals(stats.idleNodes(), 0);
   // assertEquals(stats.currentSize(), 10);
   // }
   //
   // @Test(dependsOnMethods = "testAllocateUpToMaxNodes", groups = { "unit", "poolStarted" },
   // expectedExceptions = RunNodesException.class)
   // public void testAllocateMoreNodesFails() throws RunNodesException {
   // this.nodePoolComputeService.createNodesInGroup("3", 5);
   // NodePoolStats stats = nodePoolComputeServiceContext.getPoolStats();
   // }
   //
   // @Test(dependsOnMethods = "testAllocateUpToMaxNodes", groups = { "unit", "poolStarted" })
   // public void testDeallocatingNodesAndReallocating() throws RunNodesException {
   // this.nodePoolComputeService.destroyNodesMatching(NodePredicates.inGroup("2"));
   // NodePoolStats stats = nodePoolComputeServiceContext.getPoolStats();
   // assertEquals(stats.idleNodes(), 5);
   // this.nodePoolComputeService.createNodesInGroup("2", 5);
   // }
   //
   // @Test(dependsOnGroups = "poolStarted")
   // public void testClose() throws IOException {
   // NodePoolStats stats = nodePoolComputeServiceContext.getPoolStats();
   // ((Closeable) this.nodePoolComputeService).close();
   // assertEquals(stats.currentSize(), 0);
   // }

}
