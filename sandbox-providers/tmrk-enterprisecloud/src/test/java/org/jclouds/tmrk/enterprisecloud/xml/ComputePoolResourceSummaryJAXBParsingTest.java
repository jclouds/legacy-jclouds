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
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.BaseRestClientTest;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.tmrk.enterprisecloud.domain.internal.ResourceCapacity;
import org.jclouds.tmrk.enterprisecloud.domain.resource.*;
import org.jclouds.tmrk.enterprisecloud.domain.resource.cpu.CpuComputeResourceSummary;
import org.jclouds.tmrk.enterprisecloud.features.ResourceAsyncClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
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
 * Tests behavior of JAXB parsing for ComputePoolResourceSummary
 * 
 * @author Jason King
 */
@Test(groups = "unit", testName = "ComputePoolResourceSummaryJAXBParsingTest")
public class ComputePoolResourceSummaryJAXBParsingTest extends BaseRestClientTest {

  private SimpleDateFormatDateService dateService;
  @BeforeMethod
  public void setUp() {
      dateService = new SimpleDateFormatDateService();
  }

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
   public void testParseWithJAXB() throws Exception {
      Method method = ResourceAsyncClient.class.getMethod("getResourceSummary",URI.class);
      HttpRequest request = factory(ResourceAsyncClient.class).createRequest(method, new URI("/1"));
      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);

      Function<HttpResponse, ComputePoolResourceSummary> parser = (Function<HttpResponse, ComputePoolResourceSummary>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      InputStream is = getClass().getResourceAsStream("/computePoolResourceSummary.xml");
      ComputePoolResourceSummary summary = parser.apply(new HttpResponse(200, "ok", newInputStreamPayload(is)));
      assertEquals(summary.getStartTime(),dateService.iso8601DateParse("2011-12-04T18:55:00.0Z"));
      assertEquals(summary.getEndTime(),dateService.iso8601DateParse("2011-12-05T18:55:00.0Z"));

      assertCpu(summary.getCpu());
      assertMemory(summary.getMemory());
      assertStorage(summary.getStorage());
      assertVirtualMachines(summary.getVirtualMachines());
   }

   private void assertCpu(CpuComputeResourceSummary cpu) {
      CpuComputeResourceSummary expected = CpuComputeResourceSummary.builder()
            .allocated(ResourceCapacity.builder().value(2.7).unit("GHz").build())
            .consumed(ResourceCapacity.builder().value(59).unit("MHz").build())
            .purchased(ResourceCapacity.builder().value(5).unit("GHz").build())
            .utilization(2)
            .build();
      assertEquals(cpu,expected);
   }

   private void assertMemory(MemoryComputeResourceSummary memory) {
      MemoryComputeResourceSummary expected = MemoryComputeResourceSummary.builder()
            .allocated(ResourceCapacity.builder().value(0.4).unit("GB").build())
            .consumed(ResourceCapacity.builder().value(404).unit("MB").build())
            .purchased(ResourceCapacity.builder().value(10).unit("GB").build())
            .utilization(4)
            .build();
      assertEquals(memory,expected);
   }

   private void assertStorage(StorageResourceSummary storage) {
      StorageResourceSummary expected = StorageResourceSummary.builder()
            .purchased(ResourceCapacity.builder().value(250).unit("GB").build())
            .used(ResourceCapacity.builder().value(10).unit("GB").build())
            .build();
      assertEquals(storage,expected);
   }

   private void assertVirtualMachines(VirtualMachineResourceSummary virtualMachines) {
      VirtualMachineResourceSummary expected = VirtualMachineResourceSummary.builder()
            .count(1)
            .poweredOnCount(1)
            .build();
      assertEquals(virtualMachines, expected);
   }
}
