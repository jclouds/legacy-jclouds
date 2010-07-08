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
import static com.google.common.io.Closeables.closeQuietly;
import static org.jclouds.http.Payloads.newPayload;

import java.io.File;
import java.io.InputStream;

import javax.annotation.Nullable;

import org.jclouds.http.Payload;
import org.jclouds.http.PayloadEnclosing;

/**
 * 
 * @author Adrian Cole
 */
public class PayloadEnclosingImpl implements PayloadEnclosing {
   protected Payload payload;

   public PayloadEnclosingImpl() {
      this(null);
   }

   public PayloadEnclosingImpl(@Nullable Payload payload) {
      this.payload = payload;
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
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setPayload(InputStream data) {
      setPayload(newPayload(checkNotNull(data, "data")));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setPayload(byte[] data) {
      setPayload(newPayload(checkNotNull(data, "data")));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setPayload(String data) {
      closeContentIfPresent();
      setPayload(newPayload(checkNotNull(data, "data")));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setPayload(File data) {
      setPayload(newPayload(checkNotNull(data, "data")));
   }

   private void closeContentIfPresent() {
      if (payload != null && payload.getInput() != null) {
         closeQuietly(payload.getInput());
      }
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
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
      PayloadEnclosingImpl other = (PayloadEnclosingImpl) obj;
      if (payload == null) {
         if (other.payload != null)
            return false;
      } else if (!payload.equals(other.payload))
         return false;
      return true;
   }

   @Override
   protected void finalize() throws Throwable {
      closeContentIfPresent();
      super.finalize();
   }
}
