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
package org.jclouds.http.utils;

import static org.jclouds.http.utils.ModifyRequest.parseQueryToMap;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ModifyRequestTest {

   public void testEndpoint() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).build();

      assertEquals(ModifyRequest.endpoint(request, URI.create("http://bar")), HttpRequest.builder().method("GET")
            .endpoint(URI.create("http://bar")).build());
   }

   public void testReplaceHeader() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo"))
            .headers(ImmutableMultimap.of("foo", "bar")).build();

      assertEquals(
            ModifyRequest.replaceHeader(request, "foo", "baz"),
            HttpRequest.builder().method("GET").endpoint(URI.create("http://foo"))
                  .headers(ImmutableMultimap.of("foo", "baz")).build());
   }

   public void testRemoveHeader() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo"))
            .headers(ImmutableMultimap.of("foo", "bar")).build();

      assertEquals(ModifyRequest.removeHeader(request, "foo"),
            HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).build());
   }

   public void testReplaceHeaders() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo"))
            .headers(ImmutableMultimap.of("foo", "bar", "rabbit", "tree")).build();

      assertEquals(
            ModifyRequest.replaceHeaders(request,
                  ImmutableMultimap.of("foo", "bar", "rabbit", "robot", "robert", "baz")),
            HttpRequest.builder().method("GET").endpoint(URI.create("http://foo"))
                  .headers(ImmutableMultimap.of("foo", "bar", "rabbit", "robot", "robert", "baz")).build());
   }

   public void testPutHeadersAddsAnotherValue() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo"))
            .headers(ImmutableMultimap.of("foo", "bar")).build();

      assertEquals(
            ModifyRequest.putHeaders(request, ImmutableMultimap.of("foo", "baz")),
            HttpRequest.builder().method("GET").endpoint(URI.create("http://foo"))
                  .headers(ImmutableMultimap.<String, String> builder().put("foo", "bar").put("foo", "baz").build())
                  .build());
   }

   public void testPutFormParamsAddsAnotherValue() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo"))
            .payload(Payloads.newStringPayload("foo=bar")).build();
      Payload payload = Payloads.newStringPayload("foo=bar&foo=baz");
      payload.getContentMetadata().setContentType(MediaType.APPLICATION_FORM_URLENCODED);
      assertEquals(ModifyRequest.putFormParams(request, ImmutableMultimap.of("foo", "baz")), HttpRequest.builder()
            .method("GET").endpoint(URI.create("http://foo")).payload(payload).build());
   }

   public void testParseBase64InForm() {
      Multimap<String, String> expects = LinkedListMultimap.create();
      expects.put("Version", "2010-06-15");
      expects.put("Action", "ModifyInstanceAttribute");
      expects.put("Attribute", "userData");
      expects.put("Value", "dGVzdA==");
      expects.put("InstanceId", "1");
      assertEquals(
            expects,
            parseQueryToMap("Version=2010-06-15&Action=ModifyInstanceAttribute&Attribute=userData&Value=dGVzdA%3D%3D&InstanceId=1"));
   }

   @Test
   public void testParseQueryToMapSingleParam() {
      Multimap<String, String> parsedMap = parseQueryToMap("v=1.3");
      assert parsedMap.keySet().size() == 1 : "Expected 1 key, found: " + parsedMap.keySet().size();
      assert parsedMap.keySet().contains("v") : "Expected v to be a part of the keys";
      String valueForV = Iterables.getOnlyElement(parsedMap.get("v"));
      assert valueForV.equals("1.3") : "Expected the value for 'v' to be '1.3', found: " + valueForV;
   }

   @Test
   public void testParseQueryToMapMultiParam() {
      Multimap<String, String> parsedMap = parseQueryToMap("v=1.3&sig=123");
      assert parsedMap.keySet().size() == 2 : "Expected 2 keys, found: " + parsedMap.keySet().size();
      assert parsedMap.keySet().contains("v") : "Expected v to be a part of the keys";
      assert parsedMap.keySet().contains("sig") : "Expected sig to be a part of the keys";
      String valueForV = Iterables.getOnlyElement(parsedMap.get("v"));
      assert valueForV.equals("1.3") : "Expected the value for 'v' to be '1.3', found: " + valueForV;
      String valueForSig = Iterables.getOnlyElement(parsedMap.get("sig"));
      assert valueForSig.equals("123") : "Expected the value for 'v' to be '123', found: " + valueForSig;
   }

}
