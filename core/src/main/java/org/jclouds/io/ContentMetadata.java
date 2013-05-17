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
import static com.google.common.net.HttpHeaders.CONTENT_DISPOSITION;
import static com.google.common.net.HttpHeaders.CONTENT_ENCODING;
import static com.google.common.net.HttpHeaders.CONTENT_LANGUAGE;
import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;
import static com.google.common.net.HttpHeaders.CONTENT_MD5;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.net.HttpHeaders.EXPIRES;

import java.util.Date;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
public interface ContentMetadata {
   public static final Set<String> HTTP_HEADERS = ImmutableSet.of(CONTENT_LENGTH, CONTENT_MD5, CONTENT_TYPE,
            CONTENT_DISPOSITION, CONTENT_ENCODING, CONTENT_LANGUAGE, EXPIRES);

   // See http://stackoverflow.com/questions/10584647/simpledateformat-parse-is-one-hour-out-using-rfc-1123-gmt-in-summer
   // for why not using "zzz"
   public static final String RFC1123_DATE_PATTERN = "EEE, dd MMM yyyyy HH:mm:ss Z";
   
   /**
    * Returns the total size of the payload, or the chunk that's available.
    * <p/>
    * Chunking is only used when {@link org.jclouds.http.GetOptions} is called with options like
    * tail, range, or startAt.
    * 
    * @return the length in bytes that can be be obtained from {@link #getInput()}
    * @see javax.ws.rs.core.HttpHeaders#CONTENT_LENGTH
    * @see org.jclouds.http.options.GetOptions
    */
   @Nullable
   Long getContentLength();

   /**
    * Specifies presentational information for the object.
    * 
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec19.html?sec19.5.1."/>
    */
   @Nullable
   String getContentDisposition();

   /**
    * Specifies what content encodings have been applied to the object and thus what decoding
    * mechanisms must be applied in order to obtain the media-type referenced by the Content-Type
    * header field.
    * 
    * @see <a href= "http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html?sec14.11" />
    */
   @Nullable
   String getContentEncoding();

   /**
    * 
    * A standard MIME type describing the format of the contents. If none is provided, the default
    * is binary/octet-stream.
    * 
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.17"/>
    */
   @Nullable
   String getContentType();

   @Nullable
   byte[] getContentMD5();

   /**
    * Get Content Language of the payload
    * <p/>
    * Not all providers may support it
    */
   @Nullable
   String getContentLanguage();

   /**
    * Gives the date/time after which the response is considered stale.
    * 
    * @throws IllegalStateException If the Expires header is non-null, and not a valid RFC 1123 date
    * 
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.21"/>
    */
   @Nullable
   Date getExpires();

  ContentMetadataBuilder toBuilder();

}
