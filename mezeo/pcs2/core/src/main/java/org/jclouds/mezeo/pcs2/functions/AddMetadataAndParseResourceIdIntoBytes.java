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
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.logging.Logger;
import org.jclouds.mezeo.pcs2.PCSUtil;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.mezeo.pcs2.util.PCSUtils;
import org.jclouds.rest.InvocationContext;
import org.jclouds.util.Utils;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * PCS does not return an eTag header. As such, we'll make one out of the object id.
 * 
 * @author Adrian Cole
 */
public class AddMetadataAndParseResourceIdIntoBytes implements Function<HttpResponse, byte[]>,
         InvocationContext {
   private final PCSUtil util;
   private final ConcurrentMap<Key, String> fileCache;

   @Resource
   protected Logger logger = Logger.NULL;
   private Object[] args;
   private HttpRequest request;

   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(BlobStoreConstants.PROPERTY_BLOBSTORE_TIMEOUT)
   protected long requestTimeoutMilliseconds = 30000;

   @Inject
   public AddMetadataAndParseResourceIdIntoBytes(ConcurrentMap<Key, String> fileCache, PCSUtil util) {
      this.fileCache = fileCache;
      this.util = util;
   }

   public byte[] apply(HttpResponse from) {
      if (from.getStatusCode() > 204)
         throw new BlobRuntimeException("Incorrect code for: " + from);
      checkState(request != null, "request should be initialized at this point");
      checkState(args != null, "args should be initialized at this point");
      checkArgument(args[0] instanceof String, "arg[0] must be a container name");
      checkArgument(args[1] instanceof PCSFile, "arg[1] must be a pcsfile");
      String container = args[0].toString();
      PCSFile file = (PCSFile) args[1];

      Key key = new Key(container, file.getKey());
      String id = checkNotNull(fileCache.get(key), String.format(
               "file %s should have an id in cache by now", key));

      IOUtils.closeQuietly(from.getContent());

      Set<Future<Void>> puts = Sets.newHashSet();
      for (Entry<String, String> entry : file.getMetadata().getUserMetadata().entries()) {
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

      return PCSUtils.getETag(id);
   }

   public Object[] getArgs() {
      return args;
   }

   public HttpRequest getRequest() {
      return request;
   }

   public void setContext(HttpRequest request, Object[] args) {
      this.request = request;
      this.args = args;
   }

}