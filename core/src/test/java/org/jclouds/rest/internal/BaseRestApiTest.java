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
package org.jclouds.rest.internal;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.hash.Hashing.md5;
import static org.easymock.EasyMock.createMock;
import static org.eclipse.jetty.http.HttpHeaders.TRANSFER_ENCODING;
import static org.jclouds.io.ByteSources.asByteSource;
import static org.jclouds.rest.internal.RestAnnotationProcessor.createResponseParser;
import static org.jclouds.rest.internal.RestAnnotationProcessor.getSaxResponseParserClassOrNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import org.jclouds.Constants;
import org.jclouds.concurrent.MoreExecutors;
import org.jclouds.concurrent.config.ConfiguresExecutorService;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.Multimap;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.name.Names;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public abstract class BaseRestApiTest {

   protected Injector injector;
   protected ParseSax.Factory parserFactory;

   @ConfiguresHttpCommandExecutorService
   @ConfiguresExecutorService
   public static class MockModule extends AbstractModule {
      private final HttpCommandExecutorService mock;

      public MockModule() {
         this(createMock(HttpCommandExecutorService.class));
      }

      public MockModule(HttpCommandExecutorService mock) {
         this.mock = mock;
      }

      @Override
      protected void configure() {
         bind(ExecutorService.class).annotatedWith(Names.named(Constants.PROPERTY_USER_THREADS)).toInstance(
               MoreExecutors.sameThreadExecutor());
         bind(ExecutorService.class).annotatedWith(Names.named(Constants.PROPERTY_IO_WORKER_THREADS)).toInstance(
               MoreExecutors.sameThreadExecutor());
         bind(HttpCommandExecutorService.class).toInstance(mock);
      }
   }

   protected void assertPayloadEquals(HttpRequest request, String toMatch, String contentType, boolean contentMD5) {
      assertPayloadEquals(request, toMatch, contentType, contentMD5, null);
   }

   protected void assertPayloadEquals(HttpRequest request, String toMatch, String contentType, boolean contentMD5, Date expires) {
      assertPayloadEquals(request, toMatch, contentType, null, null, null, contentMD5, expires);
   }

   protected void assertPayloadEquals(HttpRequest request, String toMatch, String contentType,
         String contentDispositon, String contentEncoding, String contentLanguage, boolean contentMD5) {
      assertPayloadEquals(request, toMatch, contentType, contentDispositon, contentEncoding, contentLanguage, 
               contentMD5, null);
   }

   protected void assertPayloadEquals(HttpRequest request, String toMatch, String contentType,
         String contentDispositon, String contentEncoding, String contentLanguage, boolean contentMD5,
         Date expires) {
      if (request.getPayload() == null) {
         assertNull(toMatch);
      } else {
         String payload = null;
         try {
            payload = Strings2.toString(request.getPayload());
         } catch (IOException e) {
            propagate(e);
         }
         assertEquals(payload, toMatch);
         Long length = Long.valueOf(payload.getBytes().length);
         try {
            assertContentHeadersEqual(request, contentType, contentDispositon, contentEncoding, contentLanguage,
                  length, contentMD5 ? asByteSource(request.getPayload().getInput()).hash(md5()).asBytes() : null, expires);
         } catch (IOException e) {
            propagate(e);
         }
      }
   }

   protected void assertContentHeadersEqual(HttpRequest request, String contentType, String contentDispositon,
         String contentEncoding, String contentLanguage, Long length, byte[] contentMD5, Date expires) {
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
      assertEquals(md.getExpires(), expires);
   }

   // FIXME Shouldn't be assertPayloadHeadersEqual?
   protected void assertNonPayloadHeadersEqual(HttpRequest request, String toMatch) {
      assertEquals(sortAndConcatHeadersIntoString(request.getHeaders()), toMatch);
   }

   public static String sortAndConcatHeadersIntoString(Multimap<String, String> headers) {
      StringBuilder buffer = new StringBuilder();
      SortedSetMultimap<String, String> sortedMap = TreeMultimap.create();
      sortedMap.putAll(headers);
      for (Entry<String, String> header : sortedMap.entries()) {
         if (header.getKey() != null)
            buffer.append(String.format("%s: %s\n", header.getKey(), header.getValue()));
      }
      return buffer.toString();
   }
   
   protected void assertRequestLineEquals(HttpRequest request, String toMatch) {
      assertEquals(request.getRequestLine(), toMatch);
   }

   protected void assertFallbackClassEquals(Method method, @Nullable Class<?> expected) {
      Fallback fallbackAnnotation = method.getAnnotation(Fallback.class);
      Class<?> assigned = fallbackAnnotation != null ? fallbackAnnotation.value() : MapHttp4xxCodesToExceptions.class;
      if (expected == null)
         assertEquals(assigned, MapHttp4xxCodesToExceptions.class);
      else
         assertEquals(assigned, expected);
   }

   protected void assertSaxResponseParserClassEquals(Method method, @Nullable Class<?> parserClass) {
      assertEquals(getSaxResponseParserClassOrNull(method), parserClass);
   }

   protected void assertResponseParserClassEquals(Method method, HttpRequest request, @Nullable Class<?> parserClass) {
      assertEquals(createResponseParser(parserFactory, injector, method, request).getClass(), parserClass);
   }

   protected RestAnnotationProcessor factory(Class<?> clazz) {
      return injector.getInstance(RestAnnotationProcessor.Factory.class).declaring(clazz);
   }

}
