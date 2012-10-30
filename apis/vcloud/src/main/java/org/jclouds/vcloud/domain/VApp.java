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
import org.jclouds.vcloud.domain.ovf.VCloudNetworkSection;

/**
 * A VApp is the result of instantiation of a {@link VAppTemplate}. <h2>note</h2>
 * <p/>
 * When the {@link #getStatus} is {@link Status#UNRESOLVED}, there will be a task present for the
 * instantiation of the VApp.
 * 
 * @author Adrian Cole
 */
public interface VApp extends ReferenceType {
   /**
    * Reference to the vdc containing this vApp.
    * 
    * @since vcloud api 1.0
    * @return vdc, or null if this is a version before 1.0 where the org isn't present
    */
   ReferenceType getVDC();

   /**
    * The creation status of the vDC
    * 
    * @since vcloud api 1.0
    */
   Status getStatus();

   /**
    * optional description
    * 
    * @since vcloud api 0.8
    */
   @Nullable
   String getDescription();

   /**
    * 
    * @return true if the OVF descriptor for the template has been uploaded to the containing vDC.
    * @since vcloud api 1.0
    */
   boolean isOvfDescriptorUploaded();

   /**
    * read‐only container for Task elements. Each element in the container represents a queued,
    * running, or failed task owned by this object.
    * 
    * @since vcloud api 1.0
    */
   List<Task> getTasks();

   /**
    * container for Vm elements representing virtual machines
    * 
    * @since vcloud api 1.0
    */
   Set<Vm> getChildren();

   /**
    * description of the predefined vApp internal networks in this template
    * 
    * @return null if the vApp is not yet instantiated
    * @since vcloud api 1.0
    */
   @Nullable
   VCloudNetworkSection getNetworkSection();
}
