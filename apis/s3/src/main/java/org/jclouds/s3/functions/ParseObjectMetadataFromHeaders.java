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
package org.jclouds.s3.functions;

import static com.google.common.io.BaseEncoding.base16;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.functions.ParseSystemAndUserMetadataFromHeaders;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.InvocationContext;
import org.jclouds.s3.blobstore.functions.BlobToObjectMetadata;
import org.jclouds.s3.domain.MutableObjectMetadata;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

/**
 * This parses @{link {@link org.jclouds.s3.domain.internal.MutableObjectMetadata} from HTTP
 * headers.
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/latest/RESTObjectGET.html" />
 * @author Adrian Cole
 */
public class ParseObjectMetadataFromHeaders implements Function<HttpResponse, MutableObjectMetadata>,
         InvocationContext<ParseObjectMetadataFromHeaders> {
   private final ParseSystemAndUserMetadataFromHeaders blobMetadataParser;
   private final BlobToObjectMetadata blobToObjectMetadata;
   private final String userMdPrefix;

   @Inject
   public ParseObjectMetadataFromHeaders(ParseSystemAndUserMetadataFromHeaders blobMetadataParser,
            BlobToObjectMetadata blobToObjectMetadata, @Named(PROPERTY_USER_METADATA_PREFIX) String userMdPrefix) {
      this.blobMetadataParser = blobMetadataParser;
      this.blobToObjectMetadata = blobToObjectMetadata;
      this.userMdPrefix = userMdPrefix;
   }

   // eTag pattern can be "a34d7e626b350d2e326196085dfa52f4-1", which is opaque and shouldn't be
   // used as content-md5, so filter etags that contain hyphens
   static final Pattern MD5_FROM_ETAG = Pattern.compile("^\"?([0-9a-f]+)\"?$");

   /**
    * parses the http response headers to create a new
    * {@link org.jclouds.s3.domain.internal.MutableObjectMetadata} object.
    */
   public MutableObjectMetadata apply(HttpResponse from) {
      BlobMetadata base = blobMetadataParser.apply(from);
      MutableObjectMetadata to = blobToObjectMetadata.apply(base);

      addETagTo(from, to);
      if (to.getContentMetadata().getContentMD5() == null && to.getETag() != null) {
         Matcher md5Matcher = MD5_FROM_ETAG.matcher(to.getETag());
         if (md5Matcher.find()) {
            byte[] md5 = base16().lowerCase().decode(md5Matcher.group(1));
            // it is possible others will look at the http payload directly
            if (from.getPayload() != null)
               from.getPayload().getContentMetadata().setContentMD5(md5);
            to.getContentMetadata().setContentMD5(md5);
         }
      }
      // amz has an etag, but matches syntax for usermetadata
      to.getUserMetadata().remove("object-etag");
      to.setCacheControl(from.getFirstHeaderOrNull(HttpHeaders.CACHE_CONTROL));
      return to;
   }

   /**
    * ETag == Content-MD5
    */
   @VisibleForTesting
   protected void addETagTo(HttpResponse from, MutableObjectMetadata metadata) {
      if (metadata.getETag() == null) {
         String eTagHeader = from.getFirstHeaderOrNull(userMdPrefix + "object-eTag");
         if (eTagHeader != null) {
            metadata.setETag(eTagHeader);
         }
      }
   }

   @Override
   public ParseObjectMetadataFromHeaders setContext(HttpRequest request) {
      blobMetadataParser.setContext(request);
      blobToObjectMetadata.setContext(request);
      return this;
   }

   public ParseObjectMetadataFromHeaders setKey(String key) {
      blobMetadataParser.setName(key);
      return this;
   }
}
