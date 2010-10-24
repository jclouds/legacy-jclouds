package org.jclouds.libvirt.compute;

import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.Properties;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.config.StandaloneComputeServiceContextModule;
import org.jclouds.libvirt.Datacenter;
import org.jclouds.libvirt.Hardware;
import org.jclouds.libvirt.Image;
import org.jclouds.libvirt.compute.domain.LibvirtComputeServiceContextModule;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Test(groups = "unit")
public class LibvirtComputeServiceContextBuilderTest {
//
//   @Test
//   public void testCreateContextModule() {
//      assertNotNull(new LibvirtComputeServiceContextBuilder(new Properties()).createContextModule());
//   }
//
//   @Test
//   public void testCanBuildDirectly() {
//      ComputeServiceContext context = new LibvirtComputeServiceContextBuilder(new Properties()) {
//
//         @Override
//         public StandaloneComputeServiceContextModule<Domain, Hardware, Image, Datacenter> createContextModule() {
//            return new StubLibvirtComputeServiceContextModule();
//         }
//
//      }.buildComputeServiceContext();
//      context.close();
//   }
//
//   @Test
//   public void testCanBuildWithComputeService() {
//      ComputeServiceContext context = ComputeServiceContextFactory
//            .createStandaloneContext(new StubLibvirtComputeServiceContextModule());
//      context.close();
//
//   }

   private static class StubLibvirtComputeServiceContextModule extends LibvirtComputeServiceContextModule {

      @Override
      protected Connect createConnection(URI endpoint, String identity, String credential) throws LibvirtException {
         // TODO replace with mock
         return null;
      }

   }
}
