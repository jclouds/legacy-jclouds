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
package org.jclouds.vcloud.domain;

import java.net.URI;

import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;

import com.google.inject.ImplementedBy;

/**
 * Many container elements are populated with references to contained objects. Each reference
 * consists of a hyperlink, an optional media type, and a name.
 * 
 * @author Adrian Cole
 * 
 */
@ImplementedBy(ReferenceTypeImpl.class)
public interface ReferenceType extends Comparable<ReferenceType> {
   /**
    * @return hyperlink to the referenced object
    */
   URI getHref();

   /**
    * @return name of the referenced object.
    * 
    */
   String getName();

   /**
    * @return object type, expressed as the media type of the XML representing of the object
    * @see VCloudMediaType
    */
   String getType();

}
