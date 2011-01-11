package org.jclouds.libvirt.compute;

import static org.testng.Assert.assertEquals;

import java.util.Properties;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.rest.RestContext;
import org.libvirt.Connect;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Test(groups = "unit")
public class LibvirtComputeServiceContextBuilderTest {

   @Test
   public void testCanBuildWithComputeService() {
      ComputeServiceContext context = new ComputeServiceContextFactory()
            .createContext(new LibvirtComputeServiceContextSpec("test:///default", "identity", "credential"));
      // System.err.println(context.getComputeService().
      context.close();
   }

   @Test
   public void testCanBuildWithRestProperties() {
      Properties restProperties = new Properties();
      restProperties.setProperty("libvirt.contextbuilder", LibvirtComputeServiceContextBuilder.class.getName());
      restProperties.setProperty("libvirt.propertiesbuilder", LibvirtPropertiesBuilder.class.getName());
      restProperties.setProperty("libvirt.endpoint", "test:///default");

      ComputeServiceContext context = new ComputeServiceContextFactory(restProperties).createContext("libvirt",
            "identity", "credential");

      context.close();
   }

   @Test
   public void testProviderSpecificContextIsCorrectType() {
      Properties restProperties = new Properties();
      restProperties.setProperty("libvirt.contextbuilder", LibvirtComputeServiceContextBuilder.class.getName());
      restProperties.setProperty("libvirt.propertiesbuilder", LibvirtPropertiesBuilder.class.getName());
      restProperties.setProperty("libvirt.endpoint", "test:///default");

      ComputeServiceContext context = new ComputeServiceContextFactory(restProperties).createContext("libvirt",
            "identity", "credential");
      
      RestContext<Connect, Connect> providerContext = context.getProviderSpecificContext();

      assertEquals(providerContext.getApi().getClass(), Connect.class);

      context.close();
   }
}
