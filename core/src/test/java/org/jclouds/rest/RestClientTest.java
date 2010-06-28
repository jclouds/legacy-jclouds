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
import static org.jclouds.rest.RestContextFactory.createContextBuilder;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.concurrent.config.ConfiguresExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.TransformingHttpCommandExecutorService;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeClass;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Nullable;

public abstract class RestClientTest<T> {

   protected RestAnnotationProcessor<T> processor;

   protected Module createModule() {
      return new Module() {

         @Override
         public void configure(Binder binder) {

         }

      };
   }

   protected Injector injector;

   protected abstract void checkFilters(GeneratedHttpRequest<T> httpMethod);

   protected abstract TypeLiteral<RestAnnotationProcessor<T>> createTypeLiteral();

   @BeforeClass
   protected void setupFactory() throws IOException {
      ContextSpec<?, ?> contextSpec = createContextSpec();
      injector = createContextBuilder(contextSpec,
               ImmutableSet.of(new MockModule(), new NullLoggingModule(), createModule()))
               .buildInjector();
      processor = injector.getInstance(Key.get(createTypeLiteral()));
   }

   abstract public ContextSpec<?, ?> createContextSpec();

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

   protected void assertPayloadEquals(GeneratedHttpRequest<T> httpMethod, String toMatch)
            throws IOException {
      if (httpMethod.getPayload() == null) {
         assertNull(toMatch);
      } else {
         String payload = Utils.toStringAndClose(httpMethod.getPayload().getInput());
         assertEquals(payload, toMatch);
      }
   }

   protected void assertHeadersEqual(GeneratedHttpRequest<T> httpMethod, String toMatch) {
      assertEquals(HttpUtils.sortAndConcatHeadersIntoString(httpMethod.getHeaders()), toMatch);
   }

   protected void assertRequestLineEquals(GeneratedHttpRequest<T> httpMethod, String toMatch) {
      assertEquals(httpMethod.getRequestLine(), toMatch);
   }

   protected void assertExceptionParserClassEquals(Method method, @Nullable Class<?> parserClass) {
      if (parserClass == null)
         assertEquals(processor.createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(
                  method).getClass(), MapHttp4xxCodesToExceptions.class);
      else
         assertEquals(processor.createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(
                  method).getClass(), parserClass);
   }

   protected void assertSaxResponseParserClassEquals(Method method, @Nullable Class<?> parserClass) {
      assertEquals(RestAnnotationProcessor.getSaxResponseParserClassOrNull(method), parserClass);
   }

   protected void assertResponseParserClassEquals(Method method,
            GeneratedHttpRequest<T> httpMethod, @Nullable Class<?> parserClass) {
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(), parserClass);
   }

}