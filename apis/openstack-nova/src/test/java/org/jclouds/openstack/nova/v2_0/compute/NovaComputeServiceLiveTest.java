package org.jclouds.openstack.nova.v2_0.compute;

import static java.util.logging.Logger.getAnonymousLogger;

import java.util.Properties;

import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.openstack.nova.v2_0.config.NovaProperties;
import org.jclouds.rest.AuthorizationException;
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

   @Test(enabled = true, dependsOnMethods = "testReboot")
   public void testSuspendResume() throws Exception {
      try {
         // may fail because of lack of AdminActions extension or non-admin user, so log and continue
         super.testSuspendResume();
      } catch (AuthorizationException e) {
         getAnonymousLogger().info("testSuspendResume() threw, probably due to lack of privileges: " + e.getMessage());
      } catch (UnsupportedOperationException e) {
         getAnonymousLogger().info("testSuspendResume() threw, probably due to unavailable AdminActions extension: " + e.getMessage());
      }
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
