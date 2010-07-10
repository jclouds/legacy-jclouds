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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Nullable;

import com.google.common.io.InputSupplier;

/**
 * @author Adrian Cole
 */
public interface Payload extends InputSupplier<InputStream> {

   /**
    * Creates a new InputStream object of the payload.
    */
   InputStream getInput();

   /**
    * Payload in its original form.
    */
   Object getRawContent();

   /**
    * Tells if the payload is capable of producing its data more than once.
    */
   boolean isRepeatable();

   /**
    * Writes the payload content to the output stream.
    * 
    * @throws IOException
    */
   void writeTo(OutputStream outstream) throws IOException;

   void setContentLength(@Nullable Long contentLength);

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

   void setContentMD5(@Nullable byte[] md5);

   @Nullable
   byte[] getContentMD5();

   void setContentType(@Nullable String md5);

   @Nullable
   String getContentType();

   /**
    * release resources used by this entity. This should be called when data is discarded.
    */
   void release();
}