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
package org.jclouds.mezeo.pcs2.domain;

import java.net.URI;

import org.jclouds.blobstore.domain.ResourceType;
import org.jclouds.mezeo.pcs2.domain.internal.MutableResourceInfoImpl;
import org.joda.time.DateTime;

import com.google.inject.ImplementedBy;

/**
 * 
 * @author Adrian Cole
 * 
 */
@ImplementedBy(MutableResourceInfoImpl.class)
public interface MutableResourceInfo extends ResourceInfo {
   void setType(ResourceType value);

   void setUrl(URI value);

   void setName(String value);

   void setCreated(DateTime value);

   void setInProject(Boolean value);

   void setModified(DateTime value);

   void setOwner(String value);

   void setVersion(Integer value);

   void setShared(Boolean value);

   void setAccessed(DateTime value);

   void setBytes(Long value);

   void setMetadata(URI value);

   void setParent(URI value);

   void setTags(URI value);

}