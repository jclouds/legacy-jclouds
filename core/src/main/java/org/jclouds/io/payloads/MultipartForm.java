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

import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.ByteStreams.join;
import static com.google.common.io.ByteStreams.newInputStreamSupplier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;

import com.google.common.io.InputSupplier;

/**
 * 
 * @author Adrian Cole
 */
public class MultipartForm extends BasePayload<Iterable<? extends Part>> {
   public static final String BOUNDARY = "--JCLOUDS--";
   private static final String rn = "\r\n";
   private static final String dd = "--";

   private boolean isRepeatable;
   private final InputSupplier<? extends InputStream> chain;

   @SuppressWarnings("unchecked")
   public MultipartForm(String boundary, Iterable<? extends Part> content) {
      super(content);
      getContentMetadata().setContentType("multipart/form-data; boundary=" + boundary);
      getContentMetadata().setContentLength(0l);
      String boundaryrn = boundary + rn;
      isRepeatable = true;
      InputSupplier<? extends InputStream> chain = join();
      for (Part part : content) {
         if (!part.isRepeatable())
            isRepeatable = false;
         getContentMetadata().setContentLength(
                  getContentMetadata().getContentLength() + part.getContentMetadata().getContentLength());
         chain = join(chain, addLengthAndReturnHeaders(boundaryrn, part), part, addLengthAndReturnRn());
      }
      chain = join(chain, addLengthAndReturnFooter(boundary));
      this.chain = chain;
   }

   public MultipartForm(String boundary, Part... parts) {
      this(boundary, newArrayList(parts));
   }

   public MultipartForm(Part... parts) {
      this(BOUNDARY, parts);
   }

   private InputSupplier<? extends InputStream> addLengthAndReturnRn() {
      getContentMetadata().setContentLength(getContentMetadata().getContentLength() + rn.length());
      return newInputStreamSupplier(rn.getBytes());
   }

   private InputSupplier<? extends InputStream> addLengthAndReturnHeaders(String boundaryrn, Part part) {
      StringBuilder builder = new StringBuilder(dd).append(boundaryrn);
      for (Entry<String, String> entry : part.getHeaders().entries()) {
         String header = String.format("%s: %s%s", entry.getKey(), entry.getValue(), rn);
         builder.append(header);
      }
      builder.append(rn);
      getContentMetadata().setContentLength(getContentMetadata().getContentLength() + builder.length());
      return newInputStreamSupplier(builder.toString().getBytes());
   }

   private InputSupplier<? extends InputStream> addLengthAndReturnFooter(String boundary) {
      String end = dd + boundary + dd + rn;
      getContentMetadata().setContentLength(getContentMetadata().getContentLength() + end.length());
      return newInputStreamSupplier(end.getBytes());
   }

   @Override
   public InputStream getInput() {
      try {
         return chain.getInput();
      } catch (IOException e) {
         propagate(e);
         return null;
      }
   }

   @Override
   public boolean isRepeatable() {
      return isRepeatable;
   }

   @Override
   public void release() {
      for (Part part : content)
         part.release();
   }

}
