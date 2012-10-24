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

package org.jclouds.abiquo.http.filters;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Collection;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.functions.AppendApiVersionToAbiquoMimeType;
import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * Unit tests for the {@link AppendApiVersionToMediaType} filter.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "AppendApiVersionToMediaTypeTest")
public class AppendApiVersionToMediaTypeTest {

   public void testAppendVersionToNonPayloadHeadersWithoutHeaders() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).build();

      AppendApiVersionToMediaType filter = new AppendApiVersionToMediaType(new AppendApiVersionToAbiquoMimeType(
            AbiquoAsyncApi.API_VERSION));

      HttpRequest filtered = filter.appendVersionToNonPayloadHeaders(request);

      assertTrue(filtered.getHeaders().get(HttpHeaders.ACCEPT).isEmpty());
   }

   public void testAppendVersionToNonPayloadHeadersWithStandardMediaType() {
      Multimap<String, String> headers = LinkedHashMultimap.<String, String> create();
      headers.put(HttpHeaders.ACCEPT, "application/xml");

      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).headers(headers)
            .build();

      AppendApiVersionToMediaType filter = new AppendApiVersionToMediaType(new AppendApiVersionToAbiquoMimeType(
            AbiquoAsyncApi.API_VERSION));

      HttpRequest filtered = filter.appendVersionToNonPayloadHeaders(request);

      Collection<String> contentType = filtered.getHeaders().get(HttpHeaders.ACCEPT);
      assertEquals(contentType.size(), 1);
      assertEquals(contentType.iterator().next(), "application/xml");
   }

   public void testAppendVersionToNonPayloadHeadersWithVersionInMediaType() {
      Multimap<String, String> headers = LinkedHashMultimap.<String, String> create();
      headers.put(HttpHeaders.ACCEPT, "application/vnd.abiquo.racks+xml;version=2.1-SNAPSHOT");

      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).headers(headers)
            .build();

      AppendApiVersionToMediaType filter = new AppendApiVersionToMediaType(new AppendApiVersionToAbiquoMimeType(
            AbiquoAsyncApi.API_VERSION));

      HttpRequest filtered = filter.appendVersionToNonPayloadHeaders(request);

      Collection<String> contentType = filtered.getHeaders().get(HttpHeaders.ACCEPT);
      assertEquals(contentType.size(), 1);
      assertEquals(contentType.iterator().next(), "application/vnd.abiquo.racks+xml;version=2.1-SNAPSHOT");
   }

   public void testAppendVersionToNonPayloadHeadersWithoutVersionInMediaType() {
      Multimap<String, String> headers = LinkedHashMultimap.<String, String> create();
      headers.put(HttpHeaders.ACCEPT, "application/vnd.abiquo.racks+xml");

      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).headers(headers)
            .build();

      AppendApiVersionToMediaType filter = new AppendApiVersionToMediaType(new AppendApiVersionToAbiquoMimeType(
            AbiquoAsyncApi.API_VERSION));

      HttpRequest filtered = filter.appendVersionToNonPayloadHeaders(request);

      Collection<String> accept = filtered.getHeaders().get(HttpHeaders.ACCEPT);
      assertEquals(accept.size(), 1);
      assertEquals(accept.iterator().next(), "application/vnd.abiquo.racks+xml;version=" + AbiquoAsyncApi.API_VERSION);
   }

   public void testAppendVersionToPayloadHeadersWithoutPayload() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).build();

      AppendApiVersionToMediaType filter = new AppendApiVersionToMediaType(new AppendApiVersionToAbiquoMimeType(
            AbiquoAsyncApi.API_VERSION));

      HttpRequest filtered = filter.appendVersionToPayloadHeaders(request);

      assertNull(filtered.getPayload());
   }

   public void testAppendVersionToPayloadHeadersWithStandardPayload() {
      Payload payload = Payloads.newByteArrayPayload(new byte[] {});
      payload.getContentMetadata().setContentType("application/xml");

      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).payload(payload)
            .build();

      AppendApiVersionToMediaType filter = new AppendApiVersionToMediaType(new AppendApiVersionToAbiquoMimeType(
            AbiquoAsyncApi.API_VERSION));

      HttpRequest filtered = filter.appendVersionToPayloadHeaders(request);

      assertEquals(filtered.getPayload().getContentMetadata().getContentType(), "application/xml");
   }

   public void testAppendVersionToPayloadHeadersWithDefaultPayload() {
      Payload payload = Payloads.newByteArrayPayload(new byte[] {});

      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).payload(payload)
            .build();

      AppendApiVersionToMediaType filter = new AppendApiVersionToMediaType(new AppendApiVersionToAbiquoMimeType(
            AbiquoAsyncApi.API_VERSION));

      HttpRequest filtered = filter.appendVersionToPayloadHeaders(request);

      assertEquals(filtered.getPayload().getContentMetadata().getContentType(), "application/unknown");
   }

   public void testAppendVersionToPayloadHeadersWithVersionInPayload() {
      Payload payload = Payloads.newByteArrayPayload(new byte[] {});
      payload.getContentMetadata().setContentType("application/vnd.abiquo.racks+xml;version=1.8.5");

      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).payload(payload)
            .build();

      AppendApiVersionToMediaType filter = new AppendApiVersionToMediaType(new AppendApiVersionToAbiquoMimeType(
            AbiquoAsyncApi.API_VERSION));

      HttpRequest filtered = filter.appendVersionToPayloadHeaders(request);

      assertEquals(filtered.getPayload().getContentMetadata().getContentType(),
            "application/vnd.abiquo.racks+xml;version=1.8.5");
   }

   public void testAppendVersionToPayloadHeadersWithoutVersionInPayload() {
      Payload payload = Payloads.newByteArrayPayload(new byte[] {});
      payload.getContentMetadata().setContentType("application/vnd.abiquo.racks+xml");

      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).payload(payload)
            .build();

      AppendApiVersionToMediaType filter = new AppendApiVersionToMediaType(new AppendApiVersionToAbiquoMimeType(
            AbiquoAsyncApi.API_VERSION));

      HttpRequest filtered = filter.appendVersionToPayloadHeaders(request);

      assertEquals(filtered.getPayload().getContentMetadata().getContentType(),
            "application/vnd.abiquo.racks+xml;version=" + AbiquoAsyncApi.API_VERSION);
   }

   public void testFilterWithAcceptAndContentTypeWithVersion() {
      Payload payload = Payloads.newByteArrayPayload(new byte[] {});
      payload.getContentMetadata().setContentType("application/vnd.abiquo.racks+xml;version=2.1-SNAPSHOT");

      Multimap<String, String> headers = LinkedHashMultimap.<String, String> create();
      headers.put(HttpHeaders.ACCEPT, "application/vnd.abiquo.racks+xml;version=2.1-SNAPSHOT");

      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).headers(headers)
            .payload(payload).build();

      AppendApiVersionToMediaType filter = new AppendApiVersionToMediaType(new AppendApiVersionToAbiquoMimeType(
            AbiquoAsyncApi.API_VERSION));

      HttpRequest filtered = filter.filter(request);

      Collection<String> accept = filtered.getHeaders().get(HttpHeaders.ACCEPT);
      assertEquals(accept.size(), 1);
      assertEquals(accept.iterator().next(), "application/vnd.abiquo.racks+xml;version=2.1-SNAPSHOT");

      assertEquals(filtered.getPayload().getContentMetadata().getContentType(),
            "application/vnd.abiquo.racks+xml;version=2.1-SNAPSHOT");
   }

   public void testFilterWithAcceptAndContentTypeWithoutVersion() {
      Payload payload = Payloads.newByteArrayPayload(new byte[] {});
      payload.getContentMetadata().setContentType("application/vnd.abiquo.racks+xml");

      Multimap<String, String> headers = LinkedHashMultimap.<String, String> create();
      headers.put(HttpHeaders.ACCEPT, "application/vnd.abiquo.racks+xml");

      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).headers(headers)
            .payload(payload).build();

      AppendApiVersionToMediaType filter = new AppendApiVersionToMediaType(new AppendApiVersionToAbiquoMimeType(
            AbiquoAsyncApi.API_VERSION));

      HttpRequest filtered = filter.filter(request);

      Collection<String> accept = filtered.getHeaders().get(HttpHeaders.ACCEPT);
      assertEquals(accept.size(), 1);
      assertEquals(accept.iterator().next(), "application/vnd.abiquo.racks+xml;version=" + AbiquoAsyncApi.API_VERSION);

      assertEquals(filtered.getPayload().getContentMetadata().getContentType(),
            "application/vnd.abiquo.racks+xml;version=" + AbiquoAsyncApi.API_VERSION);
   }

   public void testFilterWithversionInAccept() {
      Payload payload = Payloads.newByteArrayPayload(new byte[] {});
      payload.getContentMetadata().setContentType("application/vnd.abiquo.racks+xml");

      Multimap<String, String> headers = LinkedHashMultimap.<String, String> create();
      headers.put(HttpHeaders.ACCEPT, "application/vnd.abiquo.racks+xml;version=1.8.5");

      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).headers(headers)
            .payload(payload).build();

      AppendApiVersionToMediaType filter = new AppendApiVersionToMediaType(new AppendApiVersionToAbiquoMimeType(
            AbiquoAsyncApi.API_VERSION));

      HttpRequest filtered = filter.filter(request);

      Collection<String> accept = filtered.getHeaders().get(HttpHeaders.ACCEPT);
      assertEquals(accept.size(), 1);
      assertEquals(accept.iterator().next(), "application/vnd.abiquo.racks+xml;version=1.8.5");

      assertEquals(filtered.getPayload().getContentMetadata().getContentType(),
            "application/vnd.abiquo.racks+xml;version=" + AbiquoAsyncApi.API_VERSION);
   }

   public void testFilterWithversionInContentType() {
      Payload payload = Payloads.newByteArrayPayload(new byte[] {});
      payload.getContentMetadata().setContentType("application/vnd.abiquo.racks+xml;version=1.8.5");

      Multimap<String, String> headers = LinkedHashMultimap.<String, String> create();
      headers.put(HttpHeaders.ACCEPT, "application/vnd.abiquo.racks+xml");

      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).headers(headers)
            .payload(payload).build();

      AppendApiVersionToMediaType filter = new AppendApiVersionToMediaType(new AppendApiVersionToAbiquoMimeType(
            AbiquoAsyncApi.API_VERSION));

      HttpRequest filtered = filter.filter(request);

      Collection<String> accept = filtered.getHeaders().get(HttpHeaders.ACCEPT);
      assertEquals(accept.size(), 1);
      assertEquals(accept.iterator().next(), "application/vnd.abiquo.racks+xml;version=" + AbiquoAsyncApi.API_VERSION);

      assertEquals(filtered.getPayload().getContentMetadata().getContentType(),
            "application/vnd.abiquo.racks+xml;version=1.8.5");
   }
}
