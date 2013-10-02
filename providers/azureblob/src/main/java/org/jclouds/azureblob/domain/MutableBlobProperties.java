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
package org.jclouds.azureblob.domain;

import java.net.URI;
import java.util.Date;
import java.util.Map;

import org.jclouds.azureblob.domain.internal.MutableBlobPropertiesImpl;
import org.jclouds.io.MutableContentMetadata;

import com.google.inject.ImplementedBy;

/**
 * 
 * @author Adrian Cole
 * 
 */
@ImplementedBy(MutableBlobPropertiesImpl.class)
public interface MutableBlobProperties extends BlobProperties {
   /**
    * @see ListableContainerProperties#getUrl
    */
   void setUrl(URI url);

   /**
    * @see ListableContainerProperties#getName
    */
   void setName(String name);
   /**
    * @see ListableContainerProperties#getContainer
    */
   void setContainer(String container);
   
   /**
    * @see ListableContainerProperties#getLastModified
    */
   void setLastModified(Date lastModified);

   /**
    * @see ListableContainerProperties#getETag
    */
   void setETag(String eTag);

   MutableContentMetadata getContentMetadata();

   void setContentMetadata(MutableContentMetadata md);

   /**
    * @see ListableContainerProperties#getMetadata
    */
   void setMetadata(Map<String, String> metadata);

}
