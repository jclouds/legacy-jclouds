/*
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

package org.jclouds.googlecompute.functions;

import com.google.inject.TypeLiteral;
import org.jclouds.googlecompute.domain.Disk;
import org.jclouds.googlecompute.domain.Firewall;
import org.jclouds.googlecompute.domain.Image;
import org.jclouds.googlecompute.domain.Instance;
import org.jclouds.googlecompute.domain.Kernel;
import org.jclouds.googlecompute.domain.MachineType;
import org.jclouds.googlecompute.domain.Network;
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.domain.PagedList;
import org.jclouds.googlecompute.domain.Snapshot;
import org.jclouds.googlecompute.domain.Zone;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;

import javax.inject.Inject;

/**
 * Boiler plate to specify the type of paged list.
 *
 * @author David Alves
 */
public class ParsePagedLists {

   public static class ParseOperations extends ParseJson<PagedList<Operation>> {

      @Inject
      public ParseOperations(Json json) {
         super(json, new TypeLiteral<PagedList<Operation>>() {});
      }
   }

   public static class ParseMachineTypes extends ParseJson<PagedList<MachineType>> {

      @Inject
      public ParseMachineTypes(Json json) {
         super(json, new TypeLiteral<PagedList<MachineType>>() {});
      }
   }

   public static class ParseImages extends ParseJson<PagedList<Image>> {

      @Inject
      public ParseImages(Json json) {
         super(json, new TypeLiteral<PagedList<Image>>() {});
      }
   }

   public static class ParseKernels extends ParseJson<PagedList<Kernel>> {

      @Inject
      public ParseKernels(Json json) {
         super(json, new TypeLiteral<PagedList<Kernel>>() {});
      }
   }

   public static class ParseDisks extends ParseJson<PagedList<Disk>> {

      @Inject
      public ParseDisks(Json json) {
         super(json, new TypeLiteral<PagedList<Disk>>() {});
      }
   }

   public static class ParseFirewalls extends ParseJson<PagedList<Firewall>> {

      @Inject
      public ParseFirewalls(Json json) {
         super(json, new TypeLiteral<PagedList<Firewall>>() {});
      }
   }

   public static class ParseZones extends ParseJson<PagedList<Zone>> {

      @Inject
      public ParseZones(Json json) {
         super(json, new TypeLiteral<PagedList<Zone>>() {});
      }
   }

   public static class ParseInstances extends ParseJson<PagedList<Instance>> {

      @Inject
      public ParseInstances(Json json) {
         super(json, new TypeLiteral<PagedList<Instance>>() {});
      }
   }

   public static class ParseNetworks extends ParseJson<PagedList<Network>> {

      @Inject
      public ParseNetworks(Json json) {
         super(json, new TypeLiteral<PagedList<Network>>() {});
      }
   }

   public static class ParseSnapshots extends ParseJson<PagedList<Snapshot>> {

      @Inject
      public ParseSnapshots(Json json) {
         super(json, new TypeLiteral<PagedList<Snapshot>>() {});
      }
   }
}
