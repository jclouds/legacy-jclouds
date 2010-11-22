package org.jclouds.libvirt.compute;

import static org.testng.Assert.assertNotNull;

import java.util.Properties;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Test(groups = "unit")
public class LibvirtComputeServiceContextBuilderTest {

   @Test
   public void testCreateContextModule() {
      assertNotNull(new LibvirtComputeServiceContextBuilder(new Properties()).createContextModule());
   }

   @Test
   public void testCanBuildWithComputeService() {
      ComputeServiceContext context = new ComputeServiceContextFactory()
            .createContext(new LibvirtComputeServiceContextSpec("test:///default", "identity", "credential"));
      // System.err.println(context.getComputeService().
      context.close();
   }
}
