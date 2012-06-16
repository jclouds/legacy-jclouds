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
package org.jclouds.snia.cdmi.v1.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * The base type for all objects in the CDMI model.
 * 
 * @author Kenneth Nagin
 */
public class CDMIObject {

	public static Builder<?> builder() {
		return new ConcreteBuilder();
	}

	public Builder<?> toBuilder() {
		return builder().fromCDMIObject(this);
	}

	private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
	}

	public static abstract class Builder<B extends Builder<B>> {
		private String objectID;
		private String objectType;
		private String objectName;

		@SuppressWarnings("unchecked")
		protected B self() {
			return (B) this;
		}

		/**
		 * @see CDMIObject#getObjectID()
		 */
		public B objectID(String objectID) {
			this.objectID = objectID;
			return self();
		}

		/**
		 * @see CDMIObject#getObjectType()
		 */
		public B objectType(String objectType) {
			this.objectType = objectType;
			return self();
		}

		/**
		 * @see CDMIObject#getObjectName()
		 */
		public B objectName(String objectName) {
			this.objectName = objectName;
			return self();
		}

		public CDMIObject build() {
			return new CDMIObject(this);
		}

		protected B fromCDMIObject(CDMIObject in) {
			return objectID(in.getObjectID()).objectType(in.getObjectType())
					.objectName(in.getObjectName());
		}
	}

	private final String objectID;
	private final String objectType;
	private final String objectName;

	protected CDMIObject(Builder<?> builder) {
		this.objectID = checkNotNull(builder.objectID, "objectID");
		this.objectType = checkNotNull(builder.objectType, "objectType");
		this.objectName = builder.objectName;
	}

	/**
	 * Object ID of the object <br/>
	 * Every object stored within a CDMI-compliant system shall have a globally
	 * unique object identifier (ID) assigned at creation time. The CDMI object
	 * ID is a string with requirements for how it is generated and how it
	 * obtains its uniqueness. Each offering that implements CDMI is able to
	 * produce these identifiers without conflicting with other offerings.
	 * 
	 * note: CDMI Servers do not always support ObjectID tags, however
	 * downstream jclouds code does not handle null so we return a empty String
	 * instead.
	 */
	public String getObjectID() {
		return (objectID == null) ? "" : objectID;
	}

	/**
	 * 
	 * type of the object
	 */
	public String getObjectType() {
		return objectType;
	}

	/**
	 * For objects in a container, the objectName field shall be returned. For
	 * objects not in a container (objects that are only accessible by ID), the
	 * objectName field shall not be returned.
	 * 
	 * Name of the object
	 */
	@Nullable
	public String getObjectName() {
		return (objectName == null) ? "" : objectName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CDMIObject that = CDMIObject.class.cast(o);
		return equal(this.objectID, that.objectID)
				&& equal(this.objectName, that.objectName)
				&& equal(this.objectType, that.objectType);
	}

	public boolean clone(Object o) {
		if (this == o)
			return false;
		if (o == null || getClass() != o.getClass())
			return false;
		CDMIObject that = CDMIObject.class.cast(o);
		return equal(this.objectType, that.objectType);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(objectID, objectName, objectType);
	}

	@Override
	public String toString() {
		return string().toString();
	}

	protected ToStringHelper string() {
		return Objects.toStringHelper("").add("objectID", objectID)
				.add("objectName", objectName).add("objectType", objectType);
	}
}
