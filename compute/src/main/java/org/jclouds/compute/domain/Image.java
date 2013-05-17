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
package org.jclouds.compute.domain;

import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.domain.LoginCredentials;

import com.google.common.annotations.Beta;
import com.google.inject.ImplementedBy;

/**
 * Running Operating system
 * 
 * @author Adrian Cole
 */
@ImplementedBy(ImageImpl.class)
public interface Image extends ComputeMetadataIncludingStatus<Image.Status> {

   public static enum Status {
      /**
       * The image is in transition
       */
      PENDING,
      /**
       * The image is visible, and in the process of being deleted.
       */
      DELETED,
      /**
       * The image is available.
       */
      AVAILABLE,
      /**
       * There is an error on the image
       */
      ERROR,
      /**
       * The state of the image is unrecognized.
       */
      UNRECOGNIZED;

   }
   
   /**
    * The operating system installed on this image
    */
   @Beta
   OperatingSystem getOperatingSystem();

   /**
    * Version of the image
    */
   String getVersion();

   /**
    * Description of the image.
    */
   String getDescription();

   /**
    * Default credentials for the current image
    */
   LoginCredentials getDefaultCredentials();

}
