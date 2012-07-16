/**
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
package org.jclouds.vcloud.binders;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.vcloud.VCloudApiMetadata;
import org.jclouds.vcloud.domain.NetworkConnection;
import org.jclouds.vcloud.domain.NetworkConnectionSection;
import org.jclouds.vcloud.domain.network.IpAddressAllocationMode;
import org.nnsoft.guice.rocoto.Rocoto;
import org.nnsoft.guice.rocoto.configuration.ConfigurationModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code BindNetworkConnectionSectionToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "BindNetworkConnectionSectionToXmlPayloadTest")
public class BindNetworkConnectionSectionToXmlPayloadTest {
   Injector injector = Guice.createInjector(Rocoto.expandVariables(new ConfigurationModule() {

      @Override
      protected void bindConfigurations() {
         bindProperties(new VCloudApiMetadata().getDefaultProperties());
      }
   }));

   public void testWithIpAllocationModeNONE() throws IOException {

      HttpRequest request = HttpRequest.builder().endpoint("http://localhost/key").method("GET")
            .build();

      BindNetworkConnectionSectionToXmlPayload binder = injector
            .getInstance(BindNetworkConnectionSectionToXmlPayload.class);

      binder.bindToRequest(
            request,
            NetworkConnectionSection
                  .builder()
                  .type("application/vnd.vmware.vcloud.networkConnectionSection+xml")
                  .info("Specifies the available VM network connections")
                  .href(URI.create("https://1.1.1.1/api/v1.0/vApp/vm-1/networkConnectionSection/"))
                  .connections(
                        ImmutableSet.<NetworkConnection> of(NetworkConnection.builder().network("none")
                              .ipAddressAllocationMode(IpAddressAllocationMode.NONE).build())).build());
      assertEquals(request.getPayload().getContentMetadata().getContentType(),
            "application/vnd.vmware.vcloud.networkConnectionSection+xml");

      assertEquals(
            request.getPayload().getRawContent(),
            "<NetworkConnectionSection xmlns=\"http://www.vmware.com/vcloud/v1\" xmlns:ovf=\"http://schemas.dmtf.org/ovf/envelope/1\" href=\"https://1.1.1.1/api/v1.0/vApp/vm-1/networkConnectionSection/\" ovf:required=\"false\" type=\"application/vnd.vmware.vcloud.networkConnectionSection+xml\"><ovf:Info>Specifies the available VM network connections</ovf:Info><NetworkConnection network=\"none\"><NetworkConnectionIndex>0</NetworkConnectionIndex><IsConnected>false</IsConnected><IpAddressAllocationMode>NONE</IpAddressAllocationMode></NetworkConnection></NetworkConnectionSection>");

   }

}
