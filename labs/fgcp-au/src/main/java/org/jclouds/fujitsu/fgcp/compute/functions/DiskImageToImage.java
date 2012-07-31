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

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Image.Status;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.fujitsu.fgcp.domain.DiskImage;
import org.jclouds.location.suppliers.all.RegionToProviderOrJustProvider;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * Created by IntelliJ IDEA.
 * 
 * @author Dies Koper
 */
@Singleton
public class DiskImageToImage implements Function<DiskImage, Image> {

    private final DiskImageToOperatingSystem diskImageToOperatingSystem;
    private final RegionToProviderOrJustProvider regionSupplier;

    @Inject
    public DiskImageToImage(
            DiskImageToOperatingSystem diskImageToOperatingSystem,
            RegionToProviderOrJustProvider locationSupplier) {
        this.diskImageToOperatingSystem = checkNotNull(diskImageToOperatingSystem);
        this.regionSupplier = checkNotNull(locationSupplier, "locationProvider");
    }

    @Override
    public Image apply(DiskImage from) {
        ImageBuilder builder = new ImageBuilder();

        builder.ids(from.getId());
        builder.name(from.getName());
        builder.description(from.getDescription());
        builder.operatingSystem(diskImageToOperatingSystem.apply(from));
        builder.location(Iterables.getOnlyElement(regionSupplier.get()));
        builder.userMetadata(ImmutableMap.<String, String> of("registrant",
                from.getRegistrant(), "creator", from.getCreatorName()));
        // in fgcp, if the image is listed it is available
        builder.status(Status.AVAILABLE);

        return builder.build();
    }
}
