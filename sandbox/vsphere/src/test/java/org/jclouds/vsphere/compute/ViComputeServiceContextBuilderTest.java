package org.jclouds.vsphere.compute;

import static org.testng.Assert.assertNotNull;

import java.util.Properties;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.vsphere.compute.ViComputeServiceContextBuilder;
import org.jclouds.vsphere.compute.ViComputeServiceContextSpec;
import org.testng.annotations.Test;

/**
 * 
 * @author andrea.turli
 *
 */
@Test(groups = "unit")
public class ViComputeServiceContextBuilderTest {

   @Test
   public void testCreateContextModule() {
      assertNotNull(new ViComputeServiceContextBuilder(new Properties()).createContextModule());
   }

   @Test
   public void testCanBuildWithComputeService() {
      ComputeServiceContext context = new ComputeServiceContextFactory()
            .createContext(new ViComputeServiceContextSpec("https://10.38.102.196/sdk", "Administrator", "41.U17Sh"));
      context.getComputeService().listNodes();
      context.close();
   }
}
