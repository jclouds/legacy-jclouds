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
package org.jclouds.blobstore.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;
import static org.jclouds.blobstore.util.BlobStoreUtils.getNameFor;

import java.net.URI;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.date.DateService;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
public class ParseSystemAndUserMetadataFromHeaders implements Function<HttpResponse, MutableBlobMetadata>,
         InvocationContext<ParseSystemAndUserMetadataFromHeaders> {
   private final String metadataPrefix;
   private final DateService dateParser;
   private final Provider<MutableBlobMetadata> metadataFactory;

   private String name;
   private URI endpoint;

   @Inject
   public ParseSystemAndUserMetadataFromHeaders(Provider<MutableBlobMetadata> metadataFactory, DateService dateParser,
            @Named(PROPERTY_USER_METADATA_PREFIX) String metadataPrefix) {
      this.metadataFactory = checkNotNull(metadataFactory, "metadataFactory");
      this.dateParser = checkNotNull(dateParser, "dateParser");
      this.metadataPrefix = checkNotNull(metadataPrefix, "metadataPrefix");
   }

   public MutableBlobMetadata apply(HttpResponse from) {
      checkNotNull(from, "request");
      checkState(name != null, "name must be initialized by now");

      MutableBlobMetadata to = metadataFactory.get();
      to.setName(name);
      to.setUri(endpoint);
      if (from.getPayload() != null)
         HttpUtils.copy(from.getPayload().getContentMetadata(), to.getContentMetadata());
      addETagTo(from, to);
      parseLastModifiedOrThrowException(from, to);
      addUserMetadataTo(from, to);
      return to;
   }

   @VisibleForTesting
   void addUserMetadataTo(HttpResponse from, MutableBlobMetadata metadata) {
      for (Entry<String, String> header : from.getHeaders().entries()) {
         if (header.getKey() != null && header.getKey().startsWith(metadataPrefix))
            metadata.getUserMetadata().put((header.getKey().substring(metadataPrefix.length())).toLowerCase(),
                     header.getValue());
      }
   }

   @VisibleForTesting
   void parseLastModifiedOrThrowException(HttpResponse from, MutableBlobMetadata metadata) throws HttpException {
      String lastModified = from.getFirstHeaderOrNull(HttpHeaders.LAST_MODIFIED);
      if (lastModified == null) {
         // scaleup-storage uses the wrong case for the last modified header
         if ((lastModified = from.getFirstHeaderOrNull("Last-modified")) == null)
            throw new HttpException(HttpHeaders.LAST_MODIFIED + " header not present in response: " + from);
      }

      // Walrus
      if (lastModified.startsWith("20")) {
         metadata.setLastModified(dateParser.iso8601DateParse(lastModified.replace("+0000", "Z")));
      } else {
         metadata.setLastModified(dateParser.rfc822DateParse(lastModified));
      }

      if (metadata.getLastModified() == null)
         throw new HttpException("could not parse: " + HttpHeaders.LAST_MODIFIED + ": " + lastModified);
   }

   protected void addETagTo(HttpResponse from, MutableBlobMetadata metadata) {
      String eTag = from.getFirstHeaderOrNull(HttpHeaders.ETAG);
      if (metadata.getETag() == null && eTag != null) {
         metadata.setETag(eTag);
      }
   }

   public ParseSystemAndUserMetadataFromHeaders setContext(HttpRequest request) {
      this.endpoint = request.getEndpoint();
      checkArgument(request instanceof GeneratedHttpRequest, "note this handler requires a GeneratedHttpRequest");
      return setName(getNameFor(GeneratedHttpRequest.class.cast(request)));
   }

   public ParseSystemAndUserMetadataFromHeaders setName(String name) {
      this.name = checkNotNull(name, "name");
      return this;
   }
}
