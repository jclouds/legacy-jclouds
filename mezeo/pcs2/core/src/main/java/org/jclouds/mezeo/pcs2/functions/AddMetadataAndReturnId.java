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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.jclouds.blobstore.domain.Key;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.http.HttpResponse;
import org.jclouds.logging.Logger;
import org.jclouds.mezeo.pcs2.PCSUtil;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.util.Utils;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * PCS does not return an eTag header. As such, we'll make one out of the object id.
 * 
 * @author Adrian Cole
 */
public class AddMetadataAndReturnId implements Function<HttpResponse, String>,
         InvocationContext {
   private final PCSUtil util;
   private final ConcurrentMap<Key, String> fileCache;

   @Resource
   protected Logger logger = Logger.NULL;

   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(BlobStoreConstants.PROPERTY_BLOBSTORE_TIMEOUT)
   protected long requestTimeoutMilliseconds = 30000;
   private GeneratedHttpRequest<?> request;

   @Inject
   public AddMetadataAndReturnId(ConcurrentMap<Key, String> fileCache, PCSUtil util) {
      this.fileCache = fileCache;
      this.util = util;
   }

   public String apply(HttpResponse from) {
      if (from.getStatusCode() > 204)
         throw new BlobRuntimeException("Incorrect code for: " + from);
      checkState(request != null, "request should be initialized at this point");
      checkState(request.getArgs() != null, "request.getArgs() should be initialized at this point");
      checkArgument(request.getArgs()[0] instanceof String, "arg[0] must be a container name");
      checkArgument(request.getArgs()[1] instanceof PCSFile, "arg[1] must be a pcsfile");
      String container = request.getArgs()[0].toString();
      PCSFile file = (PCSFile) request.getArgs()[1];

      Key key = new Key(container, file.getKey());
      String id = checkNotNull(fileCache.get(key), String.format(
               "file %s should have an id in cache by now", key));

      IOUtils.closeQuietly(from.getContent());

      Set<Future<Void>> puts = Sets.newHashSet();
      for (Entry<String, String> entry : file.getMetadata().getUserMetadata().entrySet()) {
         puts.add(util.putMetadata(id, entry.getKey(), entry.getValue()));
      }
      for (Future<Void> put : puts) {
         try {
            put.get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
         } catch (Exception e) {
            Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
            throw new BlobRuntimeException("Error putting metadata for file: " + file, e);
         }
      }
      return id;
   }

   public void setContext(GeneratedHttpRequest<?> request) {
      this.request = request;
   }

}