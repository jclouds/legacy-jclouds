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
package org.jclouds.s3.binders;

import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_SERVICE_PATH;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_VIRTUAL_HOST_BUCKETS;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;
import org.jclouds.rest.binders.BindAsHostPrefix;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindAsHostPrefixIfConfigured implements Binder {

   protected final BindAsHostPrefix bindAsHostPrefix;
   protected final boolean isVhostStyle;
   protected final String servicePath;

   @Inject
   public BindAsHostPrefixIfConfigured(BindAsHostPrefix bindAsHostPrefix,
            @Named(PROPERTY_S3_VIRTUAL_HOST_BUCKETS) boolean isVhostStyle,
            @Named(PROPERTY_S3_SERVICE_PATH) String servicePath) {
      this.bindAsHostPrefix = bindAsHostPrefix;
      this.isVhostStyle = isVhostStyle;
      this.servicePath = servicePath;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object payload) {
      // If we have a payload/bucket/container that is not all lowercase, vhost-style URLs are not an option and must be
      // automatically converted to their path-based equivalent.  This should only be possible for AWS-S3 since it is
      // the only S3 implementation configured to allow uppercase payload/bucket/container names.
      //
      // http://code.google.com/p/jclouds/issues/detail?id=992
      String payloadAsString = payload.toString();

      if (isVhostStyle && payloadAsString.equals(payloadAsString.toLowerCase())) {
         request = bindAsHostPrefix.bindToRequest(request, payload);
         String host = request.getEndpoint().getHost();
         if (request.getEndpoint().getPort() != -1) {
            host += ":" + request.getEndpoint().getPort();
         }
         return (R) request.toBuilder().replaceHeader(HttpHeaders.HOST, host).build();
      } else {
         StringBuilder path = new StringBuilder(request.getEndpoint().getPath());
         if (servicePath.equals("/")) {
            if (path.toString().equals("/"))
               path.append(payloadAsString);
            else 
               path.insert(0, "/" + payloadAsString);
         } else {
            int indexToInsert = 0;
            indexToInsert = path.indexOf(servicePath);
            indexToInsert = indexToInsert == -1 ? 0 : indexToInsert;
            indexToInsert += servicePath.length();
            path.insert(indexToInsert, "/" + payloadAsString);
         }
         return (R) request.toBuilder().replacePath(path.toString()).build();
      }
   }
}
