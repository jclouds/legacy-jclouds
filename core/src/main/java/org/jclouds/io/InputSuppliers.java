/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.io;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;

import org.jclouds.encoding.internal.FlexBase64;

import com.google.common.annotations.Beta;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;

/**
 * functions related to or replacing those in {@link com.google.common.io.InputSupplier}
 * 
 * @author Adrian Cole
 */
@Beta
public class InputSuppliers {
   /**
    * base64 encodes bytes from the supplied supplier as they are read.
    */
   public static InputSupplier<InputStream> base64Encoder(final InputSupplier<? extends InputStream> supplier) throws IOException {
      return new InputSupplier<InputStream>() {
         @Override
         public InputStream getInput() throws IOException {
            return FlexBase64.createEncoderInputStream(supplier.getInput(), 8192, false);
         }
      };
   }

   /**
    * base64 decodes bytes from the supplied supplier as they are read.
    */
   public static InputSupplier<InputStream> base64Decoder(final InputSupplier<? extends InputStream> supplier) throws IOException {
      return new InputSupplier<InputStream>() {
         @Override
         public InputStream getInput() throws IOException {
            return FlexBase64.createDecoderInputStream(supplier.getInput());
         }
      };
   }
   
   public static InputSupplier<? extends InputStream> of(final InputStream in) {
      checkNotNull(in, "in");
      return new InputSupplier<InputStream>() {

         @Override
         public InputStream getInput() throws IOException {
            return in;
         }

      };
   }

   public static InputSupplier<? extends InputStream> of(String in) {
      byte[] bytes = checkNotNull(in, "in").getBytes(Charsets.UTF_8);
      return ByteStreams.newInputStreamSupplier(bytes);
   }
}
