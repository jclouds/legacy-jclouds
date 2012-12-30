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
package org.jclouds.azure.management.features;

import java.util.Set;
import org.jclouds.azure.management.domain.OSImage;
import org.jclouds.azure.management.domain.OSImageParams;

/**
 * The Service Management API includes operations for managing the OS images in your subscription.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157175">docs</a>
 * @see OSImageAsyncApi
 * @author Gerald Pereira, Adrian Cole
 */
public interface OSImageApi {

   /**
    * The List Hosted Services operation lists the hosted services available under the current
    * subscription.
    * 
    * @return the response object
    */
   Set<OSImage> list();

   /**
    * The Add OS Image operation adds an OS image that is currently stored in a storage account in your subscription to the image repository.
    * 
    * @param params
    *           the required parameters needed to add an image
    */
   void add(OSImageParams params);
   
   /**
    * The Update OS Image operation updates an OS image that in your image repository.
    * 
    * @param params
    *           the required parameters needed to update an image
    */
   void update(OSImageParams params);
   
   /**
    * The Delete Hosted Service operation deletes the specified hosted service from Windows Azure.
    * 
    * @param imageName
    *           the unique DNS Prefix value in the Windows Azure Management Portal
    */
   void delete(String imageName);

}
