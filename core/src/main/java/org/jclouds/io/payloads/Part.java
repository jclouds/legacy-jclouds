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

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * 
 * @author Adrian Cole
 */
public class Part extends DelegatingPayload {
   final String name;
   final Multimap<String, String> headers;

   private static class PartMap extends ImmutableMultimap.Builder<String, String> {

      static Part.PartMap create(String name) {
         Part.PartMap map = new PartMap();
         map.put("Content-Disposition", String.format("form-data; name=\"%s\"", checkNotNull(name,
                  "name")));
         return map;
      }

      static Part.PartMap create(String name, String filename) {
         Part.PartMap map = new PartMap();
         map.put("Content-Disposition", String.format("form-data; name=\"%s\"; filename=\"%s\"",
                  checkNotNull(name, "name"), checkNotNull(filename, "filename")));
         return map;
      }

      Part.PartMap contentType(@Nullable String type) {
         if (type != null)
            put(HttpHeaders.CONTENT_TYPE, checkNotNull(type, "type"));
         return this;
      }

      public static Part.PartMap create(String name, Payload delegate, Part.PartOptions options) {
         String filename = options != null ? options.getFilename() : null;
         if (delegate instanceof FilePayload)
            filename = FilePayload.class.cast(delegate).getRawContent().getName();
         Part.PartMap returnVal;
         returnVal = (filename != null) ? create(name, filename) : create(name);
         if (options != null)
            returnVal.contentType(options.getContentType());
         return returnVal;

      }
   }

   private Part(String name, Part.PartMap map, Payload delegate) {
      super(delegate);
      this.name = name;
      this.headers = checkNotNull(map, "headers").build();
   }

   public static class PartOptions {
      private String contentType;
      private String filename;

      public Part.PartOptions contentType(String contentType) {
         this.contentType = checkNotNull(contentType, "contentType");
         return this;
      }

      public Part.PartOptions filename(String filename) {
         this.filename = checkNotNull(filename, "filename");
         return this;
      }

      public static class Builder {
         public static Part.PartOptions contentType(String contentType) {
            return new PartOptions().contentType(contentType);
         }

         public static Part.PartOptions filename(String filename) {
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

   public static Part create(String name, Payload delegate, Part.PartOptions options) {
      return new Part(name, PartMap.create(name, delegate, options), delegate);
   }

   public Multimap<String, String> getHeaders() {
      return headers;
   }

   public String getName() {
      return name;
   }
}
