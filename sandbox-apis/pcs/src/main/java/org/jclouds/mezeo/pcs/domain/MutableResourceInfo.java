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
import org.jclouds.mezeo.pcs.domain.internal.MutableResourceInfoImpl;

import com.google.inject.ImplementedBy;

/**
 * 
 * @author Adrian Cole
 * 
 */
@ImplementedBy(MutableResourceInfoImpl.class)
public interface MutableResourceInfo extends ResourceInfo {
   void setType(StorageType value);

   void setUrl(URI value);

   void setName(String value);

   void setCreated(Date value);

   void setInProject(Boolean value);

   void setModified(Date value);

   void setOwner(String value);

   void setVersion(Integer value);

   void setShared(Boolean value);

   void setAccessed(Date value);

   void setBytes(Long value);

   void setMetadata(URI value);

   void setParent(URI value);

   void setTags(URI value);

}
