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
package org.jclouds.azureblob.functions;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.azureblob.domain.ContainerProperties;
import org.jclouds.azureblob.domain.MutableContainerPropertiesWithMetadata;
import org.jclouds.azureblob.domain.internal.MutableContainerPropertiesWithMetadataImpl;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.date.DateService;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

/**
 * This parses @{link {@link org.jclouds.azureblob.domain.ListableContainerProperties} from
 * HTTP headers.
 * 
 * 
 * @author Adrian Cole
 */
public class ParseContainerPropertiesFromHeaders implements Function<HttpResponse, ContainerProperties>,
      InvocationContext<ParseContainerPropertiesFromHeaders> {

   private final DateService dateParser;
   private final String metadataPrefix;
   private GeneratedHttpRequest request;

   @Inject
   public ParseContainerPropertiesFromHeaders(DateService dateParser,
         @Named(BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX) String metadataPrefix) {
      this.dateParser = dateParser;
      this.metadataPrefix = metadataPrefix;
   }

   public ContainerProperties apply(HttpResponse from) {
      MutableContainerPropertiesWithMetadata to = new MutableContainerPropertiesWithMetadataImpl();
      to.setName(request.getInvocation().getArgs().get(0).toString());
      addUserMetadataTo(from, to);
      parseLastModifiedOrThrowException(from, to);
      addETagTo(from, to);
      to.setUrl(request.getEndpoint());
      return to;
   }

   @VisibleForTesting
   void addUserMetadataTo(HttpResponse from, MutableContainerPropertiesWithMetadata metadata) {
      for (Entry<String, String> header : from.getHeaders().entries()) {
         if (header.getKey() != null && header.getKey().startsWith(metadataPrefix))
            metadata.getMetadata().put((header.getKey().substring(metadataPrefix.length())).toLowerCase(),
                  header.getValue());
      }
   }

   @VisibleForTesting
   void parseLastModifiedOrThrowException(HttpResponse from, MutableContainerPropertiesWithMetadata metadata)
         throws HttpException {
      String lastModified = from.getFirstHeaderOrNull(HttpHeaders.LAST_MODIFIED);
      if (lastModified == null)
         throw new HttpException(HttpHeaders.LAST_MODIFIED + " header not present in response: " + from);
      metadata.setLastModified(dateParser.rfc822DateParse(lastModified));
      if (metadata.getLastModified() == null)
         throw new HttpException("could not parse: " + HttpHeaders.LAST_MODIFIED + ": " + lastModified);
   }

   @VisibleForTesting
   protected void addETagTo(HttpResponse from, MutableContainerPropertiesWithMetadata metadata) {
      String eTag = from.getFirstHeaderOrNull(HttpHeaders.ETAG);
      if (metadata.getETag() == null && eTag != null) {
         metadata.setETag(eTag);
      }
   }

   @Override
   public ParseContainerPropertiesFromHeaders setContext(HttpRequest request) {
      checkArgument(request instanceof GeneratedHttpRequest, "note this handler requires a GeneratedHttpRequest");
      this.request = (GeneratedHttpRequest) request;
      return this;
   }

}
