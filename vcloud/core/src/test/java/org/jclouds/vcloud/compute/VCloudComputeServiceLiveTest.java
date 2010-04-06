package org.jclouds.vcloud.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.util.Map.Entry;

import org.jclouds.compute.BaseComputeServiceLiveTest;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.rest.RestContext;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * 
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
   protected JschSshClientModule getSshModule() {
      return new JschSshClientModule();
   }

   public void testAssignability() throws Exception {
      @SuppressWarnings("unused")
      RestContext<VCloudAsyncClient, VCloudClient> tmContext = new ComputeServiceContextFactory()
               .createContext(service, user, password).getProviderSpecificContext();
   }

   @Override
   public void testListNodes() throws Exception {
      for (Entry<String, ? extends ComputeMetadata> node : client.getNodes().entrySet()) {
         assertEquals(node.getKey(), node.getValue().getId());
         assert node.getValue().getId() != null;
         assert node.getValue().getLocationId() != null;
         assertEquals(node.getValue().getType(), ComputeType.NODE);
         NodeMetadata allData = client.getNodeMetadata(node.getValue());
         assert allData.getExtra().get("processor/count") != null;
         assert allData.getExtra().get("disk_drive/1/kb") != null;
         assert allData.getExtra().get("memory/mb") != null;
      }
   }
}