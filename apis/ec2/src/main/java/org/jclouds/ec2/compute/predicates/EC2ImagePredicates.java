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
package org.jclouds.ec2.compute.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.ec2.domain.RootDeviceType;

import com.google.common.base.Predicate;

/**
 * Container for image filters (predicates).
 * 
 * This class has static methods that create customized predicates to use with
 * {@link org.jclouds.compute.ComputeService}.
 * 
 * @author Adrian Cole
 */
public class EC2ImagePredicates {

   /**
    * evaluates true if the Image has the specified root device type
    * 
    * @param rootDeviceType
    *           rootDeviceType of the images
    * @return predicate
    */
   public static Predicate<Image> rootDeviceType(final RootDeviceType rootDeviceType) {
      checkNotNull(rootDeviceType, "rootDeviceType must be defined");
      return ImagePredicates.userMetadataContains("rootDeviceType", rootDeviceType.value());
   }

  
}
