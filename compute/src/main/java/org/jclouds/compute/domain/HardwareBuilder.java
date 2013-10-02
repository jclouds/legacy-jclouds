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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import static org.jclouds.compute.predicates.ImagePredicates.any;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.jclouds.compute.domain.internal.HardwareImpl;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.domain.Location;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * 
 * @author Adrian Cole
 */
public class HardwareBuilder extends ComputeMetadataBuilder {
   protected List<Processor> processors = Lists.newArrayList();
   protected int ram;
   protected List<Volume> volumes = Lists.newArrayList();
   protected Predicate<Image> supportsImage = any();
   protected String hypervisor;

   public HardwareBuilder() {
      super(ComputeType.HARDWARE);
   }

   public HardwareBuilder processor(Processor processor) {
      this.processors.add(checkNotNull(processor, "processor"));
      return this;
   }

   public HardwareBuilder processors(Iterable<Processor> processors) {
      this.processors = ImmutableList.copyOf(checkNotNull(processors, "processors"));
      return this;
   }

   public HardwareBuilder ram(int ram) {
      this.ram = ram;
      return this;
   }

   public HardwareBuilder volume(Volume volume) {
      this.volumes.add(checkNotNull(volume, "volume"));
      return this;
   }

   public HardwareBuilder volumes(Iterable<Volume> volumes) {
      this.volumes = ImmutableList.copyOf(checkNotNull(volumes, "volumes"));
      return this;
   }

   public HardwareBuilder supportsImage(Predicate<Image> supportsImage) {
      this.supportsImage = checkNotNull(supportsImage, "supportsImage");
      return this;
   }

   public HardwareBuilder hypervisor(String hypervisor) {
      this.hypervisor = hypervisor;
      return this;
   }

   public HardwareBuilder is64Bit(boolean is64Bit) {
      supportsImage(is64Bit ? ImagePredicates.is64Bit() : not(ImagePredicates.is64Bit()));
      return this;
   }

   @Override
   public HardwareBuilder id(String id) {
      return HardwareBuilder.class.cast(super.id(id));
   }
   
   @Override
   public HardwareBuilder tags(Iterable<String> tags) {
      return HardwareBuilder.class.cast(super.tags(tags));
   }

   @Override
   public HardwareBuilder ids(String id) {
      return HardwareBuilder.class.cast(super.ids(id));
   }

   @Override
   public HardwareBuilder providerId(String providerId) {
      return HardwareBuilder.class.cast(super.providerId(providerId));
   }

   @Override
   public HardwareBuilder name(String name) {
      return HardwareBuilder.class.cast(super.name(name));
   }

   @Override
   public HardwareBuilder location(Location location) {
      return HardwareBuilder.class.cast(super.location(location));
   }

   @Override
   public HardwareBuilder uri(URI uri) {
      return HardwareBuilder.class.cast(super.uri(uri));
   }

   @Override
   public HardwareBuilder userMetadata(Map<String, String> userMetadata) {
      return HardwareBuilder.class.cast(super.userMetadata(userMetadata));
   }

   @Override
   public Hardware build() {
      return new HardwareImpl(providerId, name, id, location, uri, userMetadata, tags, processors, ram, volumes,
               supportsImage, hypervisor);
   }

   @SuppressWarnings("unchecked")
   public static HardwareBuilder fromHardware(Hardware in) {
      return new HardwareBuilder().id(in.getId()).providerId(in.getProviderId()).location(in.getLocation()).name(
               in.getName()).uri(in.getUri()).userMetadata(in.getUserMetadata()).tags(in.getTags()).processors(
               List.class.cast(in.getProcessors())).ram(in.getRam()).volumes(List.class.cast(in.getVolumes()))
               .supportsImage(in.supportsImage()).hypervisor(in.getHypervisor());
   }
}
