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
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.domain.internal.VAppTemplateImpl;
import org.jclouds.vcloud.domain.ovf.VCloudNetworkSection;

import com.google.inject.ImplementedBy;

/**
 * A VAppTemplate is an abstract description of a vApp. It is created when you upload an OVF package
 * to a vDC.
 * 
 * @author Adrian Cole
 */
@ImplementedBy(VAppTemplateImpl.class)
public interface VAppTemplate extends ReferenceType {
   /**
    * Reference to the VDC containing this template.
    * 
    * @since vcloud api 1.0
    * @return org, or null if this is a version before 1.0 where the vdc isn't present
    */
   ReferenceType getVDC();

   /**
    * @return creation status of the VAppTemplate.
    * 
    * @since vcloud api 1.0
    */
   Status getStatus();

   /**
    * optional description
    * 
    * @since vcloud api 1.0
    */
   @Nullable
   String getDescription();

   /**
    * read-only container for Task elements. Each element in the container represents a queued,
    * running, or failed task owned by this object.
    * 
    * @since vcloud api 1.0
    */
   List<Task> getTasks();

   /**
    * 
    * @return true if the OVF descriptor for the template has been uploaded to the containing vDC.
    * @since vcloud api 1.0
    */
   boolean isOvfDescriptorUploaded();

   /**
    * read-only identifier created on import
    * 
    * @since vcloud api 1.0
    */
   @Nullable
   String getVAppScopedLocalId();

   /**
    * container for Vm elements representing virtual machines
    * 
    * @since vcloud api 1.0
    */
   Set<Vm> getChildren();

   /**
    * description of the predefined vApp internal networks in this template
    * 
    * @return null if the vAppTemplate is still copying
    * @since vcloud api 1.0
    */
   @Nullable
   VCloudNetworkSection getNetworkSection();
}