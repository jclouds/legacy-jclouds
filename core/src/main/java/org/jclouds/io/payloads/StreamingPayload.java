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

package org.jclouds.io.payloads;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Nullable;

import org.jclouds.io.Payload;
import org.jclouds.io.WriteTo;

/**
 * Note that not all services accept streaming payloads. For example, Rackspace
 * CloudFiles accepts streaming while Amazon S3 does not.
 * 
 * @author Adrian Cole
 */
public class StreamingPayload implements Payload {
   protected String contentType;
   protected transient volatile boolean written;
   protected final WriteTo writeTo;

   public StreamingPayload(WriteTo writeTo) {
      this.writeTo = checkNotNull(writeTo, "writeTo");
      this.contentType = "application/unknown";
   }

   /**
    * @throws UnsupportedOperationException
    *            this payload is for streaming writes only
    */
   @Override
   public Object getRawContent() {
      throw new UnsupportedOperationException("this payload is for streaming writes only");
   }

   /**
    * @throws UnsupportedOperationException
    *            this payload is for streaming writes only
    */
   @Override
   public InputStream getInput() {
      throw new UnsupportedOperationException("this payload is for streaming writes only");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Long getContentLength() {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setContentLength(@Nullable Long contentLength) {
      throw new UnsupportedOperationException("this payload is for streaming writes only");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public byte[] getContentMD5() {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setContentMD5(byte[] md5) {
      throw new UnsupportedOperationException("this payload is for streaming writes only");
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
      writeTo.writeTo(outstream);
   }

   @Override
   public String toString() {
      return "[contentType=" + contentType + ", written=" + written + "]";
   }

   /**
    * By default we are not repeatable.
    */
   @Override
   public boolean isRepeatable() {
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      StreamingPayload other = (StreamingPayload) obj;
      if (contentType == null) {
         if (other.contentType != null)
            return false;
      } else if (!contentType.equals(other.contentType))
         return false;
      return true;
   }

   /**
    * By default there are no resources to release.
    */
   @Override
   public void release() {
   }

   /**
    * Delegates to release()
    */
   @Override
   public void close() {
      release();
   }
}