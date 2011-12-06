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
import org.jclouds.tmrk.enterprisecloud.domain.resource.memory.ComputePoolMemoryUsageDetail;
import org.jclouds.tmrk.enterprisecloud.domain.resource.memory.VirtualMachineMemoryUsageDetail;
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
import static org.testng.Assert.assertNull;

/**
 * Tests behavior of JAXB parsing for ComputePoolMemoryUsageDetail
 * 
 * @author Jason King
 */
@Test(groups = "unit", testName = "ComputePoolResourceSummaryListJAXBParsingTest")
public class ComputePoolMemoryUsageDetailJAXBParsingTest extends BaseRestClientTest {

   private SimpleDateFormatDateService dateService;
   @BeforeMethod
   public void setUp() {
      dateService = new SimpleDateFormatDateService();
   }

   @BeforeClass
   void setupFactory() {
   RestContextSpec<String, Integer> contextSpec = contextSpec("test", "http://localhost:9999", "1", "", "userfoo",
         "credentialFoo", String.class, Integer.class,
         ImmutableSet.<Module>of(new MockModule(), new NullLoggingModule(), new AbstractModule() {

            @Override
            protected void configure() {
            }

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

   public void testParseWithJAXB() throws Exception {

      Method method = ResourceAsyncClient.class.getMethod("getComputePoolMemoryUsageDetail", URI.class);
      HttpRequest request = factory(ResourceAsyncClient.class).createRequest(method,new URI("/1"));
      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);

      Function<HttpResponse, ComputePoolMemoryUsageDetail> parser = (Function<HttpResponse, ComputePoolMemoryUsageDetail>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      InputStream is = getClass().getResourceAsStream("/computePoolMemoryUsageDetail.xml");
      ComputePoolMemoryUsageDetail detail = parser.apply(new HttpResponse(200, "ok", newInputStreamPayload(is)));

      assertEquals(detail.getTime(), dateService.iso8601DateParse("2011-12-05T15:55:00.0Z"));
      assertEquals(detail.getValue(), ResourceCapacity.builder().value(404).unit("MB").build());
      Set<VirtualMachineMemoryUsageDetail> virtualMachinesMemoryUsageDetail = detail.getVirtualMachinesMemoryUsage().getVirtualMachinesMemoryUsageDetail();
      assertDetail(virtualMachinesMemoryUsageDetail);
   }

   public void testParseNoDetailWithJAXB() throws Exception {

      Method method = ResourceAsyncClient.class.getMethod("getComputePoolMemoryUsageDetail", URI.class);
      HttpRequest request = factory(ResourceAsyncClient.class).createRequest(method,new URI("/1"));
      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);

      Function<HttpResponse, ComputePoolMemoryUsageDetail> parser = (Function<HttpResponse, ComputePoolMemoryUsageDetail>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      InputStream is = getClass().getResourceAsStream("/computePoolMemoryUsageDetailNoDetail.xml");
      ComputePoolMemoryUsageDetail detail = parser.apply(new HttpResponse(200, "ok", newInputStreamPayload(is)));

      assertEquals(detail.getTime(), dateService.iso8601DateParse("1974-12-05T15:55:00.0Z"));
      assertEquals(detail.getValue(), ResourceCapacity.builder().value(0).unit("MB").build());
      assertNull(detail.getVirtualMachinesMemoryUsage());
   }

   private void assertDetail(Set<VirtualMachineMemoryUsageDetail> details) {
      assertEquals(details.size(),1);
      VirtualMachineMemoryUsageDetail detail = Iterables.getOnlyElement(details);
      VirtualMachineMemoryUsageDetail expected = VirtualMachineMemoryUsageDetail.builder()
            .href(URI.create("/cloudapi/ecloud/virtualmachines/5504"))
            .name("helloworld")
            .type("application/vnd.tmrk.cloud.virtualMachine")
            .usage(ResourceCapacity.builder().value(404).unit("MB").build())
            .utilization(100)
            .build();
      assertEquals(detail,expected);
   }


}
