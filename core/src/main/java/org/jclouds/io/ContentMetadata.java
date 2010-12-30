/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.io;

import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;

import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
public interface ContentMetadata {
   public static final Set<String> HTTP_HEADERS = ImmutableSet.of(CONTENT_LENGTH, "Content-MD5", CONTENT_TYPE,
         "Content-Disposition", "Content-Encoding", "Content-Language");

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

}