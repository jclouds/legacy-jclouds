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
package org.jclouds.sqs.features;

import static org.jclouds.sqs.reference.SQSParameters.ACTION;
import static org.jclouds.sqs.reference.SQSParameters.VERSION;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Constants;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.sqs.domain.Action;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to SQS via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@FormParams(keys = VERSION, values = "{" + Constants.PROPERTY_API_VERSION + "}")
@VirtualHost
public interface PermissionAsyncApi {

   /**
    * @see PermissionApi#addPermissionToAccount
    */
   @Named("AddPermission")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "AddPermission")
   ListenableFuture<Void> addPermissionToAccount(@FormParam("Label") String label,
         @FormParam("ActionName.1") Action permission, @FormParam("AWSAccountId.1") String accountId);

   /**
    * @see PermissionApi#remove
    */
   @Named("RemovePermission")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RemovePermission")
   ListenableFuture<Void> remove(@FormParam("Label") String label);

}
