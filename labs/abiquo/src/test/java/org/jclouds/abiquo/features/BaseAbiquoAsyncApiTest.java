/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.features;

import static org.jclouds.Constants.PROPERTY_PRETTY_PRINT_PAYLOADS;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.jclouds.abiquo.AbiquoApiMetadata;
import org.jclouds.abiquo.config.AbiquoRestClientModule;
import org.jclouds.abiquo.http.filters.AbiquoAuthentication;
import org.jclouds.abiquo.http.filters.AppendApiVersionToMediaType;
import org.jclouds.http.HttpRequest;
import org.jclouds.providers.AnonymousProviderMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.jclouds.xml.XMLParser;
import org.testng.annotations.BeforeClass;

import com.abiquo.model.transport.SingleResourceTransportDto;
import com.google.inject.Module;

/**
 * Tests annotation parsing of {@code AbiquoAsyncApi}.
 * 
 * @author Ignasi Barrera
 */
public abstract class BaseAbiquoAsyncApiTest<T> extends BaseAsyncClientTest<T> {
   private XMLParser xml;

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      xml = injector.getInstance(XMLParser.class);
   }

   @Override
   protected void checkFilters(final HttpRequest request) {
      assertEquals(request.getFilters().size(), 2);
      assertEquals(request.getFilters().get(0).getClass(), AbiquoAuthentication.class);
      assertEquals(request.getFilters().get(1).getClass(), AppendApiVersionToMediaType.class);
   }

   @Override
   protected Module createModule() {
      return new AbiquoRestClientModule();
   }

   @Override
   protected ProviderMetadata createProviderMetadata() {
      return AnonymousProviderMetadata.forApiWithEndpoint(new AbiquoApiMetadata(), "http://localhost/api");
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      // Do not pretty print payloads in tests
      props.setProperty(PROPERTY_PRETTY_PRINT_PAYLOADS, "false");
      return props;
   }

   protected void assertPayloadEquals(final HttpRequest request, final String toMatch,
         final Class<? extends SingleResourceTransportDto> entityClass, final String contentType,
         final boolean contentMD5) throws IOException {
      // Make sure we don't have formatting issues
      SingleResourceTransportDto entity = xml.fromXML(toMatch, entityClass);
      String stringToMatch = xml.toXML(entity, entityClass);

      super.assertPayloadEquals(request, stringToMatch, contentType, contentMD5);
   }

}
