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
package org.jclouds.joyent.sdc.v6_5.domain;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * Listing of a package.
 * 
 * @author Gerald Pereira
 * @see <a href= "http://apidocs.joyent.com/sdcapidoc/cloudapi/#machines" />
 */
public class Package implements Comparable<Package> {


	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String name;
		private int memorySizeMb;
		private int diskSizeGb;
		private int swapSizeMb;
		private boolean defaultPackage;

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder memorySizeMb(int memorySizeMb) {
			this.memorySizeMb = memorySizeMb;
			return this;
		}

		public Builder diskSizeGb(int diskSizeGb) {
			this.diskSizeGb = diskSizeGb;
			return this;
		}

		public Builder swapSizeMb(int swapSizeMb) {
			this.swapSizeMb = swapSizeMb;
			return this;
		}

		public Builder isDefault(boolean defaultPackage) {
			this.defaultPackage = defaultPackage;
			return this;
		}


		public Package build() {
			return new Package(name, memorySizeMb,
					diskSizeGb, swapSizeMb, defaultPackage);
		}

		public Builder fromPackage(Package in) {
			return name(in.getName()).memorySizeMb(
					in.getMemorySizeMb()).diskSizeGb(in.getDiskSizeGb()).swapSizeMb(in.getSwapSizeMb()).isDefault(in.isDefault());
		}
	}

	// The "friendly" name for this machine
	protected final String name;
	// The amount of memory this package has (Mb)
	@SerializedName("memory")
	protected final int memorySizeMb;
	// The amount of disk this package has (Gb)
	@SerializedName("disk")
	protected final int diskSizeGb;
	// The amount of swap this package has (Gb)
	@SerializedName("swap")
	protected final int swapSizeMb;
	// Whether this is the default package in this datacenter
	@SerializedName("default")
	protected final boolean defaultPackage;

	@Override
	public int compareTo(Package other) {
		return name.compareTo(other.getName());
	}

	public Package(String name, int memorySizeMb, int diskSizeGb,
			int swapSizeMb, boolean defaultPackage) {
		super();
		this.name = name;
		this.memorySizeMb = memorySizeMb;
		this.diskSizeGb = diskSizeGb;
		this.swapSizeMb = swapSizeMb;
		this.defaultPackage = defaultPackage;
	}

	public String getName() {
		return name;
	}

	public int getMemorySizeMb() {
		return memorySizeMb;
	}

	public int getDiskSizeGb() {
		return diskSizeGb;
	}

	public int getSwapSizeMb() {
		return swapSizeMb;
	}

	public boolean isDefault() {
		return defaultPackage;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object instanceof Package) {
			return Objects.equal(name, ((Package) object).name);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name);
	}

	@Override
	public String toString() {
		return String
				.format(
						"[name=%s, memory=%s, disk=%s, swap=%s, default=%s]",
						name, memorySizeMb,
						diskSizeGb, swapSizeMb, defaultPackage);
	}
}
