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
package org.jclouds.io;

import static com.google.common.collect.Iterables.any;
import static com.google.common.io.BaseEncoding.base64;
import static com.google.common.net.HttpHeaders.CONTENT_DISPOSITION;
import static com.google.common.net.HttpHeaders.CONTENT_ENCODING;
import static com.google.common.net.HttpHeaders.CONTENT_LANGUAGE;
import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;
import static com.google.common.net.HttpHeaders.CONTENT_MD5;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.net.HttpHeaders.EXPIRES;

import java.util.Date;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.jclouds.date.DateCodec;
import org.jclouds.date.DateCodecFactory;
import org.jclouds.io.ContentMetadataCodec.DefaultContentMetadataCodec;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;

@ImplementedBy(DefaultContentMetadataCodec.class)
public interface ContentMetadataCodec {

   /**
    * Generates standard HTTP headers for the give metadata.
    */
   public Multimap<String, String> toHeaders(ContentMetadata md);

   /**
    * Sets properties related to the http headers listed in {@link ContentMetadata#HTTP_HEADERS}
    */
   public void fromHeaders(MutableContentMetadata contentMetadata, Multimap<String, String> headers);

   /**
    * Parses the 'Expires' header.
    * If invalid, returns a date in the past (in accordance with HTTP 1.1 client spec).
    */
   public Date parseExpires(String expires);

   /**
    * Default implementation, in accordance with HTTP 1.1 spec.
    * 
    * @author aled
    */
   public static class DefaultContentMetadataCodec implements ContentMetadataCodec {
      
      @Resource
      protected Logger logger = Logger.NULL;

      private final DateCodec httpExpiresDateCodec;

      @Inject
      public DefaultContentMetadataCodec(DateCodecFactory dateCodecs) {
         httpExpiresDateCodec = dateCodecs.rfc1123();
      }
      
      protected DateCodec getExpiresDateCodec() {
         return httpExpiresDateCodec;
      }
      
      @Override
      public Multimap<String, String> toHeaders(ContentMetadata md) {
         Builder<String, String> builder = ImmutableMultimap.builder();
         if (md.getContentType() != null)
            builder.put(CONTENT_TYPE, md.getContentType());
         if (md.getContentDisposition() != null)
            builder.put(CONTENT_DISPOSITION, md.getContentDisposition());
         if (md.getContentEncoding() != null)
            builder.put(CONTENT_ENCODING, md.getContentEncoding());
         if (md.getContentLanguage() != null)
            builder.put(CONTENT_LANGUAGE, md.getContentLanguage());
         if (md.getContentLength() != null)
            builder.put(CONTENT_LENGTH, md.getContentLength() + "");
         if (md.getContentMD5() != null)
            builder.put(CONTENT_MD5, base64().encode(md.getContentMD5()));
         if (md.getExpires() != null)
            builder.put(EXPIRES, getExpiresDateCodec().toString(md.getExpires()));
         return builder.build();
      }
      
      @Override
      public void fromHeaders(MutableContentMetadata contentMetadata, Multimap<String, String> headers) {
         boolean chunked = any(headers.entries(), new Predicate<Entry<String, String>>() {
            @Override
            public boolean apply(Entry<String, String> input) {
               return "Transfer-Encoding".equalsIgnoreCase(input.getKey()) && "chunked".equalsIgnoreCase(input.getValue());
            }
         });
         for (Entry<String, String> header : headers.entries()) {
            if (!chunked && CONTENT_LENGTH.equalsIgnoreCase(header.getKey())) {
               contentMetadata.setContentLength(Long.valueOf(header.getValue()));
            } else if (CONTENT_MD5.equalsIgnoreCase(header.getKey())) {
               contentMetadata.setContentMD5(base64().decode(header.getValue()));
            } else if (CONTENT_TYPE.equalsIgnoreCase(header.getKey())) {
               contentMetadata.setContentType(header.getValue());
            } else if (CONTENT_DISPOSITION.equalsIgnoreCase(header.getKey())) {
               contentMetadata.setContentDisposition(header.getValue());
            } else if (CONTENT_ENCODING.equalsIgnoreCase(header.getKey())) {
               contentMetadata.setContentEncoding(header.getValue());
            } else if (CONTENT_LANGUAGE.equalsIgnoreCase(header.getKey())) {
               contentMetadata.setContentLanguage(header.getValue());
            } else if (EXPIRES.equalsIgnoreCase(header.getKey())) {
               contentMetadata.setExpires(parseExpires(header.getValue()));
            }
         }
      }
      
      public Date parseExpires(String expires) {
         try {
            return (expires != null) ? getExpiresDateCodec().toDate(expires) : null;
         } catch (IllegalArgumentException e) {
            logger.debug("Invalid Expires header (%s); should be in RFC-1123 format; treating as already expired: %s",
                  expires, e.getMessage());
            return new Date(0);
         }
      }
   }
}
