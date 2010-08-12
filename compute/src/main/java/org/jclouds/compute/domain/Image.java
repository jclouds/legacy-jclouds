/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.compute.domain;

import org.jclouds.compute.domain.internal.ImageImpl;

import com.google.inject.ImplementedBy;
import org.jclouds.domain.Credentials;

/**
 * Running Operating system
 * 
 * @author Adrian Cole
 */
@ImplementedBy(ImageImpl.class)
public interface Image extends ComputeMetadata {

   /**
    * Version of the image
    */
   String getVersion();

   /**
    * Description of the image.
    */
   String getDescription();

   /**
    * Operating System
    */
   OsFamily getOsFamily();

   /**
    * Description of the operating system including the version.
    */
   String getOsDescription();

   /**
    * Operating System
    */
   Architecture getArchitecture();

    /**
     * Default credentials for the current image
     */
   Credentials getDefaultCredentials();

}