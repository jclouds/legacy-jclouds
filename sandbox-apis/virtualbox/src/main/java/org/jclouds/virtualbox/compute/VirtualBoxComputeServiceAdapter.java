/*
 * *
 *  * Licensed to jclouds, Inc. (jclouds) under one or more
 *  * contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  jclouds licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.jclouds.virtualbox.compute;

import com.google.inject.Singleton;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Credentials;
import org.jclouds.virtualbox.domain.VMSpec;
import org.jclouds.virtualbox.domain.Host;
import org.jclouds.virtualbox.domain.Image;
import org.virtualbox_4_1.IMachine;

import java.util.Collections;
import java.util.Map;

/**
 * Defines the connection between the {@link org.jclouds.virtualbox.VirtualBox} implementation and the jclouds
 * {@link org.jclouds.compute.ComputeService}
 *
 * @author Mattias Holmqvist
 */
@Singleton
public class VirtualBoxComputeServiceAdapter implements ComputeServiceAdapter<IMachine, VMSpec, Image, Host> {

   @Override
   public IMachine createNodeWithGroupEncodedIntoNameThenStoreCredentials(String tag, String name, Template template, Map<String, Credentials> credentialStore) {
      return null;
   }

   @Override
   public Iterable<VMSpec> listHardwareProfiles() {
      return Collections.emptyList();
   }

   @Override
   public Iterable<Image> listImages() {
      return Collections.emptyList();
   }

   @Override
   public Iterable<Host> listLocations() {
      return Collections.emptyList();
   }

   @Override
   public IMachine getNode(String id) {
      return null;
   }

   @Override
   public void destroyNode(String id) {

   }

   @Override
   public void rebootNode(String id) {

   }

   @Override
   public void resumeNode(String id) {

   }

   @Override
   public void suspendNode(String id) {

   }

   @Override
   public Iterable<IMachine> listNodes() {
      return Collections.emptyList();
   }
}
