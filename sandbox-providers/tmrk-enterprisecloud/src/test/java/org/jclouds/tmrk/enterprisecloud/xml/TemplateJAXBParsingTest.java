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
import org.jclouds.tmrk.enterprisecloud.domain.ConfigurationOptionRange;
import org.jclouds.tmrk.enterprisecloud.domain.CustomizationOption;
import org.jclouds.tmrk.enterprisecloud.domain.Link;
import org.jclouds.tmrk.enterprisecloud.domain.ResourceCapacityRange;
import org.jclouds.tmrk.enterprisecloud.domain.internal.ResourceCapacity;
import org.jclouds.tmrk.enterprisecloud.domain.software.OperatingSystem;
import org.jclouds.tmrk.enterprisecloud.domain.template.Template;
import org.jclouds.tmrk.enterprisecloud.domain.template.TemplateStorage;
import org.jclouds.tmrk.enterprisecloud.features.TemplateAsyncClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.inject.Named;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import static org.jclouds.io.Payloads.newInputStreamPayload;
import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.jclouds.rest.RestContextFactory.createContextBuilder;
import static org.testng.Assert.assertEquals;

/**
 * Tests behavior of JAXB parsing for Templates
 * 
 * @author Jason King
 */
@Test(groups = "unit", testName = "TemplateJAXBParsingTest")
public class TemplateJAXBParsingTest extends BaseRestClientTest {

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

   public void testParseTemplate() throws Exception {

      Method method = TemplateAsyncClient.class.getMethod("getTemplate", URI.class);
      HttpRequest request = factory(TemplateAsyncClient.class).createRequest(method,new URI("/1"));
      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);

      Function<HttpResponse, Template> parser = (Function<HttpResponse, Template>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      InputStream is = getClass().getResourceAsStream("/template.xml");
      Template template = parser.apply(new HttpResponse(200, "ok", newInputStreamPayload(is)));

      assertLinks(template.getLinks());
      assertOperatingSystem(template.getOperatingSystem());
      assertEquals(template.getDescription(),"");
      assertProcessor(template.getProcessor());
      assertMemory(template.getMemory());
      assertStorage(template.getStorage());
      assertEquals(template.getNetworkAdapters(),1);
      assertCustomization(template.getCustomization());
   }

   private void assertLinks(Set<Link> links) {
      assertEquals(links.size(),1);
      Link link = Iterables.getOnlyElement(links);
      assertEquals(link.getName(),"Default Compute Pool");
   }

   private void assertOperatingSystem(OperatingSystem os) throws URISyntaxException {
      OperatingSystem expected = OperatingSystem.builder()
            .href(new URI("/cloudapi/ecloud/operatingsystems/rhel5_64guest/computepools/89"))
            .name("Red Hat Enterprise Linux 5 (64-bit)")
            .type("application/vnd.tmrk.cloud.operatingSystem")
            .build();

      assertEquals(os,expected);
   }

   private void assertProcessor(ConfigurationOptionRange configuration) {
      ConfigurationOptionRange expected = ConfigurationOptionRange.builder()
            .minimum(1).maximum(8).stepFactor(1).build();
      assertEquals(configuration, expected);
   }

   private void assertMemory(ResourceCapacityRange memory) {
      ResourceCapacity min = ResourceCapacity.builder().value(256).unit("MB").build();
      ResourceCapacity max = ResourceCapacity.builder().value(261120).unit("MB").build();
      ResourceCapacity step = ResourceCapacity.builder().value(4).unit("MB").build();
      ResourceCapacityRange expected = ResourceCapacityRange.builder()
            .minimumSize(min).maximumSize(max).stepFactor(step).build();

      assertEquals(memory, expected);
   }

   private void assertStorage(TemplateStorage storage) {
      ResourceCapacity size = ResourceCapacity.builder().value(10).unit("GB").build();
      TemplateStorage expected = TemplateStorage.builder().size(size).build();
      assertEquals(storage, expected);
   }

   private void assertCustomization(CustomizationOption customization) {
      CustomizationOption expected = CustomizationOption.builder()
            .canPowerOn(false)
            .passwordRequired(false)
            .sshKeyRequired(true)
            .type(CustomizationOption.CustomizationType.LINUX)
            .build();

      assertEquals(customization, expected);
   }
}
