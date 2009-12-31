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
    * generate an MD5 Hash for the current data.
    * <p/>
    * <h2>Note</h2>
    * <p/>
    * If this is an InputStream, it will be converted to a byte array first.
    * 
    */
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
    * @return InputStream, if downloading, or whatever was set during {@link #setPayload(Object)}
    */
   public InputStream getContent() {
      checkState(payload != null, "payload");
      return payload.getContent();
   }

   @Override
   public Payload getPayload() {
      return payload;
   }

   /**
    * {@inheritDoc}
    */
   public void setPayload(Payload data) {
      this.payload = checkNotNull(data, "data");
      setLength();
   }

   /**
    * {@inheritDoc}
    */
   public void setPayload(InputStream data) {
      setPayload(Payloads.newPayload(checkNotNull(data, "data")));
   }

   /**
    * {@inheritDoc}
    */
   public void setPayload(byte[] data) {
      setPayload(Payloads.newPayload(checkNotNull(data, "data")));
   }

   /**
    * {@inheritDoc}
    */
   public void setPayload(String data) {
      setPayload(Payloads.newPayload(checkNotNull(data, "data")));
   }

   /**
    * {@inheritDoc}
    */
   public void setPayload(File data) {
      setPayload(Payloads.newPayload(checkNotNull(data, "data")));
   }

   private void setLength() {
      if (getContentLength() == null) {
         Long size = payload.calculateSize();
         if (size != null)
            this.setContentLength(size);
      }
   }

   /**
    * Returns the total size of the downloaded object, or the chunk that's available.
    * <p/>
    * Chunking is only used when org.jclouds.http.GetOptions is called with options like tail,
    * range, or startAt.
    * 
    * @return the length in bytes that can be be obtained from {@link #getContent()}
    * @see org.jclouds.http.HttpHeaders#CONTENT_LENGTH
    * @see GetObjectOptions
    */
   public Long getContentLength() {
      return contentLength;
   }

   public void setContentLength(long contentLength) {
      this.contentLength = contentLength;
   }

}
