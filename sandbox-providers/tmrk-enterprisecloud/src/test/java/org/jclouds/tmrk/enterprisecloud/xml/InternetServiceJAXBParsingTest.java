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
import org.jclouds.tmrk.enterprisecloud.domain.network.IpAddressReference;
import org.jclouds.tmrk.enterprisecloud.domain.service.Protocol;
import org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetService;
import org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetServicePersistenceType;
import org.jclouds.tmrk.enterprisecloud.domain.service.node.NodeService;
import org.jclouds.tmrk.enterprisecloud.domain.service.node.NodeServices;
import org.jclouds.tmrk.enterprisecloud.features.InternetServiceAsyncClient;
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
import static org.testng.Assert.assertTrue;

/**
 * Tests behavior of JAXB parsing for Location
 * 
 * @author Jason King
 */
@Test(groups = "unit", testName = "InternetServiceJAXBParsingTest")
public class InternetServiceJAXBParsingTest extends BaseRestClientTest {

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
   public void testParseInternetServiceWithJAXB() throws Exception {
      Method method = InternetServiceAsyncClient.class.getMethod("getInternetService",URI.class);
      HttpRequest request = factory(InternetServiceAsyncClient.class).createRequest(method, new URI("/1"));
      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);

      Function<HttpResponse, InternetService> parser = (Function<HttpResponse, InternetService>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      InputStream is = getClass().getResourceAsStream("/internetService.xml");
      InternetService internetService = parser.apply(new HttpResponse(200, "ok", newInputStreamPayload(is)));
      assertEquals(internetService.getProtocol(), Protocol.TCP);
      assertEquals(internetService.getPort(),22);
      assertTrue(internetService.isEnabled());
      assertEquals(internetService.getPublicIp(), NamedResource.builder().href(URI.create("/cloudapi/ecloud/publicips/3929")).type("application/vnd.tmrk.cloud.publicIp").name("208.39.65.40").build());
      assertEquals(internetService.getPersistence().getPersistenceType(), InternetServicePersistenceType.PersistenceType.NONE);
      assertEquals(internetService.getPersistence().getTimeout(), -1); //Default value
      assertEquals(internetService.getMonitor(),NamedResource.builder().href(URI.create("/cloudapi/ecloud/internetservices/797/monitor")).type("application/vnd.tmrk.cloud.defaultMonitor").build());
      assertNodeServices(internetService.getNodeServices());

      System.out.println(internetService);
   }

   private void assertNodeServices(NodeServices nodeServices) {
      assertEquals(nodeServices.getNodeServices().size(),1);
      NodeService nodeService = Iterables.getOnlyElement(nodeServices.getNodeServices());
      assertEquals(nodeService.getName(),"ssh");
      
      IpAddressReference ipAddress = nodeService.getIpAddress();
      assertEquals(ipAddress.getName(),"10.146.205.131");
      assertEquals(ipAddress.getNetwork(),NamedResource.builder().href(URI.create("/cloudapi/ecloud/networks/3933")).type("application/vnd.tmrk.cloud.network").name("10.146.205.128/27").build());
      assertEquals(ipAddress.getHost(),NamedResource.builder().href(URI.create("/cloudapi/ecloud/networkhosts/7144")).type("application/vnd.tmrk.cloud.networkHost").name("vmDMZ1").build());
      assertEquals(nodeService.getProtocol(), Protocol.TCP);
      assertEquals(nodeService.getPort(),22);
      assertTrue(nodeService.isEnabled());
   }
}

