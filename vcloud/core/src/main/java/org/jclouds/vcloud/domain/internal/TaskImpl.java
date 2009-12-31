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
package org.jclouds.vcloud.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Date;

import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TaskStatus;

import com.google.inject.internal.Nullable;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class TaskImpl implements Task {
	private final String id;
	private final URI location;
	private final TaskStatus status;
	private final Date startTime;
	@Nullable
	private final Date endTime;
	private final NamedResource owner;
	@Nullable
	private final NamedResource result;

	public TaskImpl(String id, URI location, TaskStatus status, Date startTime,
			@Nullable Date endTime, NamedResource owner,
			@Nullable NamedResource result) {
		this.id = checkNotNull(id, "id");
		this.location = checkNotNull(location, "location");
		this.status = checkNotNull(status, "status");
		this.startTime = startTime;
		this.endTime = endTime;
		this.owner = owner;
		this.result = result;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public Date getStartTime() {
		return startTime;
	}

	public NamedResource getOwner() {
		return owner;
	}

	public NamedResource getResult() {
		return result;
	}

	public Date getEndTime() {
		return endTime;
	}

	public int compareTo(Task o) {
		return (this == o) ? 0 : getId().compareTo(o.getId());
	}

	public String getId() {
		return id;
	}

	public URI getLocation() {
		return location;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result
				+ ((this.result == null) ? 0 : this.result.hashCode());
		result = prime * result
				+ ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaskImpl other = (TaskImpl) obj;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (result == null) {
			if (other.result != null)
				return false;
		} else if (!result.equals(other.result))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TaskImpl [endTime=" + endTime + ", id=" + id + ", location="
				+ location + ", owner=" + owner + ", result=" + result
				+ ", startTime=" + startTime + ", status=" + status + "]";
	}

}