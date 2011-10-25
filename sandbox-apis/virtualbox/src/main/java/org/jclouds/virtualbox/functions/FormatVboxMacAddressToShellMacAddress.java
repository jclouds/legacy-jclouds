/*
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

package org.jclouds.virtualbox.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * Transform a string representation of a mac address in a shell readable mac address
 * This should format virtualbox mac address xxyyzzaabbcc into a valid mac address for the different shells
 * i.e: bash - 
 * $ arp -an
 * ? (172.16.1.101) at 14:fe:b5:e2:fd:ba [ether] on eth0
 * 
 * @author Andrea Turli
 */
public class FormatVboxMacAddressToShellMacAddress implements Function<String, String> {
	private boolean isOsX;

	public FormatVboxMacAddressToShellMacAddress(boolean isOsX) {
		this.isOsX = isOsX;
	}

	@Override
	public String apply(String vboxMacAddress) {
		checkNotNull(vboxMacAddress);
		checkArgument(vboxMacAddress.length()==12);
		String macAddress = Joiner.on(":").join(Splitter.fixedLength(2).split(vboxMacAddress)).toLowerCase();
		if (isOsX) {
			macAddress = Joiner.on(":").join(Iterables.transform(Splitter.on(":").split(macAddress), new Function<String, String>() {
				@Override
				public String apply(String arg0) {
					if(arg0.equals("00")) return "0";
					if(arg0.startsWith("0")) return arg0.substring(1);

					return arg0;
				}

			}));
		}
		return macAddress;
	}
}
