/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vcloud.domain;

import java.util.List;

import javax.annotation.Nullable;

import org.jclouds.vcloud.domain.internal.VmImpl;
import org.jclouds.vcloud.domain.ovf.VCloudOperatingSystem;

import com.google.inject.ImplementedBy;

/**
 * A Vm represents a virtual machine, a member of a vApp’s Children container. <h2>note</h2>
 * <p/>
 * When the {@link #getStatus} is {@link Status#UNRESOLVED}, there will be a task present for the
 * instantiation of the VApp.
 * 
 * @author Adrian Cole
 */
@ImplementedBy(VmImpl.class)
public interface Vm extends ReferenceType {
   /**
    * Reference to the {@link VApp} or {@link VAppTemplate} containing this vm.
    * 
    * @since vcloud api 1.0
    */
   ReferenceType getParent();

   /**
    * @return creation status of the Vm or null, if a part of a VAppTemplate
    * 
    * @since vcloud api 1.0
    */
   @Nullable
   Status getStatus();

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
    * @return virtual hardware that comprises this VM, or null, if part of a vApp template
    * 
    * @since vcloud api 1.0
    */
   @Nullable
   VCloudVirtualHardware getHardware();
   
   /**
    * @return operating system on this VM, or null, if part of a vApp template
    * 
    * @since vcloud api 1.0
    */
   @Nullable
   VCloudOperatingSystem getOperatingSystem();

   /**
    * read-only identifier created on import
    * 
    * @since vcloud api 1.0
    */
   @Nullable
   String getVAppScopedLocalId();
}