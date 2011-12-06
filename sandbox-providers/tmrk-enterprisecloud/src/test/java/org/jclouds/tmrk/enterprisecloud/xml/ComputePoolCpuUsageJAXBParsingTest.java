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
import org.jclouds.tmrk.enterprisecloud.domain.Link;
import org.jclouds.tmrk.enterprisecloud.domain.resource.cpu.ComputePoolCpuUsage;
import org.jclouds.tmrk.enterprisecloud.domain.resource.cpu.ComputePoolCpuUsageDetailSummaryEntry;
import org.jclouds.tmrk.enterprisecloud.domain.resource.cpu.CpuUsageDetails;
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
import static org.testng.Assert.assertNotNull;

/**
 * Tests behavior of JAXB parsing for ComputePoolCpuUsage
 * 
 * @author Jason King
 */
@Test(groups = "unit", testName = "ComputePoolCpuUsageJAXBParsingTest")
public class ComputePoolCpuUsageJAXBParsingTest extends BaseRestClientTest {

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

      Method method = ResourceAsyncClient.class.getMethod("getComputePoolCpuUsage", URI.class);
      HttpRequest request = factory(ResourceAsyncClient.class).createRequest(method,new URI("/1"));
      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);

      Function<HttpResponse, ComputePoolCpuUsage> parser = (Function<HttpResponse, ComputePoolCpuUsage>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      InputStream is = getClass().getResourceAsStream("/computePoolsCpuUsage.xml");
      ComputePoolCpuUsage cpuUsage = parser.apply(new HttpResponse(200, "ok", newInputStreamPayload(is)));

      assertLinks(cpuUsage.getLinks());
      assertEquals(cpuUsage.getStartTime(), dateService.iso8601DateParse("2011-12-05T09:45:00.0Z"));
      assertEquals(cpuUsage.getEndTime(), dateService.iso8601DateParse("2011-12-06T09:45:00.0Z"));
      assertDetails(cpuUsage.getDetails());

      Set<ComputePoolCpuUsageDetailSummaryEntry> entries = cpuUsage.getDetails().getEntries();
      ComputePoolCpuUsageDetailSummaryEntry first = Iterables.getFirst(entries, null);
      assertEquals(cpuUsage.getStartTime(),first.getTime());

      ComputePoolCpuUsageDetailSummaryEntry last = Iterables.getLast(entries, null);
      assertEquals(cpuUsage.getEndTime(),last.getTime());
   }

   private void assertLinks(Set<Link> links) {
      assertEquals(links.size(),2);
      Link link = Iterables.get(links, 0);
      assertEquals(link.getName(),"Default Compute Pool");
      assertEquals(link.getRelationship(), Link.Relationship.UP);

      Link link2 = Iterables.get(links, 1);
      assertEquals(link2.getHref(), URI.create("/cloudapi/ecloud/computepools/89/usage/cpu/details?time=2011-12-05t09%3a45%3a00z"));
      assertEquals(link2.getType(), "application/vnd.tmrk.cloud.computePoolCpuUsageDetail");
      assertEquals(link2.getRelationship(), Link.Relationship.DOWN);
   }

   private void assertDetails(CpuUsageDetails details) {
      assertEquals(details.getEntries().size(), 289);
      for(ComputePoolCpuUsageDetailSummaryEntry entry: details.getEntries()) {
         assertDetail(entry);
      }
   }

   private void assertDetail(ComputePoolCpuUsageDetailSummaryEntry entry) {
      assertNotNull(entry.getTime());
      assertNotNull(entry.getValue());
   }
}
