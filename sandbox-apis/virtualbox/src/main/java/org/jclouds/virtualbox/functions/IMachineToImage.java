/*
 * *
 *  * Licensed to jclouds, Inc. (jclouds) under one or more
 *  * contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  jclouds licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.jclouds.virtualbox.functions;

import java.util.NoSuchElementException;

import javax.inject.Inject;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.virtualbox.VirtualBox;
import org.virtualbox_4_1.IGuestOSType;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;


public class IMachineToImage implements Function<IMachine, Image> {

	private static final String UBUNTU  = "Ubuntu Linux";

	private VirtualBoxManager virtualboxManager;

	@Inject
	public IMachineToImage(VirtualBoxManager virtualboxManager) {
		this.virtualboxManager = virtualboxManager;
	}

	@Override
	public Image apply(@Nullable IMachine from) {

		Boolean is64Bit = virtualboxManager.getVBox().getGuestOSType(from.getOSTypeId()).getIs64Bit();

		//Somehow this method gets called with the correct product item.
		OsFamily family = osFamily().apply(from);
		OperatingSystem os = OperatingSystem.builder()
				.description(from.getDescription())
				.family(family)
				.version(osVersion().apply(from))
				.is64Bit(is64Bit)
				.build();

		return new ImageBuilder()
		.id("" + from.getId())
		.description(from.getDescription())
		.operatingSystem(os)
		.build();
	}

	/**
	 * Parses the item description to determine the OSFamily
	 * @return the @see OsFamily or OsFamily.UNRECOGNIZED
	 */
	public static Function<IMachine, OsFamily> osFamily() {
		return new Function<IMachine,OsFamily>() {
			@Override
			public OsFamily apply(IMachine iMachine) {
				final String description = iMachine.getDescription();
				if ( description.startsWith(UBUNTU) ) return OsFamily.UBUNTU;
				return OsFamily.UNRECOGNIZED;
			}
		};
	}

	 /**
	    * Parses the item description to determine the os version
	    * @return the version
	    * @throws java.util.NoSuchElementException if the version cannot be determined
	    */
	    public static Function<IMachine, String> osVersion() {
	       return new Function<IMachine, String>() {
	            @Override
	            public String apply(IMachine iMachine) {
	               final String description = iMachine.getDescription();
	               OsFamily family = osFamily().apply(iMachine);
	               if(family.equals(OsFamily.UBUNTU)) return parseVersion(description, UBUNTU);
	               else throw new NoSuchElementException("No os parseVersion for item:" + iMachine);
	            }
	        };
	    }
	
	    private static String parseVersion(String description, String os) {
	        String noOsName = description.replaceFirst(os,"").trim();
	        return noOsName.split(" ")[0];
	     }
}
