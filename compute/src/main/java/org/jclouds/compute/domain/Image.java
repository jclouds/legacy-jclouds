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
package org.jclouds.compute.domain;

import org.jclouds.javax.annotation.Nullable;

import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.domain.Credentials;

import com.google.common.annotations.Beta;
import com.google.inject.ImplementedBy;

/**
 * Running Operating system
 * 
 * @author Adrian Cole
 */
@ImplementedBy(ImageImpl.class)
public interface Image extends ComputeMetadata {
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
    * secures access to root with a password. This password is required to access either the console
    * or run sudo as root.
    * <p/>
    * ex. {@code echo 'password' |sudo -S command}
    * 
    * @return root or console password, if configured, or null.
    */
   @Nullable
   String getAdminPassword();

   /**
    * Default credentials for the current image
    */
   Credentials getDefaultCredentials();

}