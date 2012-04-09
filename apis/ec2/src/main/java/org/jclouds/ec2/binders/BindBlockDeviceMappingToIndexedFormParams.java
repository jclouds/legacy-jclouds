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
package org.jclouds.ec2.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.utils.ModifyRequest;
import org.jclouds.rest.Binder;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @author Oleksiy Yarmula
 * @author Adrian Cole
 */
public class BindBlockDeviceMappingToIndexedFormParams implements Binder {

   private static final String deviceNamePattern = "BlockDeviceMapping.%d.DeviceName";
   private static final String volumeIdPattern = "BlockDeviceMapping.%d.Ebs.VolumeId";
   private static final String deleteOnTerminationPattern = "BlockDeviceMapping.%d.Ebs.DeleteOnTermination";

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Map, "this binder is only valid for Map");
      Map<String, BlockDevice> blockDeviceMapping = (Map<String, BlockDevice>) input;

      ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
      int amazonOneBasedIndex = 1; // according to docs, counters must start with 1
      for (Entry<String, BlockDevice> ebsBlockDeviceName : blockDeviceMapping.entrySet()) {
         // not null by contract
         builder.put(format(volumeIdPattern, amazonOneBasedIndex), ebsBlockDeviceName.getValue().getVolumeId());
         builder.put(format(deviceNamePattern, amazonOneBasedIndex), ebsBlockDeviceName.getKey());
         builder.put(format(deleteOnTerminationPattern, amazonOneBasedIndex),
               String.valueOf(ebsBlockDeviceName.getValue().isDeleteOnTermination()));

         amazonOneBasedIndex++;
      }
      Multimap<String, String> forms = Multimaps.forMap(builder.build());
      return forms.size() == 0 ? request : ModifyRequest.putFormParams(request, forms);
   }

}
