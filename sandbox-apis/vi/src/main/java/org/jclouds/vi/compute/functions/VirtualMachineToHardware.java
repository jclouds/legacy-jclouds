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

package org.jclouds.vi.compute.functions;

import java.util.List;

import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualDeviceBackingInfo;
import com.vmware.vim25.VirtualDisk;
import com.vmware.vim25.VirtualDiskFlatVer2BackingInfo;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * @author Adrian Cole
 */
@Singleton
public class VirtualMachineToHardware implements Function<VirtualMachine, Hardware> {

	@Override
	public Hardware apply(VirtualMachine from) {
		HardwareBuilder builder = new HardwareBuilder();

			builder.id(from.getMOR().get_value() + "");
			builder.providerId(from.getMOR().get_value() + "");
			builder.name(from.getName());
			List<Processor> processors = Lists.newArrayList();
			for (int i = 0; i < from.getConfig().getHardware().getNumCPU(); i++) {
				processors.add(new Processor(i + 1, 1));
			}
			builder.processors(processors);

			builder.ram((int) from.getConfig().getHardware().getMemoryMB());
			List<Volume> volumes = Lists.newArrayList();
			// look for volumes
			VirtualDevice[] devices = from.getConfig().getHardware().getDevice();
			for (VirtualDevice virtualDevice : devices) {
				if(virtualDevice.getDeviceInfo().getLabel().contains("Hard disk")) {
					if(virtualDevice instanceof VirtualDisk) {
						VirtualDisk disk = (VirtualDisk) virtualDevice;
						VirtualDeviceBackingInfo backingInfo = disk.getBacking();
						if(backingInfo instanceof VirtualDiskFlatVer2BackingInfo) {
							VirtualDiskFlatVer2BackingInfo diskFlatVer2BackingInfo = (VirtualDiskFlatVer2BackingInfo) backingInfo;
							volumes.add(new VolumeImpl(diskFlatVer2BackingInfo.getUuid(), Volume.Type.LOCAL, new Float(disk.getCapacityInKB() + ""), diskFlatVer2BackingInfo.getFileName(), true, false));
						}
					}
				}
			}
			builder.volumes((List<Volume>) volumes);
		return builder.build();
	}
	
	protected <T> T propagate(Exception e) {
		Throwables.propagate(e);
		assert false;
		return null;
	}
}