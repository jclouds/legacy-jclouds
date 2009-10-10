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
package org.jclouds.mezeo.pcs2.functions;

import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;

import org.jclouds.blobstore.domain.Key;
import org.jclouds.blobstore.functions.ParseContentTypeFromHeaders;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.rest.InvocationContext;

import com.google.common.base.Function;

/**
 * Parses response headers and creates a new PCSFile from them and the HTTP content.
 * 
 * @see ParseContentTypeFromHeaders
 * @author Adrian Cole
 */
public class AssembleBlobFromContentAndMetadataCache implements Function<HttpResponse, PCSFile>,
         InvocationContext {

   private final ConcurrentMap<Key, FileMetadata> cache;
   private HttpRequest request;
   private Object[] args;

   @Inject
   public AssembleBlobFromContentAndMetadataCache(ConcurrentMap<Key, FileMetadata> cache) {
      this.cache = cache;
   }

   public PCSFile apply(HttpResponse from) {
      FileMetadata metadata = cache.get(new Key(getArgs()[0].toString(), this.getArgs()[1].toString()));
      PCSFile blob = new PCSFile(metadata);
      blob.setData(from.getContent());
      blob.setContentLength(metadata.getSize());
      return blob;
   }

   public Object[] getArgs() {
      return args;
   }

   public HttpRequest getRequest() {
      return request;
   }

   public void setContext(HttpRequest request, Object[] args) {
      this.args = args;
      this.request = request;
   }

}