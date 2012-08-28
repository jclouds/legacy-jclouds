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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.fujitsu.fgcp.domain.ServerType;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * Created by IntelliJ IDEA.
 * 
 * @author Dies Koper
 */
@Singleton
public class ServerTypeToHardware implements Function<ServerType, Hardware> {
    private final CPUToProcessor cpuToProcessor;
    private final DiskToVolume diskToVolume;

    @Inject
    public ServerTypeToHardware(CPUToProcessor cpuToProcessor,
            DiskToVolume diskToVolume) {
        this.cpuToProcessor = checkNotNull(cpuToProcessor);
        this.diskToVolume = checkNotNull(diskToVolume);
    }

    @Override
    public Hardware apply(ServerType from) {
        checkNotNull(from, "ServerType");
        HardwareBuilder builder = new HardwareBuilder();

        builder.ids(from.getId());
        builder.name(from.getName());
        builder.ram((int) (1000d * Double.valueOf(from.getMemory().getSize())));
        builder.processor(cpuToProcessor.apply(from.getCpu()));
        builder.supportsImage(Predicates.<Image> alwaysTrue());
        // all servers are 64bit. The OS however may be 32 bit.
        builder.is64Bit(true);
        builder.volumes(Iterables.transform(from.getDisks(), diskToVolume));

        return builder.build();
    }
}
