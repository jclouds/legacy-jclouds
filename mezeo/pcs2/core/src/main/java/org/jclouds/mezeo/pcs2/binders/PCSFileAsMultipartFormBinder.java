/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.mezeo.pcs2.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.MultipartForm;
import org.jclouds.http.MultipartForm.Part;
import org.jclouds.rest.binders.EntityBinder;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * 
 * @author Adrian Cole
 */
public class PCSFileAsMultipartFormBinder implements EntityBinder {

   public static final String BOUNDARY = "--PCS--";

   public void addEntityToRequest(Object entity, HttpRequest request) {
      Blob<?> object = (Blob<?>) entity;

      Multimap<String, String> partHeaders = ImmutableMultimap.of("Content-Disposition",
               String.format("form-data; name=\"%s\"; filename=\"%s\"", object.getKey(), object
                        .getKey()), HttpHeaders.CONTENT_TYPE, checkNotNull(object.getMetadata()
                        .getContentType(), "object.metadata.contentType()"));

      Object data = checkNotNull(object.getData(), "object.getData()");

      Part part;
      try {

         if (data instanceof byte[]) {
            part = new Part(partHeaders, (byte[]) data);
         } else if (data instanceof String) {
            part = new Part(partHeaders, (String) data);
         } else if (data instanceof File) {
            part = new Part(partHeaders, (File) data);
         } else if (data instanceof InputStream) {
            part = new Part(partHeaders, (InputStream) data, object.getContentLength());
         } else {
            throw new IllegalArgumentException("type of part not supported: "
                     + data.getClass().getCanonicalName() + "; " + object);
         }
      } catch (FileNotFoundException e) {
         throw new IllegalArgumentException("file for part not found: " + object);
      }
      MultipartForm form = new MultipartForm(BOUNDARY, part);

      request.setEntity(form.getData());
      request.getHeaders().put(HttpHeaders.CONTENT_TYPE,
               "multipart/form-data; boundary=" + BOUNDARY);

      request.getHeaders().put(HttpHeaders.CONTENT_LENGTH, form.getSize() + "");

   }
}
