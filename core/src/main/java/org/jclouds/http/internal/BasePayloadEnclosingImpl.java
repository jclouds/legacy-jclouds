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
package org.jclouds.http.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.InputStream;

import javax.inject.Inject;

import org.jclouds.encryption.EncryptionService;
import org.jclouds.encryption.EncryptionService.MD5InputStreamResult;
import org.jclouds.http.Payload;
import org.jclouds.http.PayloadEnclosing;
import org.jclouds.http.Payloads;
import org.jclouds.http.payloads.InputStreamPayload;

import com.google.common.io.Closeables;

/**
 * 
 * @author Adrian Cole
 */
public abstract class BasePayloadEnclosingImpl implements PayloadEnclosing {
   private final EncryptionService encryptionService;
   protected Payload payload;
   protected Long contentLength;

   @Inject
   public BasePayloadEnclosingImpl(EncryptionService encryptionService) {
      this.encryptionService = encryptionService;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void generateMD5() {
      checkState(payload != null, "payload");
      if (payload instanceof InputStreamPayload) {
         MD5InputStreamResult result = encryptionService
                  .generateMD5Result(((InputStreamPayload) payload).getContent());
         setContentMD5(result.md5);
         setContentLength(result.length);
         setPayload(result.data);
      } else {
         setContentMD5(encryptionService.md5(payload.getRawContent()));
      }
   }

   protected abstract void setContentMD5(byte[] md5);

   /**
    * {@inheritDoc}
    */
   @Override
   public InputStream getContent() {
      return payload != null ? payload.getContent() : null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Payload getPayload() {
      return payload;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setPayload(Payload data) {
      closeContentIfPresent();
      this.payload = checkNotNull(data, "data");
      setLength();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setPayload(InputStream data) {
      setPayload(Payloads.newPayload(checkNotNull(data, "data")));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setPayload(byte[] data) {
      setPayload(Payloads.newPayload(checkNotNull(data, "data")));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setPayload(String data) {
      setPayload(Payloads.newPayload(checkNotNull(data, "data")));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setPayload(File data) {
      setPayload(Payloads.newPayload(checkNotNull(data, "data")));
   }

   private void setLength() {
      Long size = payload.calculateSize();
      if (size != null)
         this.setContentLength(size);
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
   public void setContentLength(long contentLength) {
      this.contentLength = contentLength;
   }

   @Override
   protected void finalize() throws Throwable {
      closeContentIfPresent();
      super.finalize();
   }

   private void closeContentIfPresent() {
      if (getContent() != null) {
         Closeables.closeQuietly(getContent());
      }
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((contentLength == null) ? 0 : contentLength.hashCode());
      result = prime * result + ((encryptionService == null) ? 0 : encryptionService.hashCode());
      result = prime * result + ((payload == null) ? 0 : payload.hashCode());
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
      BasePayloadEnclosingImpl other = (BasePayloadEnclosingImpl) obj;
      if (contentLength == null) {
         if (other.contentLength != null)
            return false;
      } else if (!contentLength.equals(other.contentLength))
         return false;
      if (encryptionService == null) {
         if (other.encryptionService != null)
            return false;
      } else if (!encryptionService.equals(other.encryptionService))
         return false;
      if (payload == null) {
         if (other.payload != null)
            return false;
      } else if (!payload.equals(other.payload))
         return false;
      return true;
   }

}
