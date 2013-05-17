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
package org.jclouds.compute.domain;

import java.util.List;

import org.jclouds.compute.domain.internal.HardwareImpl;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.inject.ImplementedBy;

/**
 * Size of a node.
 * 
 * @author Adrian Cole
 */
@ImplementedBy(HardwareImpl.class)
public interface Hardware extends ComputeMetadata {

   /**
    * Amount of virtual or physical processors provided
    */
   List<? extends Processor> getProcessors();

   /**
    * Amount of RAM provided in MB (256M, 1740)
    */
   int getRam();
   
   /**
    * volumes associated with this.
    */
   List<? extends Volume> getVolumes();

   /**
    * Determines whether this size can run an image.
    */
   Predicate<Image> supportsImage();
   
   /**
    * @return hypervisor type, if this is a virtual machine and the type is known
    */
   @Nullable
   String getHypervisor();
}
