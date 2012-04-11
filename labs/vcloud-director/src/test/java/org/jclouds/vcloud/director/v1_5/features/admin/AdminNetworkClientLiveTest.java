/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.features.admin;

import static com.google.common.base.Objects.equal;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_UPDATABLE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_REQ_LIVE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.REF_REQ_LIVE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.REQUIRED_VALUE_OBJECT_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.TASK_COMPLETE_TIMELY;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Collections;

import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.ExternalNetwork;
import org.jclouds.vcloud.director.v1_5.domain.IpScope;
import org.jclouds.vcloud.director.v1_5.domain.Network;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.NetworkFeatures;
import org.jclouds.vcloud.director.v1_5.domain.OrgNetwork;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.RouterInfo;
import org.jclouds.vcloud.director.v1_5.domain.SyslogServerSettings;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminNetworkClient;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests live behavior of {@link AdminNetworkClient}.
 * 
 * @author danikov
 */
@Test(groups = { "live", "admin" }, singleThreaded = true, testName = "AdminNetworkLiveTest")
public class AdminNetworkClientLiveTest extends BaseVCloudDirectorClientLiveTest {
   
   public static final String NETWORK = "AdminNetwork";

   /*
    * Convenience references to API clients.
    */
   private AdminNetworkClient networkClient;

   /*
    * Shared state between dependant tests.
    */
   Reference networkRef;
   Network network;
   
   @Override
   @BeforeClass(alwaysRun = true)
   protected void setupRequiredClients() {
      networkClient = adminContext.getApi().getNetworkClient();
      networkRef = Reference.builder().href(networkURI).build().toAdminReference(endpoint);
   }
   
   @Test(description = "GET /admin/network/{id}")
   public void testGetNetwork() {
      //TODO: test both org and external networks
      assertNotNull(networkRef, String.format(OBJ_REQ_LIVE, NETWORK));
      network = networkClient.getNetwork(networkRef.getHref());
      
      if(network instanceof ExternalNetwork) {
         Checks.checkExternalNetwork(Network.<ExternalNetwork>toSubType(network));
      } else if (network instanceof OrgNetwork) {
         Checks.checkOrgNetwork(Network.<OrgNetwork>toSubType(network));
      } else {
         fail(String.format(REQUIRED_VALUE_OBJECT_FMT, ".class", NETWORK, 
               network.getClass(),"ExternalNetwork,. OrgNetwork"));
      }
   }
   
   // TODO: this test is far from exhaustive
   @Test(description = "PUT /admin/network/{id}" )
   public void testUpdateNetwork() {
      //TODO: ensure network instanceof OrgNetwork, may require queries
      assertTrue(network instanceof OrgNetwork, String.format(REF_REQ_LIVE, "OrgNetwork"));
      
      OrgNetwork oldNetwork = Network.<OrgNetwork>toSubType(network)
            .toBuilder()
            .tasks(Collections.<Task>emptySet())
            .build();
      
      OrgNetwork updateNetwork = getMutatedOrgNetwork(oldNetwork);
      
      try {
         Task updateNetworkTask = networkClient.updateNetwork(network.getHref(), updateNetwork);
         Checks.checkTask(updateNetworkTask);
         assertTrue(retryTaskSuccess.apply(updateNetworkTask), String.format(TASK_COMPLETE_TIMELY, "updateNetworkTask"));
         network = networkClient.getNetwork(network.getHref());
         
         Checks.checkOrgNetwork(Network.<OrgNetwork>toSubType(network));
         
         assertTrue(equal(network.getConfiguration().getIpScope(), 
               updateNetwork.getConfiguration().getIpScope()), 
               String.format(OBJ_FIELD_UPDATABLE, NETWORK+".configuration", "ipScope"));
         assertTrue(equal(network.getConfiguration().getParentNetwork(), 
               updateNetwork.getConfiguration().getParentNetwork()), 
               String.format(OBJ_FIELD_UPDATABLE, NETWORK+".configuration", "parentNetwork"));
         assertTrue(equal(network.getConfiguration().getFenceMode(), 
               updateNetwork.getConfiguration().getFenceMode()), 
               String.format(OBJ_FIELD_UPDATABLE, NETWORK+".configuration", "fenceMode"));
         assertTrue(equal(network.getConfiguration().retainNetInfoAcrossDeployments(), 
               updateNetwork.getConfiguration().retainNetInfoAcrossDeployments()), 
               String.format(OBJ_FIELD_UPDATABLE, NETWORK+".configuration", "retainNetInfoAcrossDeployments"));
         assertTrue(equal(network.getConfiguration().getNetworkFeatures(), 
               updateNetwork.getConfiguration().getNetworkFeatures()), 
               String.format(OBJ_FIELD_UPDATABLE, NETWORK+".configuration", "networkFeatures"));
         assertTrue(equal(network.getConfiguration().getSyslogServerSettings(), 
               updateNetwork.getConfiguration().getSyslogServerSettings()), 
               String.format(OBJ_FIELD_UPDATABLE, NETWORK+".configuration", "syslogServerSettings"));
         assertTrue(equal(network.getConfiguration().getRouterInfo(), 
               updateNetwork.getConfiguration().getRouterInfo()), 
               String.format(OBJ_FIELD_UPDATABLE, NETWORK+".configuration", "routerInfo"));
         // FIXME: fails
//      assertTrue(equal(Network.<OrgNetwork>toSubType(network).getNetworkPool(), 
//            updateNetwork.getNetworkPool()), 
//            String.format(OBJ_FIELD_UPDATABLE, NETWORK, "networkPool"));
         
//      assertTrue(equal(Network.<OrgNetwork>toSubType(network).getAllowedExternalIpAddresses(), 
//            updateNetwork.getAllowedExternalIpAddresses()), 
//            String.format(OBJ_FIELD_UPDATABLE, NETWORK, "allowedExternalIpAddresses"));
      } finally {
         Task updateNetworkTask = networkClient.updateNetwork(network.getHref(), oldNetwork);
         Checks.checkTask(updateNetworkTask);
         assertTrue(retryTaskSuccess.apply(updateNetworkTask), String.format(TASK_COMPLETE_TIMELY, "updateNetworkTask"));
         network = networkClient.getNetwork(network.getHref());
      }
   }
   
   @Test(description = "POST /admin/network/{id}/action/reset")
   public void testResetNetwork() { 
      // TODO assert that network is deployed somehow
      Task resetNetworkTask = networkClient.resetNetwork(networkRef.getHref());
      Checks.checkTask(resetNetworkTask);
      assertTrue(retryTaskSuccess.apply(resetNetworkTask), String.format(TASK_COMPLETE_TIMELY, "resetNetworkTask"));
      network = networkClient.getNetwork(network.getHref());
      
      Checks.checkOrgNetwork(Network.<OrgNetwork>toSubType(network));
      // TODO: other assertions about the reset? that network is deployed when task is complete, for example
   }
   
   private static OrgNetwork getMutatedOrgNetwork(OrgNetwork network) {
       OrgNetwork.Builder<?> networkBuilder = OrgNetwork.builder().fromNetwork(network)
             .tasks(Collections.<Task>emptySet())
//           .name("new "+network.getName())
          .description("new "+network.getDescription())
          .configuration(getMutatedNetworkConfiguration(network.getConfiguration()));
       
       // FIXME: fails
//     if (network.getNetworkPool() != null) {
//        networkBuilder.networkPool(null);
//     } // TODO: else?
       
//     if (network.getAllowedExternalIpAddresses() != null) {
//        networkBuilder.allowedExternalIpAddresses(null);
//     } // TODO: else?
       
      return networkBuilder.build();
   }

   private static NetworkConfiguration getMutatedNetworkConfiguration(NetworkConfiguration config) {
      NetworkConfiguration.Builder configBuilder = config.toBuilder();
      
      if (config.getIpScope() != null) {
         configBuilder.ipScope(IpScope.builder().fromIpScope(config.getIpScope())
            // TODO: mutate to test more
            .build());
      }
      
      if (config.getParentNetwork() != null) {
//         configBuilder.parentNetwork(null);
      } // TODO: else?
      
//      configBuilder.fenceMode(config.getFenceMode() == Network.FenceMode.BRIDGED ? 
//            Network.FenceMode.BRIDGED : Network.FenceMode.ISOLATED);
      
      if (config.getSyslogServerSettings() != null) {
         configBuilder.syslogServerSettings(SyslogServerSettings.builder()
            .fromSyslogServerSettings(config.getSyslogServerSettings())
             // TODO: mutate to test more
            .build());
      }
      
      if (config.retainNetInfoAcrossDeployments() != null) {
//         configBuilder.retainNetInfoAcrossDeployments(!config.retainNetInfoAcrossDeployments());
      } else {
//         configBuilder.retainNetInfoAcrossDeployments(false);
      }
      
      if (config.getNetworkFeatures() != null) {
         configBuilder.features(NetworkFeatures.builder().fromNetworkFeatures(config.getNetworkFeatures())
            // TODO: mutate to test more
            .build());
      }
      
      if (config.getRouterInfo() != null) {
         configBuilder.routerInfo(RouterInfo.builder().fromRouterInfo(config.getRouterInfo())
            // TODO: mutate to test more
            .build());
      }
      
      return configBuilder.build();
   }
}
