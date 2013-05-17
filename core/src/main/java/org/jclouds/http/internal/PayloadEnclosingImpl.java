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
package org.jclouds.http.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.io.Payloads.newPayload;

import java.io.File;
import java.io.InputStream;

import org.jclouds.io.Payload;
import org.jclouds.io.PayloadEnclosing;
import org.jclouds.javax.annotation.Nullable;

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
      if (this.payload != null)
         payload.release();
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
      setPayload(newPayload(checkNotNull(data, "data")));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setPayload(File data) {
      setPayload(newPayload(checkNotNull(data, "data")));
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

}
