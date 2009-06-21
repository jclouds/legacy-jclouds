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
package org.jclouds.aws.s3.commands;

import java.net.URI;

import javax.annotation.Resource;

import org.jclouds.aws.s3.domain.AccessControlList;
import org.jclouds.aws.s3.xml.AccessControlListBuilder;
import org.jclouds.http.HttpHeaders;
import org.jclouds.http.HttpMethod;
import org.jclouds.http.commands.callables.ReturnTrueIf2xx;
import org.jclouds.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * A PUT request operation directed at a bucket URI with the "acl" parameter sets the Access Control
 * List (ACL) settings for that S3 item.
 * <p />
 * To set a bucket or object's ACL, you must have WRITE_ACP or FULL_CONTROL access to the item.
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/RESTAccessPolicy.html"/>
 * @author James Murty
 */
public class PutBucketAccessControlList extends S3FutureCommand<Boolean> {
   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public PutBucketAccessControlList(URI endPoint, ReturnTrueIf2xx callable,
            @Assisted("bucketName") String bucket, @Assisted AccessControlList acl) {
      super(endPoint, HttpMethod.PUT, "/?acl", callable, bucket);

      String aclPayload = "";
      try {
         aclPayload = (new AccessControlListBuilder(acl)).getXmlString();
      } catch (Exception e) {
         // TODO: How do we handle this sanely?
         logger.error(e, "Unable to build XML document for Access Control List: " + acl);
      }
      getRequest().setPayload(aclPayload);
      getRequest().getHeaders().put(HttpHeaders.CONTENT_LENGTH, aclPayload.getBytes().length + "");
   }

}