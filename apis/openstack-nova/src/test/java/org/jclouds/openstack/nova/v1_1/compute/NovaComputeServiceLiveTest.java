package org.jclouds.openstack.nova.v1_1.compute;

import java.util.Properties;

import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.openstack.nova.v1_1.config.NovaProperties;
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

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      setIfTestSystemPropertyPresent(props, KeystoneProperties.CREDENTIAL_TYPE);
      setIfTestSystemPropertyPresent(props, NovaProperties.AUTO_ALLOCATE_FLOATING_IPS);
      return props;
   }
}
