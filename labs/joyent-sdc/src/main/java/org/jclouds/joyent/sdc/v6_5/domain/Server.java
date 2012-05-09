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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.Set;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;

/**
 * Listing of a server.
 * 
 * @author Gérald Pereira
 * @see <a href= "https://customer.glesys.com/api.php?a=doc#server_list" />
 */
public class Server implements Comparable<Server> {

	public static enum Type {
		VIRTUALMACHINE, SMARTMACHINE, UNRECOGNIZED;

		public static Type fromValue(String type) {
			try {
				return valueOf(CaseFormat.UPPER_CAMEL
						.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(type,
								"type")));
			} catch (IllegalArgumentException e) {
				return UNRECOGNIZED;
			}
		}

		public String value() {
			return (CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL,
					name()));
		}

		@Override
		public String toString() {
			return value();
		}
	}

	public static enum State {
		PUBLISHING, RUNNING, STOPPED, UNRECOGNIZED;

		public static State fromValue(String state) {
			try {
				return valueOf(CaseFormat.UPPER_CAMEL.to(
						CaseFormat.UPPER_UNDERSCORE, checkNotNull(state,
								"state")));
			} catch (IllegalArgumentException e) {
				return UNRECOGNIZED;
			}
		}

		public String value() {
			return (CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL,
					name()));
		}

		@Override
		public String toString() {
			return value();
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String id;
		private String name;
		private Type type;
		private State state;
		private String dataset;
		private int memorySizeMb;
		private int diskSizeGb;
		private Set<String> ips;
		private Date created;
		private Date updated;

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder type(Type type) {
			this.type = type;
			return this;
		}

		public Builder state(State state) {
			this.state = state;
			return this;
		}

		public Builder dataset(String dataset) {
			this.dataset = dataset;
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

		public Builder ips(Set<String> ips) {
			this.ips = ips;
			return this;
		}

		public Builder created(Date created) {
			this.created = created;
			return this;
		}

		public Builder updated(Date updated) {
			this.updated = updated;
			return this;
		}

		public Server build() {
			return new Server(id, name, type, state, dataset, memorySizeMb,
					diskSizeGb, ips, created, updated);
		}

		public Builder fromServer(Server in) {
			throw new UnsupportedOperationException("TODO");
		}
	}

	// {"id":"94eba336-ecb7-49f5-8a27-52f5e4dd57a1","name":"testJClouds","type":"virtualmachine","state":"running","dataset":"sdc:sdc:centos-5.7:1.2.1","ips":["37.153.96.62","10.224.0.63"],"memory":1024,"disk":61440,"metadata":{"root_authorized_keys":"ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEAq5iOv6RKLLTkBSMFjv3yinF+SPWLwCUqqqs0uJUcC2rqI3SQ5JsZLg+YsjYJAhrl6EDgb8hJppBo212X0J5DQICDEpc6NosE3rnml+U89xO/n02Y6qd2/RiK5e7sE2tW2geK8mJxo/I1onddssh6UloHh4TyNTljbhgJJ0BmTtrNPMoVVu4/YFZbHPaned/r4SHmrW9bpaTLSbqujN+QTuL+WsB0vMNh2A/eBO4aXJ2YtrzuhcDBUDo3CoQLxKdLy+2jBilFbZkXnc40FE3DAYxl2kVEGhLbhfz+7HJ/D5y73UyNd8+DI865O1xnt82/oANb7S6gXWG86EPgkV6HLQ== prodcloud@ns35814.ovh.net\n"},"created":"2012-05-09T13:32:46+00:00","updated":"2012-05-09T13:32:54+00:00"}"
	// Server [id=94eba336-ecb7-49f5-8a27-52f5e4dd57a1, name=testJClouds,
	// state=running, type=virtualmachine]

	// The globally unique id for this machine
	protected final String id;
	// The "friendly" name for this machine
	protected final String name;
	// Whether this is a smartmachine or virtualmachine
	protected final Type type;
	// The current state of this machine
	protected final State state;
	// The dataset urn this machine was provisioned with
	protected final String dataset;
	// The amount of memory this machine has (Mb)
	@SerializedName("memory")
	protected final int memorySizeMb;
	// The amount of disk this machine has (Gb)
	@SerializedName("disk")
	protected final int diskSizeGb;
	// The IP addresses this machine has
	protected final Set<String> ips;
	// Date (ISO8601) When this machine was created
	protected final Date created;
	// Date (ISO8601) When this machine was updated
	protected final Date updated;

	// metadata Object[String => String] Any "extra" metadata this machine has

	@Override
	public int compareTo(Server other) {
		return id.compareTo(other.getId());
	}

	public Server(String id, String name, Type type, State state,
			String dataset, int memorySizeMb, int diskSizeGb, Set<String> ips,
			Date created, Date updated) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.state = state;
		this.dataset = dataset;
		this.memorySizeMb = memorySizeMb;
		this.diskSizeGb = diskSizeGb;
		this.ips = ImmutableSet.<String> copyOf(ips);
		this.created = created;
		this.updated = updated;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public State getState() {
		return state;
	}

	public String getDataset() {
		return dataset;
	}

	public int getMemorySizeMb() {
		return memorySizeMb;
	}

	public int getDiskSizeGb() {
		return diskSizeGb;
	}

	public Set<String> getIps() {
		return ips;
	}

	public Date getCreated() {
		return created;
	}

	public Date getUpdated() {
		return updated;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object instanceof Server) {
			return Objects.equal(id, ((Server) object).id);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return String.format(
				"[id=%s, name=%s, type=%s, state=%s, memory=%s, disk=%s, ips=%s, created=%s, updated=%s]", id,
				name, type.name(), state.name(),memorySizeMb,diskSizeGb,ips,created,updated);
	}
}
