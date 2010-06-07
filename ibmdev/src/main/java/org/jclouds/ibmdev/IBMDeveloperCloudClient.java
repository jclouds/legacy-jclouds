/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.ibmdev;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.ibmdev.domain.Image;
import org.jclouds.rest.AuthorizationException;

/**
 * Provides synchronous access to IBMDeveloperCloud.
 * <p/>
 * 
 * @see IBMDeveloperCloudAsyncClient
 * @see <a href="http://www-180.ibm.com/cloud/enterprise/beta/support" />
 * @author Adrian Cole
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface IBMDeveloperCloudClient {
   /**
    * 
    * @return the list of Images available to be provisioned on the IBM DeveloperCloud.
    */
   Set<? extends Image> listImages();

   /**
    * Returns the available Image identified by the supplied Image ID.
    * 
    * @return null if image is not found
    * @throws AuthorizationException
    *            code 401 if the user is not authorized to view this image to section
    */
   Image getImage(long id);

   /**
    * Deletes Image identified by the supplied Image ID.
    * 
    * @throws AuthorizationException
    *            code 401 if the user is not authorized to delete this image
    * @throws IllegalStateException
    *            code 412 if the image is in an invalid state to perform this operation
    */
   void deleteImage(long id);

   /**
    * If set to {@link Image.Visibility#PUBLIC}, makes the Image identified by the supplied Image ID
    * publicly available for all users to create Instances of.
    * 
    * @return modified image or null, if image was not found.
    * 
    * @throws AuthorizationException
    *            code 401 if the user is not authorized to change the visibility of this image
    * @throws IllegalStateException
    *            code 412 if the image is in an invalid state to perform this operation
    */
   Image setImageVisibility(long id, Image.Visibility visibility);
}
