package org.jclouds.nodepool;

import java.util.Properties;

import org.jclouds.compute.StubComputeServiceIntegrationTest;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.testng.annotations.Test;

import com.google.common.base.Joiner;

@Test(groups = "live", testName = "NodePoolStubTest")
public class NodePoolStubIntegrationTest extends StubComputeServiceIntegrationTest {

   public NodePoolStubIntegrationTest() {
      provider = "nodepool";
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.put("jclouds.identity", "foo");
      props.put("jclouds.nodepool.backend-provider", "stub");
      props.put("jclouds.nodepool.basedir", "target/test-data");
      props.put("jclouds.nodepool.backend-modules",
               Joiner.on(",").join(getSshModule().getClass().getName(), SLF4JLoggingModule.class.getName()));
      return props;
   }

}
