package org.jclouds.vi.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

import java.util.Properties;

import org.jclouds.compute.BaseComputeServiceLiveTest;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.rest.RestContext;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.Test;

import com.vmware.vim25.mo.ServiceInstance;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, sequential = true, testName = "vi.ViComputeServiceLiveTest")
public class ViComputeServiceLiveTest extends BaseComputeServiceLiveTest {
   public ViComputeServiceLiveTest() {
      provider = "vi";
   }

   @Override
   protected Properties getRestProperties() {
      Properties restProperties = new Properties();
      restProperties.setProperty("vi.contextbuilder", ViComputeServiceContextBuilder.class.getName());
      restProperties.setProperty("vi.propertiesbuilder", ViPropertiesBuilder.class.getName());
      restProperties.setProperty("vi.endpoint", "https://localhost/sdk");
      return restProperties;
   }

   @Test
   public void testTemplateBuilder() {
      Template defaultTemplate = client.templateBuilder().build();
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getVersion(), "5.3");
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.CENTOS);
      assertEquals(defaultTemplate.getLocation().getId(), "1");
      assertEquals(getCores(defaultTemplate.getHardware()), 0.5d);
   }

   @Override
   protected JschSshClientModule getSshModule() {
      return new JschSshClientModule();
   }

   public void testAssignability() throws Exception {
      @SuppressWarnings("unused")
      RestContext<ServiceInstance, ServiceInstance> goGridContext = new ComputeServiceContextFactory().createContext(
            provider, identity, credential).getProviderSpecificContext();
   }
}
