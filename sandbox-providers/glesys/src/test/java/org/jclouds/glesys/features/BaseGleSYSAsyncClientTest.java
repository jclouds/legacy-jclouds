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
package org.jclouds.glesys.features;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import org.jclouds.glesys.GleSYSAsyncClient;
import org.jclouds.glesys.GleSYSClient;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.http.options.BaseHttpRequestOptions;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.testng.Assert.*;

/**
 * @author Adrian Cole
 */
public abstract class BaseGleSYSAsyncClientTest<T> extends RestClientTest<T> {
   protected Class asyncClientClass;
   protected String remoteServicePrefix;

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), BasicAuthentication.class);
   }

   @Override
   public RestContextSpec<GleSYSClient, GleSYSAsyncClient> createContextSpec() {
      Properties props = new Properties();
      return new RestContextFactory().createContextSpec("glesys", "username", "apiKey", props);
   }

   protected Map.Entry<String, String> newEntry(String key, Object value) {
      return Maps.immutableEntry(key, value.toString());
   }

   /**
    * Test that a method call is annotated correctly.
    * <p/>
    * TODO de-code ampersands and spaces in args properly
    *
    * @param localMethod     the method to call in asyncClientClass
    * @param remoteCall      the name of the expected call on the remote server
    * @param httpMethod      "GET" or "POST"
    * @param expectResponse  if true check Accept header and response parsers
    * @param exceptionParser the class of exception handler expected
    * @param args            either Map.Entry or BaseHttpRequestOptions that make up the arguments to the method
    */
   protected void testMethod(String localMethod, String remoteCall, String httpMethod, boolean expectResponse, Class exceptionParser, Object... args) throws Exception {
      List<String> argStrings = new ArrayList<String>();
      List<Object> argValues = new ArrayList<Object>();

      for (Object arg : args) {
         if (arg instanceof BaseHttpRequestOptions) {
            for (Map.Entry<String, String> httpEntry : ((BaseHttpRequestOptions) arg).buildFormParameters().entries()) {
               argStrings.add(httpEntry.getKey() + "=" + httpEntry.getValue());
            }
            argValues.add(arg);
         } else {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) arg;
            argStrings.add(entry.getKey() + "=" + entry.getValue());
            argValues.add(entry.getValue());
         }
      }

      Method method = null;
      for (Method m : asyncClientClass.getMethods()) {
         if (m.getName().equals(localMethod)) {
            assertNull(method, "More than one method called " + localMethod + " in class " + asyncClientClass);
            method = m;
         }
      }

      assertNotNull(method, "Failed to locate method " + localMethod + " in class " + asyncClientClass);

      HttpRequest httpRequest = processor.createRequest(method, argValues.toArray());

      assertRequestLineEquals(httpRequest, httpMethod + " https://api.glesys.com/" + remoteServicePrefix + "/" + remoteCall + "/format/json HTTP/1.1");

      if (expectResponse) {
         assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
         assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      }

      if (argStrings.isEmpty()) {
         assertPayloadEquals(httpRequest, null, null, false);
      } else {
         assertNotNull(httpRequest.getPayload());
         String payload = (String) httpRequest.getPayload().getRawContent();
         Iterable<String> in = Splitter.on("&").split(payload);
         assertContentHeadersEqual(httpRequest, "application/x-www-form-urlencoded", null, null, null, 0L + payload.length(), null);
         assertEquals(ImmutableSortedSet.copyOf(in), ImmutableSortedSet.copyOf(argStrings));

      }

      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, exceptionParser);

      checkFilters(httpRequest);
   }

}
