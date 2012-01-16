/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.vcloud.util;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.VCloudError;
import org.jclouds.vcloud.xml.ErrorHandler;

/**
 * Needed to sign and verify requests and responses.
 * 
 * @author Adrian Cole
 */
@Singleton
public class VCloudUtils {
   private final ParseSax.Factory factory;
   private final Provider<ErrorHandler> errorHandlerProvider;
   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   VCloudUtils(Factory factory, Provider<ErrorHandler> errorHandlerProvider) {
      this.factory = factory;
      this.errorHandlerProvider = errorHandlerProvider;
   }

   public VCloudError parseErrorFromContent(HttpRequest request, HttpResponse response) {
      // HEAD has no content
      if (response.getPayload() == null)
         return null;
      // NOTE in vCloud Datacenter 1.5, if you make vCloud 1.0 requests, the content type 
      // header is suffixed with ;1.0
      String contentType = response.getPayload().getContentMetadata().getContentType();
      if (contentType != null && contentType.startsWith(VCloudMediaType.ERROR_XML)) {
         try {
            return (VCloudError) factory.create(errorHandlerProvider.get()).setContext(request).apply(response);
         } catch (RuntimeException e) {
            logger.warn(e, "error parsing error");
         }
      }
      return null;
   }
}