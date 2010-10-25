package org.jclouds.libvirt.compute;

import static org.testng.Assert.assertNotNull;

import java.util.Properties;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.StandaloneComputeServiceContextSpec;
import org.jclouds.libvirt.Datacenter;
import org.jclouds.libvirt.Image;
import org.jclouds.libvirt.compute.domain.LibvirtComputeServiceContextModule;
import org.libvirt.Domain;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

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
            .createContext(new StandaloneComputeServiceContextSpec<Domain, Domain, Image, Datacenter>("libvirt",
                  "test:///default", "1", "identity", "credential", new LibvirtComputeServiceContextModule(),
                  ImmutableSet.<Module> of()));
      System.err.println(context.getComputeService().listNodes());
      context.close();
   }

}
