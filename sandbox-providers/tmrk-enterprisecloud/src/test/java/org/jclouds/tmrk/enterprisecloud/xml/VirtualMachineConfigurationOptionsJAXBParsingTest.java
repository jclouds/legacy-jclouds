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
   import org.jclouds.tmrk.enterprisecloud.domain.*;
import org.jclouds.tmrk.enterprisecloud.domain.hardware.DiskConfigurationOption;
import org.jclouds.tmrk.enterprisecloud.domain.hardware.DiskConfigurationOptionRange;
import org.jclouds.tmrk.enterprisecloud.domain.internal.ResourceCapacity;
import org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachineConfigurationOptions;
import org.jclouds.tmrk.enterprisecloud.features.VirtualMachineAsyncClient;
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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests behavior of JAXB parsing for VirtualMachineConfigurationOptions
 * 
 * @author Jason King
 */
@Test(groups = "unit", testName = "VirtualMachineConfigurationOptionsJAXBParsingTest")
public class VirtualMachineConfigurationOptionsJAXBParsingTest extends BaseRestClientTest {

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
   public void testParseVirtualMachineWithJAXB() throws Exception {

      Method method = VirtualMachineAsyncClient.class.getMethod("getConfigurationOptions", URI.class);
      HttpRequest request = factory(VirtualMachineAsyncClient.class).createRequest(method,new URI("/1"));
      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);

      Function<HttpResponse, VirtualMachineConfigurationOptions> parser = (Function<HttpResponse, VirtualMachineConfigurationOptions>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      InputStream is = getClass().getResourceAsStream("/virtualMachineConfigurationOptions.xml");
      VirtualMachineConfigurationOptions configurationOptions = parser.apply(new HttpResponse(200, "ok", newInputStreamPayload(is)));

      assertProcessorOptions(configurationOptions.getProcessor());
      assertMemoryOptions(configurationOptions.getMemory());
      assertDiskConfigurationOption(configurationOptions.getDisk());
      assertNetworkAdapterOptions(configurationOptions.getNetworkAdapter());
      assertCustomizationOption(configurationOptions.getCustomization());
   }

   private void assertProcessorOptions(ConfigurationOptionRange processor) {
      assertEquals(processor.getMinimum(),1);
      assertEquals(processor.getMaximum(),8);
      assertEquals(processor.getStepFactor(),1);
   }

   private void assertMemoryOptions(ResourceCapacityRange memory) {
      assertEquals(memory.getMinimumSize(), ResourceCapacity.builder().value(256).unit("MB").build());
      assertEquals(memory.getMaximumSize(), ResourceCapacity.builder().value(261120).unit("MB").build());
      assertEquals(memory.getStepFactor(), ResourceCapacity.builder().value(4).unit("MB").build());
   }

   private void assertNetworkAdapterOptions(ConfigurationOptionRange networkAdapter) {
      assertEquals(networkAdapter.getMinimum(),1);
      assertEquals(networkAdapter.getMaximum(),4);
      assertEquals(networkAdapter.getStepFactor(),1);
   }

   private void assertCustomizationOption(CustomizationOption customization) {
      assertEquals(customization.getType(), CustomizationOption.CustomizationType.LINUX);
      assertFalse(customization.canPowerOn());
      assertFalse(customization.isPasswordRequired());
      assertTrue(customization.isSshKeyRequired());
   }

   private void assertDiskConfigurationOption(DiskConfigurationOption diskConfigurationOption) {
      assertEquals(diskConfigurationOption.getMinimum(),1);
      assertEquals(diskConfigurationOption.getMaximum(), 15);

      ResourceCapacityRange systemDiskRange = ResourceCapacityRange.builder()
            .minimumSize(ResourceCapacity.builder().value(1).unit("GB").build())
            .maximumSize(ResourceCapacity.builder().value(512).unit("GB").build())
            .stepFactor(ResourceCapacity.builder().value(1).unit("GB").build())
            .build();
      assertEquals(diskConfigurationOption.getSystemDisk(), DiskConfigurationOptionRange.builder().resourceCapacityRange(systemDiskRange).monthlyCost(0).build());

      ResourceCapacityRange dataDiskRange = ResourceCapacityRange.builder()
            .minimumSize(ResourceCapacity.builder().value(1).unit("GB").build())
            .maximumSize(ResourceCapacity.builder().value(512).unit("GB").build())
            .stepFactor(ResourceCapacity.builder().value(2).unit("GB").build())
            .build();

      assertEquals(diskConfigurationOption.getDataDisk(), DiskConfigurationOptionRange.builder().resourceCapacityRange(dataDiskRange).monthlyCost(0).build());
   }

}
