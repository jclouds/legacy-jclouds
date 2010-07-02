/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.ec2.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static org.jclouds.http.HttpUtils.addFormParamTo;

import org.jclouds.aws.ec2.domain.BlockDeviceMapping;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

/**
 * @author Oleksiy Yarmula
 */
public class BindBlockDeviceMappingToIndexedFormParams implements Binder {

   private final String deviceNamePattern = "BlockDeviceMapping.%d.DeviceName";
   private final String volumeIdPattern = "BlockDeviceMapping.%d.Ebs.VolumeId";
   private final String deleteOnTerminationPattern = "BlockDeviceMapping.%d.Ebs.DeleteOnTermination";

   public void bindToRequest(HttpRequest request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof BlockDeviceMapping,
               "this binder is only valid for BlockDeviceMapping");
      BlockDeviceMapping blockDeviceMapping = (BlockDeviceMapping) input;

      int amazonOneBasedIndex = 1; // according to docs, counters must start with 1
      for (String ebsBlockDeviceName : blockDeviceMapping.getEbsBlockDevices().keySet()) {
         for (RunningInstance.EbsBlockDevice ebsBlockDevice : blockDeviceMapping
                  .getEbsBlockDevices().get(ebsBlockDeviceName)) {

            // not null by contract
            addFormParamTo(request, format(volumeIdPattern, amazonOneBasedIndex), ebsBlockDevice
                     .getVolumeId());

            if (ebsBlockDeviceName != null) {
               addFormParamTo(request, format(deviceNamePattern, amazonOneBasedIndex),
                        ebsBlockDeviceName);
            }
            addFormParamTo(request, format(deleteOnTerminationPattern, amazonOneBasedIndex), String
                     .valueOf(ebsBlockDevice.isDeleteOnTermination()));

            amazonOneBasedIndex++;
         }
      }

   }

}
