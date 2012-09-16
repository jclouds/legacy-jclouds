/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.compute.functions;

import javax.inject.Singleton;

import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplate;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.compute.predicates.ImagePredicates;

import com.google.common.base.Function;

/**
 * Transforms a {@link VirtualMachineTemplate} into an {@link Hardware}.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class VirtualMachineTemplateToHardware implements Function<VirtualMachineTemplate, Hardware>
{
    /** The default core speed, 2.0Ghz. */
    public static final double DEFAULT_CORE_SPEED = 2.0;

    @Override
    public Hardware apply(final VirtualMachineTemplate template)
    {
        HardwareBuilder builder = new HardwareBuilder();
        builder.ids(template.getId().toString());
        builder.uri(template.getURI());

        builder.name(template.getName());
        builder.processor(new Processor(template.getCpuRequired(), DEFAULT_CORE_SPEED));
        builder.ram(template.getRamRequired());

        // Currently we consider each template as a hardware profile
        builder.supportsImage(ImagePredicates.idEquals(template.getId().toString()));

        VolumeBuilder volumeBuilder = new VolumeBuilder();
        volumeBuilder.bootDevice(true);
        volumeBuilder.size(toGb(template.getHdRequired()));
        volumeBuilder.type(Volume.Type.LOCAL);
        volumeBuilder.durable(false);
        builder.volume(volumeBuilder.build());

        return builder.build();
    }

    private static float toGb(final long bytes)
    {
        return bytes / 1024 / 1024 / (float) 1024;
    }
}
