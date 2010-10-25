package org.jclouds.libvirt.compute;

import static org.easymock.classextension.EasyMock.createMock;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.Properties;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.StandaloneComputeServiceContextSpec;
import org.jclouds.libvirt.Datacenter;
import org.jclouds.libvirt.Hardware;
import org.jclouds.libvirt.Image;
import org.jclouds.libvirt.compute.domain.LibvirtComputeServiceContextModule;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
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
            .createContext(new StandaloneComputeServiceContextSpec<Domain, Hardware, Image, Datacenter>("libvirt",
                  "stub", "1", "identity", "credential", new StubLibvirtComputeServiceContextModule(), ImmutableSet
                        .<Module> of()));
      context.close();
   }

   private static class StubLibvirtComputeServiceContextModule extends LibvirtComputeServiceContextModule {

      @Override
      protected Connect createConnection(URI endpoint, String identity, String credential) throws LibvirtException {
         return createMock(Connect.class);
      }

   }
}
