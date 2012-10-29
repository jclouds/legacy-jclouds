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
import org.jclouds.vcloud.domain.internal.VDCImpl;

import com.google.inject.ImplementedBy;

/**
 * A vDC is a deployment environment for vApps. A Vdc element provides a user view of a vDC.
 * 
 * @author Adrian Cole
 */
@org.jclouds.vcloud.endpoints.VDC
@ImplementedBy(VDCImpl.class)
public interface VDC extends ReferenceType {
   /**
    * Reference to the org containing this vDC.
    * 
    * @since vcloud api 1.0
    * @return org, or null if this is a version before 1.0 where the org isn't present
    */
   ReferenceType getOrg();

   /**
    * The creation status of the vDC
    * 
    * @since vcloud api 1.0
    */
   VDCStatus getStatus();

   /**
    * optional description
    * 
    * @since vcloud api 0.8
    */
   @Nullable
   String getDescription();

   /**
    * read‐only container for Task elements. Each element in the container represents a queued,
    * running, or failed task owned by this object.
    * 
    * @since vcloud api 1.0
    */
   List<Task> getTasks();

   /**
    * defines how resources are allocated by the vDC. The value of this element is set by the
    * administrator who created the vDC. It is read‐only to users.
    * 
    * @since vcloud api 1.0
    */
   AllocationModel getAllocationModel();

   /**
    * defines the storage capacity available in the vDC
    * 
    * @since vcloud api 0.8
    * @return null if the provider doesn't support storage capacity
    */
   @Nullable
   Capacity getStorageCapacity();

   /**
    * reports CPU resource consumption in a vDC
    * 
    * @since vcloud api 0.8
    * @return null if the provider doesn't support cpu capacity
    */
   @Nullable
   Capacity getCpuCapacity();

   /**
    * reports memory resource consumption in a vDC
    * 
    * @since vcloud api 0.8
    * @return null if the provider doesn't support memory capacity
    */
   @Nullable
   Capacity getMemoryCapacity();

   /**
    * container for ResourceEntity elements
    * 
    * @since vcloud api 0.8
    */
   Map<String, ReferenceType> getResourceEntities();

   /**
    * container for OrgNetwork elements that represent organization networks contained by the vDC
    * 
    * @since vcloud api 0.8
    */
   Map<String, ReferenceType> getAvailableNetworks();

   /**
    * maximum number of virtual NICs allowed in this vDC. Defaults to 0, which specifies an
    * unlimited number.
    * 
    * @since vcloud api 1.0
    */
   int getNicQuota();

   /**
    * maximum number of OrgNetwork objects that can be deployed in this vDC. Defaults to 0, which
    * specifies an unlimited number.
    * 
    * @since vcloud api 1.0
    */
   int getNetworkQuota();

   /**
    * maximum number of virtual machines that can be deployed in this vDC. Defaults to 0, which
    * specifies an unlimited number.
    * 
    * @since vcloud api 0.8
    */
   int getVmQuota();

   /**
    * true if this vDC is enabled
    * 
    * @since vcloud api 1.0
    */
   boolean isEnabled();

}
