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
package org.jclouds.http;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newTreeSet;
import static com.google.common.io.ByteStreams.toByteArray;
import static com.google.common.io.Closeables.closeQuietly;
import static java.util.Collections.singletonList;
import static javax.ws.rs.core.HttpHeaders.HOST;
import static org.jclouds.http.Payloads.newUrlEncodedFormPayload;
import static org.jclouds.util.Patterns.CHAR_TO_ENCODED_PATTERN;
import static org.jclouds.util.Patterns.PATTERN_THAT_BREAKS_URI;
import static org.jclouds.util.Patterns.PLUS_PATTERN;
import static org.jclouds.util.Patterns.STAR_PATTERN;
import static org.jclouds.util.Patterns.URI_PATTERN;
import static org.jclouds.util.Patterns.URL_ENCODED_PATTERN;
import static org.jclouds.util.Patterns._7E_PATTERN;
import static org.jclouds.util.Utils.replaceAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.Constants;
import org.jclouds.logging.Logger;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import com.google.inject.Inject;

/**
 * @author Adrian Cole
 */
@Singleton
public class HttpUtils {
   @Inject(optional = true)
   @Named(Constants.PROPERTY_RELAX_HOSTNAME)
   private boolean relaxHostname = false;

   @Inject(optional = true)
   @Named(Constants.PROPERTY_PROXY_SYSTEM)
   private boolean systemProxies = System.getProperty("java.net.useSystemProxies") != null ? Boolean
            .parseBoolean(System.getProperty("java.net.useSystemProxies"))
            : false;

   private final int globalMaxConnections;
   private final int globalMaxConnectionsPerHost;
   private final int connectionTimeout;
   private final int soTimeout;
   @Inject(optional = true)
   @Named(Constants.PROPERTY_PROXY_HOST)
   private String proxyHost;
   @Inject(optional = true)
   @Named(Constants.PROPERTY_PROXY_PORT)
   private Integer proxyPort;
   @Inject(optional = true)
   @Named(Constants.PROPERTY_PROXY_USER)
   private String proxyUser;
   @Inject(optional = true)
   @Named(Constants.PROPERTY_PROXY_PASSWORD)
   private String proxyPassword;

   @Inject
   public HttpUtils(@Named(Constants.PROPERTY_CONNECTION_TIMEOUT) int connectionTimeout,
            @Named(Constants.PROPERTY_SO_TIMEOUT) int soTimeout,
            @Named(Constants.PROPERTY_MAX_CONNECTIONS_PER_CONTEXT) int globalMaxConnections,
            @Named(Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST) int globalMaxConnectionsPerHost) {
      this.soTimeout = soTimeout;
      this.connectionTimeout = connectionTimeout;
      this.globalMaxConnections = globalMaxConnections;
      this.globalMaxConnectionsPerHost = globalMaxConnectionsPerHost;
   }

   /**
    * @see org.jclouds.Constants.PROPERTY_PROXY_HOST
    */
   public String getProxyHost() {
      return proxyHost;
   }

   /**
    * @see org.jclouds.Constants.PROPERTY_PROXY_PORT
    */
   public Integer getProxyPort() {
      return proxyPort;
   }

   /**
    * @see org.jclouds.Constants.PROPERTY_PROXY_USER
    */
   public String getProxyUser() {
      return proxyUser;
   }

   /**
    * @see org.jclouds.Constants.PROPERTY_PROXY_PASSWORD
    */
   public String getProxyPassword() {
      return proxyPassword;
   }

   public int getSocketOpenTimeout() {
      return soTimeout;
   }

   public int getConnectionTimeout() {
      return connectionTimeout;
   }

   public boolean relaxHostname() {
      return relaxHostname;
   }

   public boolean useSystemProxies() {
      return systemProxies;
   }

   public int getMaxConnections() {
      return globalMaxConnections;
   }

   public int getMaxConnectionsPerHost() {
      return globalMaxConnectionsPerHost;
   }

   /**
    * keys to the map are only used for socket information, not path. In this case, you should
    * remove any path or query details from the URI.
    */
   public static URI createBaseEndpointFor(URI endpoint) {
      if (endpoint.getPort() == -1) {
         return URI.create(String.format("%s://%s", endpoint.getScheme(), endpoint.getHost()));
      } else {
         return URI.create(String.format("%s://%s:%d", endpoint.getScheme(), endpoint.getHost(),
                  endpoint.getPort()));
      }
   }

   /**
    * Web browsers do not always handle '+' characters well, use the well-supported '%20' instead.
    */
   public static String urlEncode(String in, char... skipEncode) {
      if (isUrlEncoded(in))
         return in;
      try {
         String returnVal = URLEncoder.encode(in, "UTF-8");
         returnVal = replaceAll(returnVal, '+', PLUS_PATTERN, "%20");
         returnVal = replaceAll(returnVal, '*', STAR_PATTERN, "%2A");
         returnVal = replaceAll(returnVal, _7E_PATTERN, "~");
         for (char c : skipEncode) {
            returnVal = replaceAll(returnVal, CHAR_TO_ENCODED_PATTERN.get(c), c + "");
         }
         return returnVal;
      } catch (UnsupportedEncodingException e) {
         throw new IllegalStateException("Bad encoding on input: " + in, e);
      }
   }

   public static boolean isUrlEncoded(String in) {
      return URL_ENCODED_PATTERN.matcher(in).matches();
   }

   public static String urlDecode(String in) {
      try {
         return URLDecoder.decode(in, "UTF-8");
      } catch (UnsupportedEncodingException e) {
         throw new IllegalStateException("Bad encoding on input: " + in, e);
      }
   }

   /**
    * Content stream may need to be read. However, we should always close the http stream.
    */
   public static void consumeContent(HttpResponse response) {
      if (response.getContent() != null) {
         try {
            toByteArray(response.getContent());
         } catch (IOException e) {
            propagate(e);
         } finally {
            closeQuietly(response.getContent());
         }
      }
   }

   /**
    * Content stream may need to be read. However, we should always close the http stream.
    */
   public static byte[] closeClientButKeepContentStream(HttpResponse response) {
      if (response.getContent() != null) {
         try {
            byte[] data = toByteArray(response.getContent());
            response.setContent(new ByteArrayInputStream(data));
            return data;
         } catch (IOException e) {
            propagate(e);
         } finally {
            closeQuietly(response.getContent());
         }
      }
      return null;
   }

   public static URI parseEndPoint(String hostHeader) {
      URI redirectURI = URI.create(hostHeader);
      String scheme = redirectURI.getScheme();

      checkState(redirectURI.getScheme().startsWith("http"), String.format(
               "header %s didn't parse an http scheme: [%s]", hostHeader, scheme));
      int port = redirectURI.getPort() > 0 ? redirectURI.getPort() : redirectURI.getScheme()
               .equals("https") ? 443 : 80;
      String host = redirectURI.getHost();
      checkState(host.indexOf('/') == -1, String.format(
               "header %s didn't parse an http host correctly: [%s]", hostHeader, host));
      URI endPoint = URI.create(String.format("%s://%s:%d", scheme, host, port));
      return endPoint;
   }

   public static URI replaceHostInEndPoint(URI endPoint, String host) {
      return URI.create(endPoint.toString().replace(endPoint.getHost(), host));
   }

   /**
    * Used to extract the URI and authentication data from a String. Note that the java URI class
    * breaks, if there are special characters like '/' present. Otherwise, we wouldn't need this
    * class, and we could simply use URI.create("uri").getUserData(); Also, URI breaks if there are
    * curly braces.
    * 
    */
   public static URI createUri(String uriPath) {
      List<String> onQuery = newArrayList(Splitter.on('?').split(uriPath));
      if (onQuery.size() == 2) {
         onQuery.add(urlEncode(onQuery.remove(1), '=', '&'));
         uriPath = Joiner.on('?').join(onQuery);
      }
      if (uriPath.indexOf('@') != 1) {
         List<String> parts = newArrayList(Splitter.on('@').split(uriPath));
         String path = parts.remove(parts.size() - 1);
         if (parts.size() > 1) {
            parts = newArrayList(urlEncode(Joiner.on('@').join(parts), '/', ':'));
         }
         parts.add(urlEncode(path, '/', ':'));
         uriPath = Joiner.on('@').join(parts);
      } else {
         List<String> parts = newArrayList(Splitter.on('/').split(uriPath));
         String path = parts.remove(parts.size() - 1);
         parts.add(urlEncode(path, ':'));
         uriPath = Joiner.on('/').join(parts);
      }

      if (PATTERN_THAT_BREAKS_URI.matcher(uriPath).matches()) {
         // Compile and use regular expression
         Matcher matcher = URI_PATTERN.matcher(uriPath);
         if (matcher.find()) {
            String scheme = matcher.group(1);
            String rest = matcher.group(4);
            String identity = matcher.group(2);
            String key = matcher.group(3);
            return URI.create(String.format("%s://%s:%s@%s", scheme, urlEncode(identity),
                     urlEncode(key), rest));
         } else {
            throw new IllegalArgumentException("bad syntax");
         }
      } else {
         return URI.create(uriPath);
      }
   }

   public static void logRequest(Logger logger, HttpRequest request, String prefix) {
      if (logger.isDebugEnabled()) {
         logger.debug("%s %s", prefix, request.getRequestLine().toString());
         for (Entry<String, String> header : request.getHeaders().entries()) {
            if (header.getKey() != null)
               logger.debug("%s %s: %s", prefix, header.getKey(), header.getValue());
         }
      }
   }

   public static void logResponse(Logger logger, HttpResponse response, String prefix) {
      if (logger.isDebugEnabled()) {
         logger.debug("%s %s", prefix, response.getStatusLine().toString());
         for (Entry<String, String> header : response.getHeaders().entries()) {
            logger.debug("%s %s: %s", prefix, header.getKey(), header.getValue());
         }
      }
   }

   public static void copy(InputStream input, OutputStream output) throws IOException {
      byte[] buffer = new byte[1024];
      long length = 0;
      int numRead = -1;
      try {
         do {
            numRead = input.read(buffer);
            if (numRead > 0) {
               length += numRead;
               output.write(buffer, 0, numRead);
            }
         } while (numRead != -1);
      } finally {
         output.close();
         closeQuietly(input);
      }
   }

   public static String sortAndConcatHeadersIntoString(Multimap<String, String> headers) {
      StringBuffer buffer = new StringBuffer();
      SortedSetMultimap<String, String> sortedMap = TreeMultimap.create();
      sortedMap.putAll(headers);
      for (Entry<String, String> header : sortedMap.entries()) {
         if (header.getKey() != null)
            buffer.append(String.format("%s: %s\n", header.getKey(), header.getValue()));
      }
      return buffer.toString();
   }

   /**
    * change the destination of the current http command. typically used in handling redirects.
    * 
    * @param string
    */
   public static void changeSchemeHostAndPortTo(HttpRequest request, String scheme, String host,
            int port, UriBuilder builder) {
      builder.uri(request.getEndpoint());
      builder.scheme(scheme);
      builder.host(host);
      builder.port(port);
      request.setEndpoint(builder.build());
      request.getHeaders().replaceValues(HOST, singletonList(host));
   }

   /**
    * change the path of the service. typically used in handling redirects.
    */
   public static void changePathTo(HttpRequest request, String newPath, UriBuilder builder) {
      builder.uri(request.getEndpoint());
      builder.replacePath(newPath);
      request.setEndpoint(builder.build());
   }

   /**
    * change method from GET to HEAD. typically used in handling redirects.
    */
   public static void changeToGETRequest(HttpRequest request) {
      request.setMethod(HttpMethod.GET);
   }

   public static void addQueryParamTo(HttpRequest request, String key, Object value,
            UriBuilder builder) {
      addQueryParamTo(request, key, ImmutableSet.<Object> of(value), builder, request.getSkips());
   }

   public static void addQueryParamTo(HttpRequest request, String key, Iterable<?> values,
            UriBuilder builder) {
      addQueryParamTo(request, key, values, builder, request.getSkips());
   }

   public static void addQueryParamTo(HttpRequest request, String key, Iterable<?> values,
            UriBuilder builder, char... skips) {
      builder.uri(request.getEndpoint());
      Multimap<String, String> map = parseQueryToMap(request.getEndpoint().getQuery());
      for (Object o : values)
         map.put(key, o.toString());
      builder.replaceQuery(makeQueryLine(map, null, skips));
      request.setEndpoint(builder.build());
   }

   public static void replaceMatrixParam(HttpRequest request, String name, Object value,
            UriBuilder builder) {
      replaceMatrixParam(request, name, new Object[] { value }, builder);
   }

   public static void replaceMatrixParam(HttpRequest request, String name, Object[] values,
            UriBuilder builder) {
      builder.uri(request.getEndpoint());
      builder.replaceMatrixParam(name, values);
      request.setEndpoint(builder.build());
   }

   public static void addFormParamTo(HttpRequest request, String key, String value) {
      addFormParamTo(request, key, ImmutableSet.<Object> of(value));

   }

   public static void addFormParamTo(HttpRequest request, String key, Iterable<?> values) {
      Multimap<String, String> map;
      map = parseQueryToMap(request.getPayload().getRawContent().toString());
      for (Object o : values)
         map.put(key, o.toString());
      request.setPayload(newUrlEncodedFormPayload(map));
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
         String[] parts = urlDecode(in).split("&");
         for (String part : parts) {
            parseKeyValueFromStringToMap(part, map);
         }
      }
      return map;
   }

   public static void parseKeyValueFromStringToMap(String stringToParse,
            Multimap<String, String> map) {
      // note that '=' can be a valid part of the value
      int indexOfFirstEquals = stringToParse.indexOf('=');
      String key = indexOfFirstEquals == -1 ? stringToParse : stringToParse.substring(0,
               indexOfFirstEquals);
      String value = indexOfFirstEquals == -1 ? null : stringToParse
               .substring(indexOfFirstEquals + 1);
      map.put(key, value);
   }

   public static SortedSet<Entry<String, String>> sortEntries(
            Collection<Map.Entry<String, String>> in, Comparator<Map.Entry<String, String>> sorter) {
      SortedSet<Entry<String, String>> entries = newTreeSet(sorter);
      entries.addAll(in);
      return entries;
   }

   public static String makeQueryLine(Multimap<String, String> params,
            @Nullable Comparator<Map.Entry<String, String>> sorter, char... skips) {
      Iterator<Map.Entry<String, String>> pairs = ((sorter == null) ? params.entries()
               : sortEntries(params.entries(), sorter)).iterator();
      StringBuilder formBuilder = new StringBuilder();
      while (pairs.hasNext()) {
         Map.Entry<String, String> pair = pairs.next();
         formBuilder.append(urlEncode(pair.getKey(), skips));
         if (pair.getValue() != null && !pair.getValue().equals("")) {
            formBuilder.append("=");
            formBuilder.append(urlEncode(pair.getValue(), skips));
         }
         if (pairs.hasNext())
            formBuilder.append("&");
      }
      return formBuilder.toString();
   }
}
