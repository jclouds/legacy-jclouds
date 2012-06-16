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
package org.jclouds.joyent.sdc.v6_5.compute.functions;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OperatingSystem.Builder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.util.ComputeServiceUtils;
import org.jclouds.joyent.sdc.v6_5.domain.Dataset;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

/**
 * A function for transforming a sdc specific Dataset into a generic
 * OperatingSystem object.
 * 
 * @author Adrian Cole
 */
public class DatasetToOperatingSystem implements Function<Dataset, OperatingSystem> {
   public static final Pattern DEFAULT_PATTERN = Pattern.compile("(([^ ]*) ([0-9.]+) ?.*)");
   // Windows Machine 2008 R2 x64
   public static final Pattern WINDOWS_PATTERN = Pattern.compile("Windows (.*) (x[86][64])");

   @javax.annotation.Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Map<OsFamily, Map<String, String>> osVersionMap;

   @Inject
   public DatasetToOperatingSystem(Map<OsFamily, Map<String, String>> osVersionMap) {
      this.osVersionMap = osVersionMap;
   }

   public OperatingSystem apply(Dataset from) {
      Builder builder = OperatingSystem.builder();
      builder.name(from.getName());
      builder.description(from.getUrn());
      builder.is64Bit(true);//TODO: verify

      List<String> pieces = ImmutableList.copyOf(Splitter.on(':').split(from.getUrn()));
      if (pieces.get(2).indexOf('-') != -1) {
         List<String> osFamVersion = ImmutableList.copyOf(Splitter.on('-').split(pieces.get(2)));
         OsFamily family = OsFamily.fromValue(osFamVersion.get(0));
         builder.family(family);
         if (family != OsFamily.UNRECOGNIZED)
            builder.version(ComputeServiceUtils.parseVersionOrReturnEmptyString(family, osFamVersion.get(1),
                  osVersionMap));
      } else {
         builder.family(OsFamily.fromValue(pieces.get(2)));
      }
      return builder.build();
   }

}
