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
package org.jclouds.tmrk.enterprisecloud.xml;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import org.jclouds.crypto.Crypto;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.BaseRestClientTest;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.tmrk.enterprisecloud.domain.NamedResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.ResourceCapacity;
import org.jclouds.tmrk.enterprisecloud.domain.layout.DeviceLayout;
import org.jclouds.tmrk.enterprisecloud.domain.layout.LayoutGroup;
import org.jclouds.tmrk.enterprisecloud.domain.layout.LayoutRow;
import org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachine;
import org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachineReference;
import org.jclouds.tmrk.enterprisecloud.features.LayoutAsyncClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.inject.Named;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Set;

import static org.jclouds.io.Payloads.newInputStreamPayload;
import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.jclouds.rest.RestContextFactory.createContextBuilder;
import static org.testng.Assert.assertEquals;

/**
 * Tests behavior of JAXB parsing for DeviceLayout
 * 
 * @author Jason King
 */
@Test(groups = "unit", testName = "DeviceLayoutJAXBParsingTest")
public class DeviceLayoutJAXBParsingTest extends BaseRestClientTest {

   @BeforeClass
   void setupFactory() {
   RestContextSpec<String, Integer> contextSpec = contextSpec("test", "http://localhost:9999", "1", "", "userfoo",
        "credentialFoo", String.class, Integer.class,
        ImmutableSet.<Module> of(new MockModule(), new NullLoggingModule(), new AbstractModule() {

            @Override
            protected void configure() {}

            @SuppressWarnings("unused")
            @Provides
            @Named("exception")
            Set<String> exception() {
                throw new AuthorizationException();
            }

        }));

      injector = createContextBuilder(contextSpec).buildInjector();
      parserFactory = injector.getInstance(ParseSax.Factory.class);
      crypto = injector.getInstance(Crypto.class);
   }

   @Test
   public void testParseDeviceLayoutWithJAXB() throws Exception {
      Method method = LayoutAsyncClient.class.getMethod("getLayouts",URI.class);
      HttpRequest request = factory(LayoutAsyncClient.class).createRequest(method, new URI("/1"));
      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);

      Function<HttpResponse, DeviceLayout> parser = (Function<HttpResponse, DeviceLayout>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      InputStream is = getClass().getResourceAsStream("/deviceLayout.xml");
      DeviceLayout location = parser.apply(new HttpResponse(200, "ok", newInputStreamPayload(is)));
      assertRows(location.getRows());
   }
   
   private void assertRows(Set<LayoutRow> rows) {
      assertEquals(rows.size(),1);
      LayoutRow row = Iterables.getOnlyElement(rows);
      assertEquals(row.getIndex(),1);
      assertGroups(row.getGroups());
   }
   
   private void assertGroups(Set<LayoutGroup> groups) {
      assertEquals(groups.size(),1);
      LayoutGroup group = Iterables.getOnlyElement(groups);
      assertEquals(group.getIndex(), 33);
      assertVirtualMachineReferences(group.getVirtualMachineReferences());
   }
   
   private void assertVirtualMachineReferences(Set<VirtualMachineReference> virtualMachineReferences) {
      assertEquals(virtualMachineReferences.size(), 1);
      VirtualMachineReference vmReference = Iterables.getOnlyElement(virtualMachineReferences);

      assertEquals(vmReference.getName(),"helloworld");
      assertEquals(vmReference.getStatus(), VirtualMachine.VirtualMachineStatus.DEPLOYED);
      assertEquals(vmReference.getProcessorCount(),1);
      assertEquals(vmReference.getMemory(), ResourceCapacity.builder().value(384).unit("MB").build());
      assertEquals(vmReference.getStorage(), ResourceCapacity.builder().value(10).unit("GB").build());


      NamedResource expectedOs = NamedResource.builder().href(URI.create("/cloudapi/ecloud/operatingsystems/rhel5_64guest/computepools/89"))
                                                .name("Red Hat Enterprise Linux 5 (64-bit)")
                                                .type("application/vnd.tmrk.cloud.operatingSystem").build();
      assertEquals(vmReference.getOperatingSystem(),expectedOs);
   }
}
