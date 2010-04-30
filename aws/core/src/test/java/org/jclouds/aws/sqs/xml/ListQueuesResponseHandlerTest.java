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
package org.jclouds.aws.sqs.xml;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jclouds.PerformanceTest;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.sqs.SQS;
import org.jclouds.aws.sqs.domain.Queue;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.ParserModule;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * Tests behavior of {@code ListQueuesResponseHandlerr}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "sqs.ListQueuesResponseHandlerrTest")
public class ListQueuesResponseHandlerTest extends PerformanceTest {

   private Injector injector;
   private Factory factory;
   private RegexListQueuesResponseHandler handler;
   private InputSupplier<ByteArrayInputStream> supplier;

   @BeforeTest
   protected void setUpInjector() throws IOException {

      LOOP_COUNT = 100000;
      THREAD_COUNT = 100;

      System.out.printf("queue response handle speed test %d threads %d count%n", THREAD_COUNT,
               LOOP_COUNT);
      injector = Guice.createInjector(new ParserModule(), new AbstractModule() {

         @Override
         protected void configure() {
            bind(UriBuilder.class).to(UriBuilderImpl.class);
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         @SQS
         Map<String, URI> provideRegions() {
            return ImmutableMap.<String, URI> of(Region.EU_WEST_1, URI
                     .create("https://eu-west-1.queue.amazonaws.com"));
         }

      });
      handler = injector.getInstance(RegexListQueuesResponseHandler.class);

      factory = injector.getInstance(ParseSax.Factory.class);
      InputStream inputStream = getClass().getResourceAsStream("/sqs/list_queues.xml");
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ByteStreams.copy(inputStream, out);
      supplier = ByteStreams.newInputStreamSupplier(out.toByteArray());
      assert factory != null;
   }

   Set<Queue> expected = ImmutableSet.of(new Queue(Region.EU_WEST_1, "adriancole-sqs1", URI
            .create("https://eu-west-1.queue.amazonaws.com/993194456877/adriancole-sqs1")),

   new Queue(Region.EU_WEST_1, "adriancole-sqs111", URI
            .create("https://eu-west-1.queue.amazonaws.com/993194456877/adriancole-sqs111")));

   public void testSax() {
      ListQueuesResponseHandler handler = injector.getInstance(ListQueuesResponseHandler.class);
      Set<Queue> result;
      try {
         result = factory.create(handler).parse(supplier.getInput());
         assertEquals(result, expected);
      } catch (Exception e) {
         Throwables.propagate(e);
      }
   }

   public void testRegex() {
      try {
         assertEquals(handler.apply(new HttpResponse(supplier.getInput())), expected);
      } catch (IOException e) {
         Throwables.propagate(e);
      }
   }

   @Test(enabled = false)
   void testRegexSerialResponseTime() throws IOException {
      long now = System.currentTimeMillis();
      for (int i = 0; i < LOOP_COUNT; i++)
         testRegex();
      System.out.println("testRegex serial: " + (System.currentTimeMillis() - now) + "");
   }

   @Test(enabled = false)
   void testRegexParallelResponseTime() throws Throwable {
      List<Runnable> tasks = ImmutableList.<Runnable> of(new Runnable() {
         public void run() {
            testRegex();
         }
      });
      executeMultiThreadedPerformanceTest("testRegexParallelResponseTime", tasks);
   }

   @Test(enabled = false)
   void testSaxSerialResponseTime() throws IOException {
      long now = System.currentTimeMillis();
      for (int i = 0; i < LOOP_COUNT; i++)
         testSax();
      System.out.println("testSax serial: " + (System.currentTimeMillis() - now) + "");
   }

   @Test(enabled = false)
   void testSaxParallelResponseTime() throws Throwable {
      List<Runnable> tasks = ImmutableList.<Runnable> of(new Runnable() {
         public void run() {
            testSax();
         }
      });
      executeMultiThreadedPerformanceTest("testSaxParallelResponseTime", tasks);
   }

}
