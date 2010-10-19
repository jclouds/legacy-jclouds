package org.jclouds.servermanager.compute;

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
public class ServerManagerComputeServiceContextBuilderTest {

   @Test
   public void testCreateContextModule() {
      assertNotNull(ServerManagerComputeServiceContextBuilder.createContextModule());
   }

   @Test
   public void testCanBuildDirectly() {
      ComputeServiceContext context = new ServerManagerComputeServiceContextBuilder(new Properties())
            .buildComputeServiceContext();
      context.close();
   }

   @Test
   public void testCanBuildWithComputeService() {
      ComputeServiceContext context = ComputeServiceContextFactory
            .createStandaloneContext(ServerManagerComputeServiceContextBuilder.createContextModule());
      context.close();

   }
}
