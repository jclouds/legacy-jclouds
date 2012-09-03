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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.util.ComputeServiceUtils;
import org.jclouds.fujitsu.fgcp.domain.DiskImage;

import com.google.common.base.Function;

/**
 * 
 * @author Dies Koper
 */
@Singleton
public class DiskImageToOperatingSystem implements
        Function<DiskImage, OperatingSystem> {

    private static final Pattern OS_VERSION_PATTERN = Pattern
            .compile("^.*?(\\d.*)\\s(32|64).*$");

    @Override
    public OperatingSystem apply(DiskImage image) {
        checkNotNull(image, "disk image");

        // convert to short name rhel to accommodate ComputeServiceUtils
        // conventions
        String shortOsName = image.getOsName().replace(
                "Red Hat Enterprise Linux", "rhel");
        OsFamily osFamily = ComputeServiceUtils
                .parseOsFamilyOrUnrecognized(shortOsName);
        OperatingSystem.Builder builder = OperatingSystem.builder();

        builder.name(image.getOsName());
        builder.family(osFamily);
        builder.is64Bit(image.getOsName().contains("64bit")
                || image.getOsName().contains("64 bit")
                || image.getOsName().contains("x64"));
        // OsType returns guest type (hvm, pv), which aws-ec2 is mapping to arch
        builder.arch(image.getOsType());
        Matcher m = OS_VERSION_PATTERN.matcher(image.getOsName());
        if (m.matches()) {
            builder.version(m.group(1));
        }
        builder.description(image.getOsName());

        return builder.build();
    }
}
