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
            .createContext(new ViComputeServiceContextSpec("https://localhost/sdk", "Administrator", "password"));
      context.getComputeService().listNodes();
      context.close();
   }
}
