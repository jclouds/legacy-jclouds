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

import org.apache.commons.io.IOUtils;
import org.jclouds.blobstore.domain.Key;
import org.jclouds.http.HttpResponse;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;

/**
 * invalidates cache and returns true when the http response code is in the range 200-299.
 * 
 * @author Adrian Cole
 */
public class InvalidatePCSKeyCacheAndReturnVoidIf2xx implements Function<HttpResponse, Void>,
         InvocationContext {
   private final ConcurrentMap<Key, String> cache;
   private final ConcurrentMap<Key, FileMetadata> mdCache;
   private GeneratedHttpRequest<?> request;

   @Inject
   public InvalidatePCSKeyCacheAndReturnVoidIf2xx(ConcurrentMap<Key, String> cache,
            ConcurrentMap<Key, FileMetadata> mdCache) {
      this.cache = cache;
      this.mdCache = mdCache;
   }

   public Void apply(HttpResponse from) {
      IOUtils.closeQuietly(from.getContent());
      int code = from.getStatusCode();
      if (code >= 300 || code < 200) {
         throw new IllegalStateException("incorrect code for this operation: " + from);
      }
      Key key = new Key(request.getArgs()[0].toString(), request.getArgs()[1].toString());
      cache.remove(key);
      mdCache.remove(key);
      return null;
   }

   public void setContext(GeneratedHttpRequest<?> request) {
      this.request = request;
   }

}