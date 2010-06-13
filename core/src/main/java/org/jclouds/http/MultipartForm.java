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
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.payloads.FilePayload;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.io.InputSupplier;

/**
 * 
 * @author Adrian Cole
 */
public class MultipartForm implements Payload {
   private static final String rn = "\r\n";
   private static final String dd = "--";

   private final InputSupplier<? extends InputStream> chain;
   private long size;
   private boolean isRepeatable;
   private boolean written;
   private final Iterable<? extends Part> parts;

   public MultipartForm(String boundary, Part... parts) {
      this(boundary, Lists.newArrayList(parts));
   }

   @SuppressWarnings("unchecked")
   public MultipartForm(String boundary, Iterable<? extends Part> parts) {
      this.parts = parts;
      String boundaryrn = boundary + rn;
      isRepeatable = true;
      InputSupplier<? extends InputStream> chain = ByteStreams.join();
      for (Part part : parts) {
         if (!part.isRepeatable())
            isRepeatable = false;
         size += part.calculateSize();
         chain = ByteStreams.join(chain, addLengthAndReturnHeaders(boundaryrn, part), part,
                  addLengthAndReturnRn());
      }
      chain = ByteStreams.join(chain, addLengthAndReturnFooter(boundary));
      this.chain = chain;
   }

   private InputSupplier<? extends InputStream> addLengthAndReturnRn() {
      size += rn.length();
      return ByteStreams.newInputStreamSupplier(rn.getBytes());
   }

   private InputSupplier<? extends InputStream> addLengthAndReturnHeaders(String boundaryrn,
            Part part) {
      StringBuilder builder = new StringBuilder(dd).append(boundaryrn);
      for (Entry<String, String> entry : part.getHeaders().entries()) {
         String header = String.format("%s: %s%s", entry.getKey(), entry.getValue(), rn);
         builder.append(header);
      }
      builder.append(rn);
      size += builder.length();
      return ByteStreams.newInputStreamSupplier(builder.toString().getBytes());
   }

   private InputSupplier<? extends InputStream> addLengthAndReturnFooter(String boundary) {
      String end = dd + boundary + dd + rn;
      size += end.length();
      return ByteStreams.newInputStreamSupplier(end.getBytes());
   }

   public MultipartForm(Part... parts) {
      this("__redrose__", parts);
   }

   public static class Part implements Payload {
      private final String name;
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

         public static PartMap create(String name, Payload delegate, PartOptions options) {
            String filename = options != null ? options.getFilename() : null;
            if (delegate instanceof FilePayload)
               filename = FilePayload.class.cast(delegate).getRawContent().getName();
            PartMap returnVal;
            returnVal = (filename != null) ? create(name, filename) : create(name);
            if (options != null)
               returnVal.contentType(options.getContentType());
            return returnVal;

         }
      }

      private Part(String name, PartMap map, Payload delegate) {
         this.name = name;
         this.delegate = checkNotNull(delegate, "delegate");
         this.headers = ImmutableMultimap.copyOf(Multimaps.forMap((checkNotNull(map, "headers"))));
      }

      public static class PartOptions {
         private String contentType;
         private String filename;

         public PartOptions contentType(String contentType) {
            this.contentType = checkNotNull(contentType, "contentType");
            return this;
         }

         public PartOptions filename(String filename) {
            this.filename = checkNotNull(filename, "filename");
            return this;
         }

         public static class Builder {
            public static PartOptions contentType(String contentType) {
               return new PartOptions().contentType(contentType);
            }

            public static PartOptions filename(String filename) {
               return new PartOptions().filename(filename);
            }
         }

         public String getContentType() {
            return contentType;
         }

         public String getFilename() {
            return filename;
         }
      }

      public static Part create(String name, String value) {
         return new Part(name, PartMap.create(name), Payloads.newStringPayload(value));
      }

      public static Part create(String name, Payload delegate, PartOptions options) {
         return new Part(name, PartMap.create(name, delegate, options), delegate);
      }

      public Multimap<String, String> getHeaders() {
         return headers;
      }

      @Override
      public Long calculateSize() {
         return delegate.calculateSize();
      }

      @Override
      public InputStream getInput() {
         return delegate.getInput();
      }

      @Override
      public Object getRawContent() {
         return delegate.getInput();
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
         result = prime * result + ((name == null) ? 0 : name.hashCode());
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
         if (name == null) {
            if (other.name != null)
               return false;
         } else if (!name.equals(other.name))
            return false;
         return true;
      }

      public String getName() {
         return name;
      }
   }

   @Override
   public Long calculateSize() {
      return size;
   }

   @Override
   public InputStream getInput() {
      try {
         return chain.getInput();
      } catch (IOException e) {
         Throwables.propagate(e);
         return null;
      }
   }

   @Override
   public Object getRawContent() {
      return getInput();
   }

   @Override
   public boolean isRepeatable() {
      return isRepeatable;
   }

   @Override
   public void writeTo(OutputStream outstream) throws IOException {
      checkState(!written || !isRepeatable,
               "InputStreams can only be writted to an outputstream once");
      written = true;
      InputStream in = getInput();
      try {
         ByteStreams.copy(getInput(), outstream);
      } finally {
         Closeables.closeQuietly(in);
      }
   }

   @Override
   public String toString() {
      return "MultipartForm [chain=" + chain + ", isRepeatable=" + isRepeatable + ", size=" + size
               + ", written=" + written + "]";
   }

   public Iterable<? extends Part> getParts() {
      return parts;
   }
}
