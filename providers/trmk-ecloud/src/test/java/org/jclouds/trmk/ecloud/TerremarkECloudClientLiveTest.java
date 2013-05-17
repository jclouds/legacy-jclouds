/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.ecloud;

import java.util.Map.Entry;
import java.util.Properties;

import org.jclouds.domain.LoginCredentials;
import org.jclouds.ssh.SshClient;
import org.jclouds.trmk.ecloud.suppliers.TerremarkECloudInternetServiceAndPublicIpAddressSupplier;
import org.jclouds.trmk.vcloud_0_8.TerremarkClientLiveTest;
import org.jclouds.trmk.vcloud_0_8.domain.InternetService;
import org.jclouds.trmk.vcloud_0_8.domain.Protocol;
import org.jclouds.trmk.vcloud_0_8.domain.PublicIpAddress;
import org.jclouds.trmk.vcloud_0_8.domain.VApp;
import org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants;
import org.testng.annotations.Test;

import com.google.common.net.HostAndPort;

/**
 * Tests behavior of {@code TerremarkECloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, singleThreaded = true, testName = "TerremarkECloudClientLiveTest")
public class TerremarkECloudClientLiveTest extends TerremarkClientLiveTest {

   public TerremarkECloudClientLiveTest() {
      this.provider = "trmk-ecloud";
      this.itemName = "Ubuntu Server 10.04 x64";
      this.expectedOs = "Ubuntu Linux (64-bit)";
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.setProperty(VCloudConstants.PROPERTY_VCLOUD_DEFAULT_VDC,
            ".* - " + System.getProperty("test.trmk-ecloud.datacenter", "MIA"));
      return props;
   }

   @Override
   protected SshClient getConnectionFor(HostAndPort socket) {
      return sshFactory.create(socket, LoginCredentials.builder().user("ecloud").password("TmrkCl0ud1s#1!").privateKey(
               key.getPrivateKey()).authenticateSudo(true).build());
   }

   @Override
   protected Entry<InternetService, PublicIpAddress> getNewInternetServiceAndIpForSSH(VApp vApp) {
      return new TerremarkECloudInternetServiceAndPublicIpAddressSupplier(TerremarkECloudClient.class.cast(api))
            .getNewInternetServiceAndIp(vApp, 22, Protocol.TCP);
   }

}
