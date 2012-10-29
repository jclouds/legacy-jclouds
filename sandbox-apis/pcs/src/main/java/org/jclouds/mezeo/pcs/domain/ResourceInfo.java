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
package org.jclouds.mezeo.pcs.domain;

import java.net.URI;
import java.util.Date;

import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.mezeo.pcs.domain.internal.ResourceInfoImpl;

import com.google.inject.ImplementedBy;

/**
 * 
 * @author Adrian Cole
 * 
 */
@ImplementedBy(ResourceInfoImpl.class)
public interface ResourceInfo extends Comparable<ResourceInfo> {
   StorageType getType();

   URI getUrl();

   String getName();

   Date getCreated();

   Boolean isInProject();

   Date getModified();

   String getOwner();

   Integer getVersion();

   Boolean isShared();

   Date getAccessed();

   Long getBytes();

   URI getMetadata();

   URI getParent();

   URI getTags();

}
