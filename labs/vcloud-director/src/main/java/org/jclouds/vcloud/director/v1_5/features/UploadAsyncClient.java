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
package org.jclouds.vcloud.director.v1_5.features;

import java.io.File;

import javax.ws.rs.PUT;

import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.vcloud.director.v1_5.domain.URISupplier;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationToRequest;
import org.jclouds.vcloud.director.v1_5.functions.ThrowVCloudErrorOn4xx;

import com.google.common.util.concurrent.ListenableFuture;

/**

 * @see UploadClient
 * @author danikov
 */
@RequestFilters(AddVCloudAuthorizationToRequest.class)
public interface UploadAsyncClient { // TODO: implement these operations correctly

   /**
    * @see UploadClient#uploadFile(URISupplier, File)
    */
   @PUT
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Object> uploadFile(URISupplier target, File file);
   
   /**
    * @see UploadClient#uploadBigFile(URISupplier, File)
    */
   @PUT
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Object> uploadBigFile(URISupplier target, File file);
   
}
