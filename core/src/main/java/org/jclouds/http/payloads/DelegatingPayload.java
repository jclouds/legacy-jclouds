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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jclouds.http.Payload;

/**
 * 
 * @author Adrian Cole
 */
public class DelegatingPayload implements Payload {

   private final Payload delegate;

   public DelegatingPayload(Payload delegate) {
      this.delegate = checkNotNull(delegate, "delegate");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public InputStream getInput() {
      return delegate.getInput();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object getRawContent() {
      return delegate.getInput();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isRepeatable() {
      return delegate.isRepeatable();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void writeTo(OutputStream outstream) throws IOException {
      delegate.writeTo(outstream);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Long getContentLength() {
      return delegate.getContentLength();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public byte[] getContentMD5() {
      return delegate.getContentMD5();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setContentLength(Long contentLength) {
      delegate.setContentLength(contentLength);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setContentMD5(byte[] md5) {
      delegate.setContentMD5(md5);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getContentType() {
      return delegate.getContentType();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setContentType(String md5) {
      delegate.setContentType(md5);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return delegate.hashCode();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      return delegate.equals(obj);

   }

   public Payload getDelegate() {
      return delegate;
   }

   @Override
   public void release() {
      delegate.release();
   }
}