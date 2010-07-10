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
package org.jclouds.rest;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.easymock.classextension.EasyMock.createMock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.concurrent.config.ConfiguresExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.TransformingHttpCommandExecutorService;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Utils;
import org.mortbay.jetty.HttpHeaders;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.internal.Nullable;

public abstract class BaseRestClientTest {

   protected Injector injector;
   protected ParseSax.Factory parserFactory;
   protected EncryptionService encryptionService;

   @ConfiguresHttpCommandExecutorService
   @ConfiguresExecutorService
   public static class MockModule extends AbstractModule {
      private final TransformingHttpCommandExecutorService mock;

      public MockModule() {
         this(createMock(TransformingHttpCommandExecutorService.class));
      }

      public MockModule(TransformingHttpCommandExecutorService mock) {
         this.mock = mock;
      }

      @Override
      protected void configure() {
         install(new ExecutorServiceModule(sameThreadExecutor(), sameThreadExecutor()));
         bind(TransformingHttpCommandExecutorService.class).toInstance(mock);
      }
   }

   protected void assertPayloadEquals(HttpRequest request, String toMatch, String contentType,
            boolean contentMD5) throws IOException {
      if (request.getPayload() == null) {
         assertNull(toMatch);
      } else {
         String payload = Utils.toStringAndClose(request.getPayload().getInput());
         assertEquals(payload, toMatch);
         if (request.getFirstHeaderOrNull(HttpHeaders.TRANSFER_ENCODING) == null) {
            assertEquals(request.getPayload().getContentLength(), new Long(
                     payload.getBytes().length));
         } else {
            assertEquals(request.getFirstHeaderOrNull(HttpHeaders.TRANSFER_ENCODING), "chunked");
            assert request.getPayload().getContentLength() == null
                     || request.getPayload().getContentLength().equals(
                              new Long(payload.getBytes().length));
         }
         assertEquals(request.getPayload().getContentType(), contentType);
         assertEquals(request.getPayload().getContentMD5(), contentMD5 ? encryptionService
                  .md5(request.getPayload().getInput()) : null);
      }
   }

   protected void assertNonPayloadHeadersEqual(HttpRequest request, String toMatch) {
      assertEquals(HttpUtils.sortAndConcatHeadersIntoString(request.getHeaders()), toMatch);
   }

   protected void assertRequestLineEquals(HttpRequest request, String toMatch) {
      assertEquals(request.getRequestLine(), toMatch);
   }

   protected void assertExceptionParserClassEquals(Method method, @Nullable Class<?> parserClass) {
      if (parserClass == null)
         assertEquals(
                  RestAnnotationProcessor
                           .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(
                                    injector, method).getClass(), MapHttp4xxCodesToExceptions.class);
      else
         assertEquals(
                  RestAnnotationProcessor
                           .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(
                                    injector, method).getClass(), parserClass);
   }

   protected void assertSaxResponseParserClassEquals(Method method, @Nullable Class<?> parserClass) {
      assertEquals(RestAnnotationProcessor.getSaxResponseParserClassOrNull(method), parserClass);
   }

   protected void assertResponseParserClassEquals(Method method, HttpRequest request,
            @Nullable Class<?> parserClass) {
      assertEquals(RestAnnotationProcessor.createResponseParser(parserFactory, injector, method,
               request).getClass(), parserClass);
   }

}