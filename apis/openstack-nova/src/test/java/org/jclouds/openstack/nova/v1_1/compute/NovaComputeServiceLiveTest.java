package org.jclouds.openstack.nova.v1_1.compute;

import org.jclouds.compute.BaseComputeServiceLiveTest;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "NovaComputeServiceLiveTest")
public class NovaComputeServiceLiveTest extends BaseComputeServiceLiveTest {

   public NovaComputeServiceLiveTest() {
      provider = "openstack-nova";
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }
   
   @Override
   public void testOptionToNotBlock() {
      // start call is blocking anyway.
   }

   @Test(enabled = true, dependsOnMethods = "testReboot", expectedExceptions = UnsupportedOperationException.class)
   public void testSuspendResume() throws Exception {
      super.testSuspendResume();
   }

   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   @Override
   public void testGetNodesWithDetails() throws Exception {
      super.testGetNodesWithDetails();
   }

   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   @Override
   public void testListNodes() throws Exception {
      super.testListNodes();
   }

   @Test(enabled = true, dependsOnMethods = { "testListNodes", "testGetNodesWithDetails" })
   @Override
   public void testDestroyNodes() {
      super.testDestroyNodes();
   }
}
