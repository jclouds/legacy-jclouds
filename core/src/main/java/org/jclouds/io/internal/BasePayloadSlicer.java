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
package org.jclouds.io.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

import javax.inject.Singleton;

import org.jclouds.io.Payload;
import org.jclouds.io.PayloadSlicer;
import org.jclouds.io.payloads.BaseMutableContentMetadata;
import org.jclouds.io.payloads.InputStreamPayload;
import org.jclouds.io.payloads.InputStreamSupplierPayload;

import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BasePayloadSlicer implements PayloadSlicer {
   /**
    * {@inheritDoc}
    */
   @Override
   public Payload slice(Payload input, long offset, long length) {
      checkNotNull(input);
      checkArgument(offset >= 0, "offset is negative");
      checkArgument(length >= 0, "length is negative");
      Payload returnVal;
      if (input.getRawContent() instanceof File) {
         returnVal = doSlice((File) input.getRawContent(), offset, length);
      } else if (input.getRawContent() instanceof String) {
         returnVal = doSlice((String) input.getRawContent(), offset, length);
      } else if (input.getRawContent() instanceof byte[]) {
         returnVal = doSlice((byte[]) input.getRawContent(), offset, length);
      } else if (input.getRawContent() instanceof InputStream) {
         returnVal = doSlice((InputStream) input.getRawContent(), offset, length);
      } else {
         returnVal = doSlice(input, offset, length);
      }
      return copyMetadataAndSetLength(input, returnVal, length);
   }

   protected Payload doSlice(Payload content, long offset, long length) {
      return doSlice((InputSupplier<? extends InputStream>) content, offset, length);
   }

   protected Payload doSlice(String content, long offset, long length) {
      return doSlice(content.getBytes(), offset, length);
   }

   protected Payload doSlice(File content, long offset, long length) {
      return doSlice(Files.newInputStreamSupplier(content), offset, length);
   }

   protected Payload doSlice(InputStream content, long offset, long length) {
      try {
         ByteStreams.skipFully(content, offset);
      } catch (IOException ioe) {
         throw Throwables.propagate(ioe);
      }
      return new InputStreamPayload(ByteStreams.limit(content, length));
   }

   protected Payload doSlice(InputSupplier<? extends InputStream> content, long offset, long length) {
      return new InputStreamSupplierPayload(ByteStreams.slice(content, offset, length));
   }

   protected Payload doSlice(byte[] content, long offset, long length) {
      Payload returnVal;
      checkArgument(offset <= Integer.MAX_VALUE, "offset is too big for an array");
      checkArgument(length <= Integer.MAX_VALUE, "length is too big for an array");
      returnVal = new InputStreamSupplierPayload(
            ByteStreams.newInputStreamSupplier(content, (int) offset, (int) length));
      return returnVal;
   }

   protected Payload copyMetadataAndSetLength(Payload input, Payload returnVal, long length) {
      returnVal.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(input.getContentMetadata()
            .toBuilder().contentLength(length).contentMD5(null).build()));
      return returnVal;
   }

}
