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
package org.jclouds.http.payloads;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.io.ByteStreams.copy;
import static com.google.common.io.Closeables.closeQuietly;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Nullable;

import org.jclouds.http.Payload;

/**
 * @author Adrian Cole
 */
public abstract class BasePayload<V> implements Payload {
   protected final V content;
   protected String contentType;
   protected Long contentLength;
   protected byte[] contentMD5;
   protected transient volatile boolean written;

   protected BasePayload(V content, @Nullable String contentType, @Nullable Long contentLength,
            @Nullable byte[] contentMD5) {
      this.content = checkNotNull(content, "content");
      this.contentType = contentType == null ? "application/unknown" : contentType;
      this.contentLength = contentLength;
      if (contentMD5 != null)
         setContentMD5(contentMD5);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public V getRawContent() {
      return content;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Long getContentLength() {
      return contentLength;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setContentLength(@Nullable Long contentLength) {
      this.contentLength = contentLength;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public byte[] getContentMD5() {
      if (contentMD5 != null) {
         byte[] retval = new byte[contentMD5.length];
         System.arraycopy(this.contentMD5, 0, retval, 0, contentMD5.length);
         return retval;
      } else {
         return null;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setContentMD5(byte[] md5) {
      if (md5 != null) {
         byte[] retval = new byte[md5.length];
         System.arraycopy(md5, 0, retval, 0, md5.length);
         this.contentMD5 = md5;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getContentType() {
      return contentType;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setContentType(@Nullable String contentType) {
      this.contentType = contentType;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void writeTo(OutputStream outstream) throws IOException {
      checkState(!written || isRepeatable(), "can only be writted to an outputstream once");
      written = true;
      InputStream in = getInput();
      try {
         copy(in, outstream);
      } finally {
         closeQuietly(in);
      }
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((content == null) ? 0 : content.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (!(obj instanceof Payload))
         return false;
      Payload other = (Payload) obj;
      if (content == null) {
         if (other.getRawContent() != null)
            return false;
      } else if (!content.equals(other.getRawContent()))
         return false;
      return true;
   }

}