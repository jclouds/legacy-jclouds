package org.jclouds.vcloud.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.domain.OsFamily.UBUNTU;

import org.jclouds.compute.BaseComputeServiceLiveTest;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.rest.RestContext;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * 
 * Generally disabled, as it incurs higher fees.
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, sequential = true, testName = "vcloud.VCloudComputeServiceLiveTest")
public class VCloudComputeServiceLiveTest extends BaseComputeServiceLiveTest {

   @BeforeClass
   @Override
   public void setServiceDefaults() {
      System.setProperty("vcloud.endpoint", checkNotNull(System
               .getProperty("jclouds.test.endpoint"), "jclouds.test.endpoint"));
      service = "vcloud";
   }

   @Override
   protected boolean canRunScript(Template template) {
      return false;
   }

   @Override
   protected Template buildTemplate(TemplateBuilder templateBuilder) {
      return templateBuilder.osFamily(UBUNTU).smallest().build();
   }

   @Override
   protected JschSshClientModule getSshModule() {
      return new JschSshClientModule();
   }

   public void testAssignability() throws Exception {
      @SuppressWarnings("unused")
      RestContext<VCloudAsyncClient, VCloudClient> tmContext = new ComputeServiceContextFactory()
               .createContext(service, user, password).getProviderSpecificContext();

      VCloudComputeService computeService = VCloudComputeService.class.cast(client);

      @SuppressWarnings("unused")
      VCloudComputeClient computeClient = VCloudComputeClient.class.cast(computeService);
   }
}