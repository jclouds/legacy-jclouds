/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.glesys.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * Represents the allowed arguments for a certain server resource type (such as
 * disksize, memorysize, cpucores, and transfer).
 * <p/>
 * This is a composite type consisting of both the set of allowed units for the
 * resource type as well as the cost per unit.
 * 
 * @see AllowedArgumentsForCreateServer
 * @author Peter Gardfjäll
 */
public class AllowedArguments {
	public static Builder builder() {
		return new Builder();
	}

	public Builder toBuilder() {
		return new Builder().fromAllowedArgument(this);
	}

	public static class Builder {

		protected Cost costPerUnit;
		protected Set<Integer> allowedUnits;

		/**
		 * @see AllowedArguments#getCostPerUnit()
		 */
		public Builder costPerUnit(Cost costPerUnit) {
			this.costPerUnit = costPerUnit;
			return this;
		}

		/**
		 * @see AllowedArguments#getAllowedUnits()
		 */
		public Builder allowedUnits(Set<Integer> allowedUnits) {
			this.allowedUnits = ImmutableSet.copyOf(checkNotNull(allowedUnits,
					"allowedUnits"));
			return this;
		}

		public Builder allowedUnits(Integer... allowedUnits) {
			return allowedUnits(ImmutableSet.copyOf(allowedUnits));
		}

		public AllowedArguments build() {
			return new AllowedArguments(this.costPerUnit, this.allowedUnits);
		}

		public Builder fromAllowedArgument(AllowedArguments in) {
			return this.costPerUnit(in.getCostPerUnit()).allowedUnits(
					in.getAllowedUnits());
		}
	}

	private final Cost costPerUnit;
	private final Set<Integer> allowedUnits;

	@ConstructorProperties({ "costperunit", "units" })
	protected AllowedArguments(Cost costPerUnit, Set<Integer> units) {
		this.costPerUnit = checkNotNull(costPerUnit, "costPerUnit");
		this.allowedUnits = ImmutableSet.copyOf(checkNotNull(units,
				"allowedUnits"));
	}

	/**
	 * @return the cost per unit.
	 */
	public Cost getCostPerUnit() {
		return this.costPerUnit;
	}

	/**
	 * @return the set of allowed units for the resource type.
	 */
	public Set<Integer> getAllowedUnits() {
		return this.allowedUnits;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.costPerUnit, this.allowedUnits);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		AllowedArguments that = AllowedArguments.class.cast(obj);
		return Objects.equal(this.costPerUnit, that.costPerUnit)
				&& Objects.equal(this.allowedUnits, that.allowedUnits);
	}

	protected ToStringHelper string() {
		return Objects.toStringHelper("").add("costPerUnit", this.costPerUnit)
				.add("allowedUnits", this.allowedUnits);
	}

	@Override
	public String toString() {
		return string().toString();
	}
}
