package org.jclouds.joyent.cloudapi.v6_5.compute;

import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "JoyentCloudComputeServiceLiveTest")
public class JoyentCloudComputeServiceLiveTest extends BaseComputeServiceLiveTest {

   public JoyentCloudComputeServiceLiveTest() {
      provider = "joyent-cloudapi";
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }
   

}
