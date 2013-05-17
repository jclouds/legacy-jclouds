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
package org.jclouds.hpcloud.compute;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.location.Zone;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.compute.NovaComputeServiceAdapter;
import org.jclouds.openstack.nova.v2_0.compute.functions.RemoveFloatingIpFromNodeAndDeallocate;
import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ImageInZone;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndName;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
public class HPCloudComputeServiceAdapter extends NovaComputeServiceAdapter {

   @Inject
   public HPCloudComputeServiceAdapter(NovaApi novaApi, @Zone Supplier<Set<String>> zoneIds,
            RemoveFloatingIpFromNodeAndDeallocate removeFloatingIpFromNodeAndDeallocate, LoadingCache<ZoneAndName, KeyPair> keyPairCache) {
      super(novaApi, zoneIds, removeFloatingIpFromNodeAndDeallocate, keyPairCache);
   }

   @Override
   public Iterable<ImageInZone> listImages() {
      return Iterables.filter(super.listImages(), new Predicate<ImageInZone>() {

         @Override
         public boolean apply(ImageInZone arg0) {
            String imageName = arg0.getImage().getName();
            return imageName.indexOf("Kernel") == -1 && imageName.indexOf("Ramdisk") == -1;
         }

         @Override
         public String toString() {
            return "notKernelOrRamdisk";
         }
      });
   }
}
