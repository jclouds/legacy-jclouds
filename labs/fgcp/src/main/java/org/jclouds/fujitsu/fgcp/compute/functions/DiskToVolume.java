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
package org.jclouds.fujitsu.fgcp.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;

import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.fujitsu.fgcp.domain.Disk;

import com.google.common.base.Function;

/**
 * Created by IntelliJ IDEA.
 * 
 * @author Dies Koper
 */
@Singleton
public class DiskToVolume implements Function<Disk, Volume> {

   @Override
   public Volume apply(Disk disk) {
      checkNotNull(disk, "disk");

      VolumeBuilder builder = new VolumeBuilder();

      builder.size(1000f * Float.valueOf(disk.getSize()));
      // "Disk"'s are additional disks; they can't be booted disk(?)
      builder.bootDevice(false);
      builder.durable(true);
      builder.type(Volume.Type.SAN);
      builder.id("type: " + disk.getType() + " usage: " + disk.getUsage());

      return builder.build();
   }
}
