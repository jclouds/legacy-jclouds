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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.io.Payloads.newUrlEncodedFormPayload;

import java.net.URI;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.http.HttpRequest;
import org.jclouds.util.Strings2;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/**
 * 
 * @author Adrian Cole
 */
public class ModifyRequest {
   @SuppressWarnings("unchecked")
   public static <R extends HttpRequest> R putHeaders(R request, Multimap<String, String> moreHeaders) {
      return (R) request.toBuilder().headers(
               ImmutableMultimap.<String, String> builder().putAll(request.getHeaders()).putAll(moreHeaders).build())
               .build();
   }

   @SuppressWarnings("unchecked")
   public static <R extends HttpRequest> R endpoint(R request, URI endpoint) {
      return (R) checkNotNull(request, "request").toBuilder().endpoint(checkNotNull(endpoint, "endpoint")).build();
   }

   @SuppressWarnings("unchecked")
   public static <R extends HttpRequest> R replaceHeaders(R request, Multimap<String, String> headers) {
      Multimap<String, String> newHeaders = LinkedHashMultimap.create(checkNotNull(request, "request").getHeaders());
      for (String header : headers.keySet())
         newHeaders.replaceValues(header, headers.get(header));
      return (R) request.toBuilder().headers(newHeaders).build();
   }

   public static <R extends HttpRequest> R replaceHeader(R request, String header, String... values) {
      return replaceHeader(request, header, ImmutableList.copyOf(checkNotNull(values, "values")));
   }

   @SuppressWarnings("unchecked")
   public static <R extends HttpRequest> R replaceHeader(R request, String header, Iterable<String> values) {
      Multimap<String, String> headers = LinkedHashMultimap.create(checkNotNull(request, "request").getHeaders());
      headers.replaceValues(checkNotNull(header, "header"), checkNotNull(values, "values"));
      return (R) request.toBuilder().headers(headers).build();
   }

   @SuppressWarnings("unchecked")
   public static <R extends HttpRequest> R removeHeader(R request, String header) {
      Multimap<String, String> headers = LinkedHashMultimap.create(checkNotNull(request, "request").getHeaders());
      headers.removeAll(checkNotNull(header, "header"));
      return (R) request.toBuilder().headers(headers).build();
   }

   public static <R extends HttpRequest> R addQueryParam(R request, String key, Object value, UriBuilder builder) {
      return addQueryParam(request, key, ImmutableSet.<Object> of(value), builder, request.getSkips());
   }

   public static <R extends HttpRequest> R addQueryParam(R request, String key, Iterable<?> values, UriBuilder builder) {
      return addQueryParam(request, key, values, builder, request.getSkips());
   }

   @SuppressWarnings("unchecked")
   public static <R extends HttpRequest> R addQueryParam(R request, String key, Iterable<?> values, UriBuilder builder,
            char... skips) {
      builder.uri(request.getEndpoint());
      Multimap<String, String> map = parseQueryToMap(request.getEndpoint().getQuery());
      for (Object o : values)
         map.put(key, o.toString());
      builder.replaceQuery(makeQueryLine(map, null, skips));
      return (R) request.toBuilder().endpoint(builder.build()).build();
   }

   public static <R extends HttpRequest> R replaceMatrixParam(R request, String name, Object value, UriBuilder builder) {
      return replaceMatrixParam(request, name, new Object[] { value }, builder);
   }

   @SuppressWarnings("unchecked")
   public static <R extends HttpRequest> R replaceMatrixParam(R request, String name, Object[] values,
            UriBuilder builder) {
      builder.uri(request.getEndpoint());
      builder.replaceMatrixParam(name, values);
      return (R) request.toBuilder().endpoint(builder.build()).build();
   }

   public static <R extends HttpRequest> R addFormParam(R request, String key, String value) {
      return addFormParam(request, key, ImmutableSet.<Object> of(value));
   }

   @SuppressWarnings("unchecked")
   public static <R extends HttpRequest> R addFormParam(R request, String key, Iterable<?> values) {
      Multimap<String, String> map = request.getPayload() != null ? parseQueryToMap(request.getPayload()
               .getRawContent().toString()) : LinkedHashMultimap.<String, String> create();
      for (Object o : values)
         map.put(key, o.toString());
      return (R) request.toBuilder().payload(newUrlEncodedFormPayload(map)).build();
   }

   @SuppressWarnings("unchecked")
   public static <R extends HttpRequest> R putFormParams(R request, Multimap<String, String> params) {
      Multimap<String, String> map = request.getPayload() != null ? parseQueryToMap(request.getPayload()
               .getRawContent().toString()) : LinkedHashMultimap.<String, String> create();
      map.putAll(params);
      return (R) request.toBuilder().payload(newUrlEncodedFormPayload(map)).build();
   }

   public static Multimap<String, String> parseQueryToMap(String in) {
      Multimap<String, String> map = LinkedListMultimap.create();
      if (in == null) {
      } else if (in.indexOf('&') == -1) {
         if (in.contains("="))
            parseKeyValueFromStringToMap(in, map);
         else
            map.put(in, null);
      } else {
         String[] parts = Strings2.urlDecode(in).split("&");
         for (String part : parts) {
            parseKeyValueFromStringToMap(part, map);
         }
      }
      return map;
   }

   public static void parseKeyValueFromStringToMap(String stringToParse, Multimap<String, String> map) {
      // note that '=' can be a valid part of the value
      int indexOfFirstEquals = stringToParse.indexOf('=');
      String key = indexOfFirstEquals == -1 ? stringToParse : stringToParse.substring(0, indexOfFirstEquals);
      String value = indexOfFirstEquals == -1 ? null : stringToParse.substring(indexOfFirstEquals + 1);
      map.put(key, value);
   }

   public static String makeQueryLine(Multimap<String, String> params,
            @Nullable Comparator<Map.Entry<String, String>> sorter, char... skips) {
      Iterator<Map.Entry<String, String>> pairs = ((sorter == null) ? params.entries() : ImmutableSortedSet.copyOf(
               sorter, params.entries())).iterator();
      StringBuilder formBuilder = new StringBuilder();
      while (pairs.hasNext()) {
         Map.Entry<String, String> pair = pairs.next();
         formBuilder.append(Strings2.urlEncode(pair.getKey(), skips));
         if (pair.getValue() != null)
            formBuilder.append("=");
         if (pair.getValue() != null && !pair.getValue().equals("")) {
            formBuilder.append(Strings2.urlEncode(pair.getValue(), skips));
         }
         if (pairs.hasNext())
            formBuilder.append("&");
      }
      return formBuilder.toString();
   }
}
