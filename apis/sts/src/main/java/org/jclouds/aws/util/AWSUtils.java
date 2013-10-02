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
package org.jclouds.aws.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.xml.ErrorHandler;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RequestSigner;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * Needed to sign and verify requests and responses.
 * 
 * @author Adrian Cole
 */
@Singleton
@SuppressWarnings("unchecked")
public class AWSUtils {
   @Singleton
   public static class GetRegionFromLocation implements Function<Location, String> {
      public String apply(Location location) {
         String region = location.getScope() == LocationScope.REGION ? location.getId() : location.getParent().getId();
         return region;
      }
   }

   private final RequestSigner signer;
   private final ParseSax.Factory factory;
   private final Provider<ErrorHandler> errorHandlerProvider;
   private final String requestId;
   private final String requestToken;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   AWSUtils(@Named(PROPERTY_HEADER_TAG) String headerTag, RequestSigner signer, Factory factory,
         Provider<ErrorHandler> errorHandlerProvider) {
      this.signer = signer;
      this.factory = factory;
      this.errorHandlerProvider = errorHandlerProvider;
      this.requestId = String.format("x-%s-request-id", headerTag);
      this.requestToken = String.format("x-%s-id-2", headerTag);
   }

   public AWSError parseAWSErrorFromContent(HttpRequest request, HttpResponse response) {
      if (response.getPayload() == null)
         return null;
      if ("text/plain".equals(response.getPayload().getContentMetadata().getContentType()))
         return null;
      try {
         AWSError error = factory.create(errorHandlerProvider.get()).setContext(request).apply(response);
         if (error.getRequestId() == null)
            error.setRequestId(response.getFirstHeaderOrNull(requestId));
         error.setRequestToken(response.getFirstHeaderOrNull(requestToken));
         if ("SignatureDoesNotMatch".equals(error.getCode())) {
            error.setStringSigned(signer.createStringToSign(request));
            error.setSignature(signer.sign(error.getStringSigned()));
         }
         return error;
      } catch (RuntimeException e) {
         logger.warn(e, "error parsing error");
         return null;
      }
   }

   public static <R extends HttpRequest> R indexStringArrayToFormValuesWithStringFormat(R request, String format,
         Object input) {
      checkArgument(checkNotNull(input, "input") instanceof String[], "this binder is only valid for String[] : "
            + input.getClass());
      String[] values = (String[]) input;
      Builder<String, String> builder = ImmutableMultimap.builder();
      for (int i = 0; i < values.length; i++) {
         builder.put(String.format(format, i + 1), checkNotNull(values[i], format.toLowerCase() + "s[" + i + "]"));
      }
      ImmutableMultimap<String, String> forms = builder.build();
      return forms.size() == 0 ? request : (R) request.toBuilder().replaceFormParams(forms).build();
   }

   // TODO: make this more dynamic
   public static boolean isRegion(String regionName) {
      return Region.DEFAULT_REGIONS.contains(regionName);
   }

   public static <R extends HttpRequest> R indexIterableToFormValuesWithPrefix(R request, String prefix, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Iterable<?>, "this binder is only valid for Iterable<?>: "
            + input.getClass());
      Iterable<?> values = (Iterable<?>) input;
      Builder<String, String> builder = ImmutableMultimap.builder();
      int i = 0;
      for (Object o : values) {
         builder.put(prefix + "." + (i++ + 1), checkNotNull(o.toString(), prefix.toLowerCase() + "s[" + i + "]"));
      }
      ImmutableMultimap<String, String> forms = builder.build();
      return forms.size() == 0 ? request : (R) request.toBuilder().replaceFormParams(forms).build();
   }

   public static <R extends HttpRequest> R indexStringArrayToFormValuesWithPrefix(R request, String prefix, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof String[], "this binder is only valid for String[] : "
            + input.getClass());
      String[] values = (String[]) input;
      Builder<String, String> builder = ImmutableMultimap.builder();
      for (int i = 0; i < values.length; i++) {
         builder.put(prefix + "." + (i + 1), checkNotNull(values[i], prefix.toLowerCase() + "s[" + i + "]"));
      }
      ImmutableMultimap<String, String> forms = builder.build();
      return forms.size() == 0 ? request : (R) request.toBuilder().replaceFormParams(forms).build();
   }

   public static <R extends HttpRequest> R indexMapToFormValuesWithPrefix(R request, String prefix, String keySuffix, String valueSuffix, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Map<?, ?>, "this binder is only valid for Map<?,?>: " + input.getClass());
      Map<?, ?> map = (Map<?, ?>) input;
      Builder<String, String> builder = ImmutableMultimap.builder();
      int i = 1;
      for (Map.Entry<?, ?> e : map.entrySet()) {
         builder.put(prefix + "." + i + "." + keySuffix, checkNotNull(e.getKey().toString(), keySuffix.toLowerCase() + "s[" + i + "]"));
         if (e.getValue() != null) {
            builder.put(prefix + "." + i + "." + valueSuffix, e.getValue().toString());
         }
         i++;
      }
      ImmutableMultimap<String, String> forms = builder.build();
      return forms.size() == 0 ? request : (R) request.toBuilder().replaceFormParams(forms).build();
   }

   public static <R extends HttpRequest> R indexMultimapToFormValuesWithPrefix(R request, String prefix, String keySuffix, String valueSuffix, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Multimap<?, ?>, "this binder is only valid for Multimap<?,?>: " + input.getClass());
      Multimap<Object, Object> map = (Multimap<Object, Object>) input;
      Builder<String, String> builder = ImmutableMultimap.builder();
      int i = 1;
      for (Map.Entry<Object, Collection<Object>> entry : map.asMap().entrySet()) {
         builder.put(prefix + "." + i + "." + keySuffix, checkNotNull(entry.getKey().toString(), keySuffix.toLowerCase() + "s[" + i + "]"));
         int j = 1;
         for (Object v : entry.getValue()) {
            builder.put(prefix + "." + i + "." + valueSuffix + "." + j, v.toString());
            j++;
         }
         i++;
      }
      ImmutableMultimap<String, String> forms = builder.build();
      return forms.size() == 0 ? request : (R) request.toBuilder().replaceFormParams(forms).build();
   }

   public static <R extends HttpRequest> R indexMapOfIterableToFormValuesWithPrefix(R request, String prefix, String keySuffix, String valueSuffix, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Map<?, ?>, "this binder is only valid for Map<?,Iterable<?>>: " + input.getClass());
      Map<Object, Iterable<Object>> map = (Map<Object, Iterable<Object>>) input;
      Builder<String, String> builder = ImmutableMultimap.builder();
      int i = 1;
      for (Map.Entry<Object, Iterable<Object>> entry : map.entrySet()) {
         builder.put(prefix + "." + i + "." + keySuffix, checkNotNull(entry.getKey().toString(), keySuffix.toLowerCase() + "s[" + i + "]"));
         Iterable<Object> iterable = entry.getValue();
         if (!Iterables.isEmpty(iterable)) {
            int j = 1;
            for (Object v : iterable) {
               builder.put(prefix + "." + i + "." + valueSuffix + "." + j, v.toString());
               j++;
            }
         }
         i++;
      }
      ImmutableMultimap<String, String> forms = builder.build();
      return forms.size() == 0 ? request : (R) request.toBuilder().replaceFormParams(forms).build();
   }

   public static String getRegionFromLocationOrNull(Location location) {
      return location.getScope() == LocationScope.ZONE ? location.getParent().getId() : location.getId();
   }

   // there may not be a region, and in this case we do-not encode it into the string
   public static String[] parseHandle(String id) {
      String[] parts = checkNotNull(id, "id").split("/");
      return (parts.length == 1) ? new String[] { null, id } : parts;
   }

   public static String findRegionInArgsOrNull(GeneratedHttpRequest gRequest) {
      for (Object arg : gRequest.getInvocation().getArgs()) {
         if (arg instanceof String) {
            String regionName = (String) arg;
            // TODO regions may not be amazon regions!
            // take from a configured value
            if (isRegion(regionName))
               return regionName;
         }
      }
      return null;
   }
}
