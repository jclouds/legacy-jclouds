/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.http;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.Multimaps.forMap;
import static org.jclouds.http.utils.Queries.buildQueryLine;
import static org.jclouds.http.utils.Queries.encodeQueryLine;
import static org.jclouds.http.utils.Queries.queryParser;
import static org.jclouds.util.Strings2.urlDecode;
import static org.jclouds.util.Strings2.urlEncode;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * Functions on {@code String}s and {@link URI}s. Strings can be level 1 <a
 * href="http://tools.ietf.org/html/rfc6570">RFC6570</a> form.
 * 
 * ex.
 * 
 * <pre>
 *  https://api.github.com/repos/{user}
 * </pre>
 * 
 * <h4>Reminder</h4>
 * 
 * Unresolved <a href="http://tools.ietf.org/html/rfc6570">RFC6570</a> templates are not supported by
 * {@link URI#create(String)} and result in an {@link IllegalArgumentException}.
 * 
 * <h4>Limitations</h4>
 * 
 * In order to reduce complexity not needed in jclouds, this doesn't support {@link URI#getUserInfo()},
 * {@link URI#getFragment()}, or {@code matrix} params. Matrix params can be achieved via adding {@code ;} refs in the
 * http path directly. Moreover, since jclouds only uses level 1 templates, this doesn't support the additional forms
 * noted in the RFC.
 * 
 * @since 1.6
 * 
 * @author Adrian Cole
 * 
 */
@Beta
public final class Uris {

   /**
    * @param template
    *           URI string that can be in level 1 <a href="http://tools.ietf.org/html/rfc6570">RFC6570</a> form.
    */
   public static UriBuilder uriBuilder(CharSequence template) {
      return new UriBuilder(template);
   }

   /**
    * @param in
    *           uri
    */
   public static UriBuilder uriBuilder(URI uri) {
      return new UriBuilder(uri);
   }

   /**
    * Mutable URI builder that can be in level 1 <a href="http://tools.ietf.org/html/rfc6570">RFC6570</a> template form.
    * 
    * ex.
    * 
    * <pre>
    *  https://api.github.com/repos/{user}
    * </pre>
    * 
    */
   public static final class UriBuilder {
      // colon for urns, semicolon & equals for matrix params
      private Iterable<Character> skipPathEncoding = Lists.charactersOf("/:;=");
      private String scheme;
      private String host;
      private Integer port;
      private String path;
      private Multimap<String, Object> query = DecodingMultimap.create();

      /**
       * override default of {@code / : ; =}
       * @param scheme
       *           scheme to set or replace
       */
      public UriBuilder skipPathEncoding(Iterable<Character> skipPathEncoding) {
         this.skipPathEncoding = ImmutableSet.copyOf(checkNotNull(skipPathEncoding, "skipPathEncoding"));
         return this;
      }
      
      /**
       * @param scheme
       *           scheme to set or replace
       */
      public UriBuilder scheme(String scheme) {
         this.scheme = checkNotNull(scheme, "scheme");
         return this;
      }

      /**
       * @param host
       *           host to set or replace
       * @return replaced value
       */
      public UriBuilder host(String host) {
         this.host = checkNotNull(host, "host");
         return this;
      }

      public UriBuilder path(@Nullable String path) {
         path = emptyToNull(path);
         if (path == null)
            this.path = null;
         else
            this.path = prefixIfNeeded(urlDecode(path));
         return this;
      }

      public UriBuilder appendPath(String path) {
         path = urlDecode(checkNotNull(path, "path"));
         if (this.path == null) {
            path(path);
         } else {
            path(slash(this.path, path));
         }
         return this;
      }

      public UriBuilder query(@Nullable String queryLine) {
         if (query == null)
            return clearQuery();
         return query(queryParser().apply(queryLine));
      }

      public UriBuilder clearQuery() {
         query.clear();
         return this;
      }

      public UriBuilder query(Multimap<String, ?> parameters) {
         checkNotNull(parameters, "parameters");
         query.clear();
         query.putAll(parameters);
         return this;
      }

      public UriBuilder addQuery(String name, Iterable<?> values) {
         query.putAll(checkNotNull(name, "name"), checkNotNull(values, "values of %s", name));
         return this;
      }

      public UriBuilder addQuery(String name, String... values) {
         return addQuery(name, Arrays.asList(checkNotNull(values, "values of %s", name)));
      }

      public UriBuilder addQuery(Multimap<String, ?> parameters) {
         query.putAll(checkNotNull(parameters, "parameters"));
         return this;
      }

      public UriBuilder replaceQuery(String name, Iterable<?> values) {
         query.replaceValues(checkNotNull(name, "name"), checkNotNull(values, "values of %s", name));
         return this;
      }

      public UriBuilder replaceQuery(String name, String... values) {
         return replaceQuery(name, Arrays.asList(checkNotNull(values, "values of %s", name)));
      }

      public UriBuilder replaceQuery(Map<String, ?> parameters) {
         return replaceQuery(forMap(parameters));
      }

      public UriBuilder replaceQuery(Multimap<String, ?> parameters) {
         for (String key : checkNotNull(parameters, "parameters").keySet())
            replaceQuery(key, parameters.get(key));
         return this;
      }

      /**
       * <a href="http://tools.ietf.org/html/rfc6570">RFC6570</a> templates have variables defined in curly braces.
       * Curly brace characters are unparsable via {@link URI#create} and result in an {@link IllegalArgumentException}.
       * 
       * This implementation temporarily replaces curly braces with double parenthesis so that it can reuse
       * {@link URI#create}.
       * 
       * @param uri
       *           template which may have template parameters inside
       */
      private UriBuilder(CharSequence uri) {
         this(URI.create(escapeSpecialChars(checkNotNull(uri, "uri"))));
      }

      private static String escapeSpecialChars(CharSequence uri) {
         // skip encoding if there's no valid variables set. ex. {a} is the left valid
         if (uri.length() < 3)
            return uri.toString();

         // duplicates memory even if there are no special characters, however only requires a single scan.
         StringBuilder builder = new StringBuilder();
         for (char c : Lists.charactersOf(uri)) {
            switch (c) {
            case '{':
               builder.append("((");
               break;
            case '}':
               builder.append("))");
               break;
            default:
               builder.append(c);
            }
         }
         return builder.toString();
      }

      private static String unescapeSpecialChars(CharSequence uri) {
         if (uri.length() < 5) // skip encoding if there's no valid variables set. ex. ((a)) is the left valid
            return uri.toString();

         char last = uri.charAt(0);// duplicates even if there are no special characters, but only requires 1 scan
         StringBuilder builder = new StringBuilder();
         for (char c : Lists.charactersOf(uri)) {
            switch (c) {
            case '(':
               if (last == '(') {
                  builder.setCharAt(builder.length() - 1, '{');
               } else {
                  builder.append('(');
               }
               break;
            case ')':
               if (last == ')') {
                  builder.setCharAt(builder.length() - 1, '}');
               } else {
                  builder.append(')');
               }
               break;
            default:
               builder.append(c);
            }
            last = c;
         }
         return builder.toString();
      }

      private UriBuilder(URI uri) {
         checkNotNull(uri, "uri");
         this.scheme = uri.getScheme();
         this.host = uri.getHost();
         this.port = uri.getPort() == -1 ? null : uri.getPort();
         if (uri.getPath() != null)
            path(unescapeSpecialChars(uri.getPath()));
         if (uri.getQuery() != null)
            query(queryParser().apply(unescapeSpecialChars(uri.getQuery())));
      }

      public URI build() {
         return build(ImmutableMap.<String, Object> of());
      }

      /**
       * @throws IllegalArgumentException
       *            if there's a problem parsing the URI
       */
      public URI build(Map<String, ?> variables) {
         try {
            return new URI(expand(variables));
         } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
         }
      }

      private String expand(Map<String, ?> variables) {
         StringBuilder b = new StringBuilder();
         if (scheme != null)
            b.append(scheme).append("://");
         if (host != null)
            b.append(UriTemplates.expand(host, variables));
         if (port != null)
            b.append(':').append(port);
         if (path != null)
            b.append(urlEncode(UriTemplates.expand(path, variables), skipPathEncoding));
         if (query.size() > 0)
            b.append('?').append(encodeQueryLine(query));
         return b.toString();
      }

      /**
       * returns template expression without url encoding
       */
      @Override
      public String toString() {
         StringBuilder b = new StringBuilder();
         if (scheme != null)
            b.append(scheme).append("://");
         if (host != null)
            b.append(host);
         if (port != null)
            b.append(':').append(port);
         if (path != null)
            b.append(path);
         if (query.size() > 0)
            b.append('?').append(buildQueryLine(query));
         return b.toString();
      }
   }

   private static String slash(CharSequence left, CharSequence right) {
      return delimit(left, right, '/');
   }

   private static String delimit(CharSequence left, CharSequence right, char token) {
      if (left.length() == 0)
         return right.toString();
      if (right.length() == 0)
         return left.toString();
      StringBuilder builder = new StringBuilder(left);
      if (lastChar(left) == token) {
         if (firstChar(right) == token) // left/ + /right
            return builder.append(right.subSequence(1, right.length())).toString();
         return builder.append(right).toString(); // left/ + right
      } else if (firstChar(right) == token) {
         return builder.append(right).toString(); // left + /right
      } // left + / + right
      return new StringBuilder(left).append(token).append(right).toString();
   }

   public static boolean lastCharIsToken(CharSequence left, char token) {
      return lastChar(left) == token;
   }

   public static char lastChar(CharSequence in) {
      return in.charAt(in.length() - 1);
   }

   public static char firstChar(CharSequence in) {
      return in.charAt(0);
   }

   public static boolean isToken(CharSequence right, char token) {
      return right.length() == 1 && right.charAt(0) == token;
   }

   private static String prefixIfNeeded(String in) {
      if (in != null && in.charAt(0) != '/')
         return new StringBuilder().append('/').append(in).toString();
      return in;
   }

   /**
    * Mutable and permits null values. Url decodes all mutations except {@link Multimap#putAll(Multimap)}
    * 
    * @author Adrian Cole
    * 
    */
   static final class DecodingMultimap extends ForwardingMultimap<String, Object> {

      private static Multimap<String, Object> create() {
         return new DecodingMultimap();
      }

      private final Multimap<String, Object> delegate = LinkedHashMultimap.create();
      private final Function<Object, Object> urlDecoder = new Function<Object, Object>() {
         public Object apply(Object in) {
            return urlDecode(in);
         }
      };

      @Override
      public boolean put(String key, Object value) {
         return super.put(urlDecode(key), urlDecode(value));
      }

      @Override
      public boolean putAll(String key, Iterable<? extends Object> values) {
         return super.putAll(urlDecode(key), Iterables.transform(values, urlDecoder));
      }

      @Override
      public boolean putAll(Multimap<? extends String, ? extends Object> multimap) {
         return super.putAll(multimap);
      }

      @Override
      public Collection<Object> replaceValues(String key, Iterable<? extends Object> values) {
         return super.replaceValues(urlDecode(key), Iterables.transform(values, urlDecoder));
      }

      @Override
      protected Multimap<String, Object> delegate() {
         return delegate;
      }

   }

}
