package org.jclouds.servermanager.compute;

import static org.testng.Assert.assertNotNull;

import java.util.Properties;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.StandaloneComputeServiceContextSpec;
import org.jclouds.servermanager.Datacenter;
import org.jclouds.servermanager.Hardware;
import org.jclouds.servermanager.Image;
import org.jclouds.servermanager.Server;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

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
      ComputeServiceContext context = new ComputeServiceContextFactory()
            .createContext(new StandaloneComputeServiceContextSpec<Server, Hardware, Image, Datacenter>(
                  "servermanager", "http://host", "1", "identity", "credential",
                  ServerManagerComputeServiceContextBuilder.createContextModule(), ImmutableSet.<Module> of()));

      context.close();

   }
}
