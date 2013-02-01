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
package org.jclouds.joyent.cloudapi.v6_5.compute.functions;

import static org.jclouds.compute.domain.OsFamily.UNRECOGNIZED;
import static org.jclouds.compute.domain.OsFamily.fromValue;
import static org.jclouds.compute.util.ComputeServiceUtils.parseVersionOrReturnEmptyString;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OperatingSystem.Builder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.joyent.cloudapi.v6_5.domain.Dataset;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

/**
 * A function for transforming a cloudApi specific Dataset into a generic OperatingSystem object.
 * 
 * @author Adrian Cole
 */
public class DatasetToOperatingSystem implements Function<Dataset, OperatingSystem> {

   private final Map<OsFamily, Map<String, String>> osVersionMap;

   @Inject
   public DatasetToOperatingSystem(Map<OsFamily, Map<String, String>> osVersionMap) {
      this.osVersionMap = osVersionMap;
   }

   public OperatingSystem apply(Dataset from) {
      Builder builder = OperatingSystem.builder();
      builder.name(from.getName());
      builder.description(from.getUrn());
      builder.is64Bit(true);// TODO: verify
      String os = from.getOs();
      OsFamily family = UNRECOGNIZED;
      String version = "";
      if (os.compareTo("smartos") == 0) {
          family = fromValue(os);
          version = from.getVersion();
      }
      else {
          List<String> pieces = ImmutableList.copyOf(Splitter.on(':').split(from.getUrn()));
          if (pieces.get(2).indexOf('-') != -1) {
             List<String> osFamVersion = ImmutableList.copyOf(Splitter.on('-').split(pieces.get(2)));
             family = fromValue(osFamVersion.get(0));
             if (family != UNRECOGNIZED)
                version = osFamVersion.get(1);
          } else {
             family = fromValue(pieces.get(2));
          }
      }
      builder.family(family);
      if (family != UNRECOGNIZED)
         version = parseVersionOrReturnEmptyString(family, version, osVersionMap);
      if ("".equals(version))
         version = from.getVersion();
      builder.version(version);
      return builder.build();
   }
}
