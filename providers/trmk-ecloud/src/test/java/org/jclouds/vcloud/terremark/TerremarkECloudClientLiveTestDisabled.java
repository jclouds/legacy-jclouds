/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vcloud.terremark;

import static org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions.Builder.processorCount;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.domain.Credentials;
import org.jclouds.net.IPSocket;
import org.jclouds.ssh.SshClient;
import org.jclouds.vcloud.terremark.domain.InternetService;
import org.jclouds.vcloud.terremark.domain.Protocol;
import org.jclouds.vcloud.terremark.domain.PublicIpAddress;
import org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code TerremarkVCloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = false, sequential = true)
public class TerremarkECloudClientLiveTestDisabled extends TerremarkClientLiveTest {
   @BeforeClass
   void setProvider() {
      this.provider = "trmk-ecloud";
      this.itemName = "Ubuntu 8.04 LTS (x86)";
   }

   @Override
   TerremarkInstantiateVAppTemplateOptions createInstantiateOptions() {
      return processorCount(1).memory(512);
   }

   @Override
   protected SshClient getConnectionFor(IPSocket socket) {
      return sshFactory.create(socket, new Credentials("ecloud", "$Ep455l0ud!2"));
   }

   @Override
   protected Entry<InternetService, PublicIpAddress> getNewInternetServiceAndIpForSSH(URI vdc) {
      PublicIpAddress ip = TerremarkECloudClient.class.cast(tmClient).activatePublicIpInVDC(
               tmClient.findVDCInOrgNamed(null, null).getHref());
      InternetService is = tmClient.addInternetServiceToExistingIp(ip.getId(), "SSH", Protocol.TCP, 22);
      Map<InternetService, PublicIpAddress> result = ImmutableMap.<InternetService, PublicIpAddress> of(is, ip);
      Entry<InternetService, PublicIpAddress> entry = Iterables.getOnlyElement(result.entrySet());
      return entry;
   }

}
