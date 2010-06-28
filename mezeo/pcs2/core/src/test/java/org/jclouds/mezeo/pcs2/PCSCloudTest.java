/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.mezeo.pcs2;

import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.mezeo.pcs2.PCSCloudAsyncClient.Response;
import org.jclouds.mezeo.pcs2.xml.CloudXlinkHandler;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code PCSClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.PCSCloudTest")
public class PCSCloudTest extends RestClientTest<PCSCloudAsyncClient> {

   public void testAuthenticate() throws SecurityException, NoSuchMethodException {
      Method method = PCSCloudAsyncClient.class.getMethod("authenticate");
      GeneratedHttpRequest<PCSCloudAsyncClient> httpMethod = processor.createRequest(method);
      assertEquals(httpMethod.getRequestLine(), "GET http://localhost:8080/ HTTP/1.1");
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(), ParseSax.class);
      assertEquals(RestAnnotationProcessor.getSaxResponseParserClassOrNull(method),
               CloudXlinkHandler.class);
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), BasicAuthentication.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<PCSCloudAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<PCSCloudAsyncClient>>() {
      };
   }

   @Override
   public ContextSpec<PCSCloudClient, PCSCloudAsyncClient> createContextSpec() {
      return contextSpec("test", "http://localhost:8080", "1", "identity", "credential",
               PCSCloudClient.class, PCSCloudAsyncClient.class);
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<PCSCloudAsyncClient> httpMethod) {

   }

   @Timeout(duration = 10, timeUnit = TimeUnit.SECONDS)
   public interface PCSCloudClient {

      Response authenticate();
   }

}
