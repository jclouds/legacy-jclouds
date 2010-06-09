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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map.Entry;

import org.jclouds.util.InputStreamChain;
import org.jclouds.util.Utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

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
      chain.addInputStream(part.getData());
      chain.addAsInputStream(rn);
      size += part.getSize() + rn.length();
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

   public static class Part {
      private final Multimap<String, String> headers;
      private final InputStream data;
      private final long size;

      public Part(Multimap<String, String> headers, InputStream data, long size) {
         this.headers = headers;
         this.data = data;
         this.size = size;
      }

      public Part(Multimap<String, String> headers, String data) {
         this(headers, Utils.toInputStream(data), data.length());
      }

      public Part(Multimap<String, String> headers, File data) throws FileNotFoundException {
         this(headers, new FileInputStream(data), data.length());
      }

      public Part(Multimap<String, String> headers, byte[] data) {
         this(headers, new ByteArrayInputStream(data), data.length);
      }

      public Multimap<String, String> getHeaders() {
         return headers;
      }

      public InputStream getData() {
         return data;
      }

      public long getSize() {
         return size;
      }
   }

   public long getSize() {
      return size;
   }

   public InputStream getData() {
      return chain;
   }
}
