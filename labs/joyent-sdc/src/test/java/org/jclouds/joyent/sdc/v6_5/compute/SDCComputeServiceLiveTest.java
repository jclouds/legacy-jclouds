package org.jclouds.joyent.sdc.v6_5.compute;

import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "SDCComputeServiceLiveTest")
public class SDCComputeServiceLiveTest extends BaseComputeServiceLiveTest {

   public SDCComputeServiceLiveTest() {
      provider = "joyent-sdc";
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }
   

}
