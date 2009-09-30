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

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.net.URI;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;
import org.jclouds.mezeo.pcs2.PCSUtil;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.mezeo.pcs2.util.PCSUtils;
import org.jclouds.rest.RestContext;
import org.jclouds.util.Utils;

import com.google.common.base.Function;

/**
 * PCS does not return an eTag header. As such, we'll make one out of the object id.
 * 
 * @author Adrian Cole
 */
public class AddMetadataAndParseResourceIdIntoBytes implements Function<HttpResponse, byte[]>,
         RestContext {
   private final PCSUtil util;
   @Resource
   protected Logger logger = Logger.NULL;
   private Object[] args;
   private HttpRequest request;

   @Inject
   public AddMetadataAndParseResourceIdIntoBytes(PCSUtil util) {
      this.util = util;
   }

   public byte[] apply(HttpResponse from) {
      checkState(args != null, "args should be initialized at this point");
      PCSFile file = null;
      for (Object arg : args) {
         if (arg instanceof PCSFile)
            file = (PCSFile) arg;
      }
      checkState(file != null, "No PCSFile found in args, improper method declarations");
      checkState(request != null, "request should be initialized at this point");

      try {
         String toParse = Utils.toStringAndClose(from.getContent());
         logger.trace("%s: received the following response: %s", from, toParse);
         URI uri = URI.create(toParse.trim());
         for (Entry<String, String> entry : file.getMetadata().getUserMetadata().entries()) {
            URI key = UriBuilder.fromUri(uri).path(String.format("metadata/%s", entry.getKey()))
                     .build();
            util.put(key, entry.getValue());
         }
         return PCSUtils.getEtag(uri);
      } catch (IOException e) {
         throw new HttpResponseException("couldn't parse url from response", null, from, e);
      }
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