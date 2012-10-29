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

import java.util.List;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.domain.internal.CatalogImpl;

import com.google.inject.ImplementedBy;

/**
 * @author Adrian Cole
 */
@org.jclouds.vcloud.endpoints.Catalog
@ImplementedBy(CatalogImpl.class)
public interface Catalog extends ReferenceType, Map<String, ReferenceType> {
   /**
    * Reference to the org containing this vDC.
    * 
    * @since vcloud api 1.0
    * @return org, or null if this is a version before 1.0 where the org isn't present
    */
   ReferenceType getOrg();

   /**
    * optional description
    * 
    * @since vcloud api 0.8
    */
   @Nullable
   String getDescription();

   /**
    * read‐only element, true if the catalog is published
    * 
    * @since vcloud api 1.0
    */
   boolean isPublished();

   /**
    * @return true, if the current user cannot modify the catalog
    * @since vcloud api 1.0
    */
   boolean isReadOnly();

   /**
    * read‐only container for Task elements. Each element in the container represents a queued,
    * running, or failed task owned by this object.
    * 
    * @since vcloud api 1.0
    */
   List<Task> getTasks();
}
