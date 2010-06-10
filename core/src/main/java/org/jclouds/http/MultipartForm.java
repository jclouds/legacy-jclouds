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
package org.jclouds.http;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.payloads.FilePayload;
import org.jclouds.util.InputStreamChain;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * 
 * @author Adrian Cole
 */
public class MultipartForm {
   private static final String rn = "\r\n";
   private static final String dd = "--";

   private final InputStreamChain chain;
   private long size;

   public MultipartForm(String boundary, Part... parts) {
      this(boundary, Lists.newArrayList(parts));
   }

   public MultipartForm(String boundary, Iterable<? extends Part> parts) {
      String boundaryrn = boundary + rn;
      chain = new InputStreamChain();
      for (Part part : parts) {
         addHeaders(boundaryrn, part);
         addData(part);
      }
      addFooter(boundary);
   }

   private void addData(Part part) {
      chain.addInputStream(part.getContent());
      chain.addAsInputStream(rn);
      size += part.calculateSize() + rn.length();
   }

   private void addHeaders(String boundaryrn, Part part) {
      StringBuilder builder = new StringBuilder(dd).append(boundaryrn);
      for (Entry<String, String> entry : part.getHeaders().entries()) {
         String header = String.format("%s: %s%s", entry.getKey(), entry.getValue(), rn);
         builder.append(header);
      }
      builder.append(rn);
      chain.addAsInputStream(builder.toString());
      size += builder.length();
   }

   private void addFooter(String boundary) {
      String end = dd + boundary + dd + rn;
      chain.addAsInputStream(end);
      size += end.length();
   }

   public MultipartForm(Part... parts) {
      this("__redrose__", parts);
   }

   public static class Part implements Payload {
      private final Multimap<String, String> headers;
      private final Payload delegate;

      private static class PartMap extends LinkedHashMap<String, String> {

         /** The serialVersionUID */
         private static final long serialVersionUID = -287387556008320212L;

         static PartMap create(String name) {
            PartMap map = new PartMap();
            map.put("Content-Disposition", String.format("form-data; name=\"%s\"", checkNotNull(
                     name, "name")));
            return map;
         }

         static PartMap create(String name, String filename) {
            PartMap map = new PartMap();
            map.put("Content-Disposition", String.format("form-data; name=\"%s\"; filename=\"%s\"",
                     checkNotNull(name, "name"), checkNotNull(filename, "filename")));
            return map;
         }

         PartMap contentType(@Nullable String type) {
            if (type != null)
               put(HttpHeaders.CONTENT_TYPE, checkNotNull(type, "type"));
            return this;
         }
      }

      private Part(PartMap map, Payload delegate) {
         this.delegate = checkNotNull(delegate, "delegate");
         this.headers = ImmutableMultimap.copyOf(Multimaps.forMap((checkNotNull(map, "headers"))));
      }

      public static Part create(String name, String value) {
         return new Part(PartMap.create(name), Payloads.newStringPayload(value));
      }

      public static Part create(String name, Payload delegate, String contentType) {
         return new Part(PartMap.create(name).contentType(contentType), delegate);
      }

      public static Part create(String name, FilePayload delegate, String contentType) {
         return new Part(PartMap.create(name, delegate.getRawContent().getName()).contentType(
                  contentType), delegate);
      }

      public Multimap<String, String> getHeaders() {
         return headers;
      }

      @Override
      public Long calculateSize() {
         return delegate.calculateSize();
      }

      @Override
      public InputStream getContent() {
         return delegate.getContent();
      }

      @Override
      public Object getRawContent() {
         return delegate.getContent();
      }

      @Override
      public boolean isRepeatable() {
         return delegate.isRepeatable();
      }

      @Override
      public void writeTo(OutputStream outstream) throws IOException {
         delegate.writeTo(outstream);
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((delegate == null) ? 0 : delegate.hashCode());
         result = prime * result + ((headers == null) ? 0 : headers.hashCode());
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
         Part other = (Part) obj;
         if (delegate == null) {
            if (other.delegate != null)
               return false;
         } else if (!delegate.equals(other.delegate))
            return false;
         if (headers == null) {
            if (other.headers != null)
               return false;
         } else if (!headers.equals(other.headers))
            return false;
         return true;
      }
   }

   public long getSize() {
      return size;
   }

   public InputStream getData() {
      return chain;
   }
}
