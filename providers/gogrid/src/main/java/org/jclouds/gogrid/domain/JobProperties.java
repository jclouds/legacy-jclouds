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
package org.jclouds.gogrid.domain;

import com.google.common.primitives.Longs;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * State of a job.
 *
 * @see <a href="http://wiki.gogrid.com/wiki/index.php/API:Job_State_(Object)"/>
 *
 * @author Oleksiy Yarmula
 */
public class JobProperties implements Comparable<JobProperties> {

    private long id;
    @SerializedName("updatedon")
    private Date updatedOn;
    private JobState state;
    private String note;

    /**
     * A no-args constructor is required for deserialization
     */
    public JobProperties() {

    }

    public JobProperties(long id, Date updatedOn, JobState state, String note) {
        this.id = id;
        this.updatedOn = updatedOn;
        this.state = state;
        this.note = note;
    }

    public long getId() {
        return id;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public JobState getState() {
        return state;
    }

    public String getNote() {
        return note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobProperties jobState = (JobProperties) o;

        if (id != jobState.id) return false;
        if (note != null ? !note.equals(jobState.note) : jobState.note != null) return false;
        if (state != null ? !state.equals(jobState.state) : jobState.state != null) return false;
        if (updatedOn != null ? !updatedOn.equals(jobState.updatedOn) : jobState.updatedOn != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (updatedOn != null ? updatedOn.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (note != null ? note.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JobState{" +
                "id=" + id +
                ", updatedOn=" + updatedOn +
                ", state=" + state +
                ", note='" + note + '\'' +
                '}';
    }

    @Override
    public int compareTo(JobProperties o) {
        return Longs.compare(id, o.getId());
    }
}
