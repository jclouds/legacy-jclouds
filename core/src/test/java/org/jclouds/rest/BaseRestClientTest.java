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
package org.jclouds.rest;

import static com.google.common.base.Throwables.propagate;
import static org.easymock.classextension.EasyMock.createMock;
import static org.jclouds.http.HttpUtils.sortAndConcatHeadersIntoString;
import static org.eclipse.jetty.http.HttpHeaders.TRANSFER_ENCODING;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;

import org.jclouds.javax.annotation.Nullable;

import org.jclouds.Constants;
import org.jclouds.concurrent.MoreExecutors;
import org.jclouds.concurrent.config.ConfiguresExecutorService;
import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.TransformingHttpCommandExecutorService;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.name.Names;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public abstract class BaseRestClientTest {

   protected Injector injector;
   protected ParseSax.Factory parserFactory;
   protected Crypto crypto;

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
         bind(ExecutorService.class).annotatedWith(Names.named(Constants.PROPERTY_USER_THREADS)).toInstance(
               MoreExecutors.sameThreadExecutor());
         bind(ExecutorService.class).annotatedWith(Names.named(Constants.PROPERTY_IO_WORKER_THREADS)).toInstance(
               MoreExecutors.sameThreadExecutor());
         bind(TransformingHttpCommandExecutorService.class).toInstance(mock);
      }
   }

   protected void assertPayloadEquals(HttpRequest request, String toMatch, String contentType, boolean contentMD5) {
      assertPayloadEquals(request, toMatch, contentType, null, null, null, contentMD5);
   }

   protected void assertPayloadEquals(HttpRequest request, String toMatch, String contentType,
         String contentDispositon, String contentEncoding, String contentLanguage, boolean contentMD5) {
      if (request.getPayload() == null) {
         assertNull(toMatch);
      } else {
         String payload = null;
         try {
            payload = Strings2.toStringAndClose(request.getPayload().getInput());
         } catch (IOException e) {
            propagate(e);
         }
         assertEquals(payload, toMatch);
         Long length = new Long(payload.getBytes().length);
         try {
            assertContentHeadersEqual(request, contentType, contentDispositon, contentEncoding, contentLanguage,
                  length, contentMD5 ? CryptoStreams.md5(request.getPayload()) : null);
         } catch (IOException e) {
            propagate(e);
         }
      }
   }

   protected void assertContentHeadersEqual(HttpRequest request, String contentType, String contentDispositon,
         String contentEncoding, String contentLanguage, Long length, byte[] contentMD5) {
      MutableContentMetadata md = request.getPayload().getContentMetadata();
      if (request.getFirstHeaderOrNull(TRANSFER_ENCODING) == null) {
         assertEquals(md.getContentLength(), length);
      } else {
         assertEquals(request.getFirstHeaderOrNull(TRANSFER_ENCODING), "chunked");
         assert md.getContentLength() == null || md.getContentLength().equals(length);
      }
      assertEquals(md.getContentType(), contentType);
      assertEquals(md.getContentDisposition(), contentDispositon);
      assertEquals(md.getContentEncoding(), contentEncoding);
      assertEquals(md.getContentLanguage(), contentLanguage);
      assertEquals(md.getContentMD5(), contentMD5);
   }

   // FIXME Shouldn't be assertPayloadHeadersEqual?
   protected void assertNonPayloadHeadersEqual(HttpRequest request, String toMatch) {
      assertEquals(sortAndConcatHeadersIntoString(request.getHeaders()), toMatch);
   }

   protected void assertRequestLineEquals(HttpRequest request, String toMatch) {
      assertEquals(request.getRequestLine(), toMatch);
   }

   protected void assertExceptionParserClassEquals(Method method, @Nullable Class<?> parserClass) {
      if (parserClass == null)
         assertEquals(
               RestAnnotationProcessor
                     .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(injector, method).getClass(),
               MapHttp4xxCodesToExceptions.class);
      else
         assertEquals(
               RestAnnotationProcessor
                     .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(injector, method).getClass(),
               parserClass);
   }

   protected void assertSaxResponseParserClassEquals(Method method, @Nullable Class<?> parserClass) {
      assertEquals(RestAnnotationProcessor.getSaxResponseParserClassOrNull(method), parserClass);
   }

   protected void assertResponseParserClassEquals(Method method, HttpRequest request, @Nullable Class<?> parserClass) {
      assertEquals(RestAnnotationProcessor.createResponseParser(parserFactory, injector, method, request).getClass(),
            parserClass);
   }

}