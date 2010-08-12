/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.aws.ec2.functions;

import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.testng.annotations.Test;

import java.util.Map;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

/**
 * @author Oleksiy Yarmula
 */
public class RunningInstanceToStorageMappingUnixTest {

   @Test
   public void testMappingForM1LargeInstance() {
      RunningInstanceToStorageMappingUnix volumeMapping = new RunningInstanceToStorageMappingUnix();
      RunningInstance instance = createMock(RunningInstance.class);
      expect(instance.getInstanceType()).andStubReturn(InstanceType.M1_LARGE);
      replay(instance);

      Map<String, String> mappingReturned = volumeMapping.apply(instance);
      assert mappingReturned.size() == 3 : String.format(
              "Expected size of mapping devices: %d. Found: %d", 3, mappingReturned.size());

      assert mappingReturned.containsKey("disk_drive//dev/sda1/gb");
      assert mappingReturned.containsKey("disk_drive//dev/sdb/gb");
      assert mappingReturned.containsKey("disk_drive//dev/sdc/gb");
   }

   @Test
   public void testMappingForM1XLargeInstance() {
      RunningInstanceToStorageMappingUnix volumeMapping = new RunningInstanceToStorageMappingUnix();
      RunningInstance instance = createMock(RunningInstance.class);
      expect(instance.getInstanceType()).andStubReturn(InstanceType.M1_XLARGE);
      replay(instance);

      Map<String, String> mappingReturned = volumeMapping.apply(instance);
      assert mappingReturned.size() == 5 : String.format(
              "Expected size of mapping devices: %d. Found: %d", 5, mappingReturned.size());

      assert mappingReturned.containsKey("disk_drive//dev/sda1/gb");
      assert mappingReturned.containsKey("disk_drive//dev/sdb/gb");
      assert mappingReturned.containsKey("disk_drive//dev/sdc/gb");
      assert mappingReturned.containsKey("disk_drive//dev/sdd/gb");
      assert mappingReturned.containsKey("disk_drive//dev/sde/gb");
   }

}
