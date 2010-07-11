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
package org.jclouds.aws.s3.binders;

import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_SERVICE_PATH;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_VIRTUAL_HOST_BUCKETS;
import static org.jclouds.http.HttpUtils.urlEncode;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.aws.s3.S3AsyncClient;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.binders.BindAsHostPrefix;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindAsHostPrefixIfConfigured implements Binder {

   private final Provider<UriBuilder> uriBuilderProvider;
   private final BindAsHostPrefix bindAsHostPrefix;
   private final boolean isVhostStyle;
   private final String servicePath;

   @Inject
   public BindAsHostPrefixIfConfigured(BindAsHostPrefix bindAsHostPrefix,
            @Named(PROPERTY_S3_VIRTUAL_HOST_BUCKETS) boolean isVhostStyle,
            @Named(PROPERTY_S3_SERVICE_PATH) String servicePath,
            Provider<UriBuilder> uriBuilderProvider) {
      this.bindAsHostPrefix = bindAsHostPrefix;
      this.isVhostStyle = isVhostStyle;
      this.servicePath = servicePath;
      this.uriBuilderProvider = uriBuilderProvider;
   }

   public void bindToRequest(HttpRequest request, Object payload) {
      if (isVhostStyle) {
         bindAsHostPrefix.bindToRequest(request, payload);
         request.getHeaders().replaceValues(HttpHeaders.HOST,
                  ImmutableSet.of(request.getEndpoint().getHost()));
      } else {
         UriBuilder builder = uriBuilderProvider.get().uri(request.getEndpoint());
         StringBuilder path = new StringBuilder(urlEncode(request.getEndpoint().getPath(),
                  S3AsyncClient.class.getAnnotation(SkipEncoding.class).value()));
         int indexToInsert = path.indexOf(servicePath);
         indexToInsert = indexToInsert == -1 ? 0 : indexToInsert;
         indexToInsert += servicePath.length();
         path.insert(indexToInsert, "/" + payload.toString());
         builder.replacePath(path.toString());
         request.setEndpoint(builder.buildFromEncodedMap(Maps.<String, Object> newHashMap()));
      }
   }
}
