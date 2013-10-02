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

/**
 * Provides access to SQS via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@FormParams(keys = VERSION, values = "{" + Constants.PROPERTY_API_VERSION + "}")
@VirtualHost
public interface PermissionApi {

   /**
    * The AddPermission action adds a permission to a queue for a specific
    * principal. This allows for sharing access to the queue.
    * 
    * When you create a queue, you have full control access rights for the
    * queue. Only you (as owner of the queue) can grant or deny permissions to
    * the queue. For more information about these permissions, see Shared Queues
    * in the Amazon SQS Developer Guide.
    * 
    * Note
    * 
    * AddPermission writes an SQS-generated policy. If you want to write your
    * own policy, use SetQueueAttributes to upload your policy.
    * 
    * @param queue
    *           queue to change permissions on
    * @param label
    * 
    *           The unique identification of the permission you're setting.
    *           example: AliceSendMessage
    * 
    *           Constraints: Maximum 80 characters; alphanumeric characters,
    *           hyphens (-), and underscores (_) are allowed.
    * @param permission
    *           The action you want to allow for the specified principal.
    * @param accountId
    *           The AWS account number of the principal who will be given
    *           permission. The principal must have an AWS account, but does not
    *           need to be signed up for Amazon SQS. For information about
    *           locating the AWS account identification, see Your AWS
    *           Identifiers in the Amazon SQS Developer Guide.
    * 
    *           Constraints: Valid 12-digit AWS account number, without hyphens
    * 
    */
   @Named("AddPermission")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "AddPermission")
   void addPermissionToAccount(@FormParam("Label") String label,
         @FormParam("ActionName.1") Action permission, @FormParam("AWSAccountId.1") String accountId);

   /**
    * The RemovePermission action revokes any permissions in the queue policy
    * that matches the Label parameter. Only the owner of the queue can remove
    * permissions.
    * 
    * @param queue
    *           queue to change permissions on
    * 
    * @param label
    *           The identification of the permission you want to remove. This is
    *           the label you added in AddPermission. example: AliceSendMessage
    */
   @Named("RemovePermission")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RemovePermission")
   void remove(@FormParam("Label") String label);

}
