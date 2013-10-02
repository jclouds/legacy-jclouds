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
package org.jclouds.io.payloads;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.io.WriteTo;

/**
 * Note that not all services accept streaming payloads. For example, Rackspace CloudFiles accepts
 * streaming while Amazon S3 does not.
 * 
 * @author Adrian Cole
 */
public class StreamingPayload implements Payload {

   protected transient volatile boolean written;
   protected final WriteTo writeTo;
   protected MutableContentMetadata contentMetadata;

   public StreamingPayload(WriteTo writeTo) {
      this(writeTo, new BaseMutableContentMetadata());
   }

   protected StreamingPayload(WriteTo writeTo, MutableContentMetadata contentMetadata) {
      this.writeTo = checkNotNull(writeTo, "writeTo");
      this.contentMetadata = checkNotNull(contentMetadata, "contentMetadata");
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
   public void writeTo(OutputStream outstream) throws IOException {
      writeTo.writeTo(outstream);
   }

   @Override
   public String toString() {
      return "[contentMetadata=" + contentMetadata + ", written=" + written + "]";
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
      result = prime * result + ((contentMetadata == null) ? 0 : contentMetadata.hashCode());
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
      if (contentMetadata == null) {
         if (other.contentMetadata != null)
            return false;
      } else if (!contentMetadata.equals(other.contentMetadata))
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

   /**
    * {@inheritDoc}
    */
   @Override
   public MutableContentMetadata getContentMetadata() {
      return contentMetadata;
   }

   @Override
   public void setContentMetadata(MutableContentMetadata in) {
      contentMetadata = in;
   }
}
