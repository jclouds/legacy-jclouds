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
package org.jclouds.gogrid.domain;

import com.google.common.primitives.Longs;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Represents any job in GoGrid system
 * (jobs include server creation, stopping, etc)
 *
 * @see <a href="http://wiki.gogrid.com/wiki/index.php/API:Job_(Object)" />
 * @author Oleksiy Yarmula
 */
public class Job implements Comparable<Job> {

    private long id;
    private Option command;
    @SerializedName("objecttype")
    private ObjectType objectType;
    @SerializedName("createdon")
    private Date createdOn;
    @SerializedName("lastupdatedon")
    private Date lastUpdatedOn;
    @SerializedName("currentstate")
    private JobState currentState;
    private int attempts;
    private String owner;
    private List<JobProperties> history;
    @SerializedName("detail") /*NOTE: as of Feb 28, 10,
                                      there is a contradiction b/w the name in
                                      documentation (details) and actual param
                                      name (detail)*/
    private Map<String, String> details;

    /**
     * A no-args constructor is required for deserialization
     */
    public Job() {
    }

    public Job(long id, Option command, ObjectType objectType,
               Date createdOn, Date lastUpdatedOn, JobState currentState,
               int attempts, String owner, List<JobProperties> history,
               Map<String, String> details) {
        this.id = id;
        this.command = command;
        this.objectType = objectType;
        this.createdOn = createdOn;
        this.lastUpdatedOn = lastUpdatedOn;
        this.currentState = currentState;
        this.attempts = attempts;
        this.owner = owner;
        this.history = history;
        this.details = details;
    }

    public long getId() {
        return id;
    }

    public Option getCommand() {
        return command;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Date getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public JobState getCurrentState() {
        return currentState;
    }

    public int getAttempts() {
        return attempts;
    }

    public String getOwner() {
        return owner;
    }

    public List<JobProperties> getHistory() {
        return history;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Job job = (Job) o;

        if (attempts != job.attempts) return false;
        if (id != job.id) return false;
        if (command != null ? !command.equals(job.command) : job.command != null) return false;
        if (createdOn != null ? !createdOn.equals(job.createdOn) : job.createdOn != null) return false;
        if (currentState != null ? !currentState.equals(job.currentState) : job.currentState != null) return false;
        if (details != null ? !details.equals(job.details) : job.details != null) return false;
        if (history != null ? !history.equals(job.history) : job.history != null) return false;
        if (lastUpdatedOn != null ? !lastUpdatedOn.equals(job.lastUpdatedOn) : job.lastUpdatedOn != null) return false;
        if (objectType != null ? !objectType.equals(job.objectType) : job.objectType != null) return false;
        if (owner != null ? !owner.equals(job.owner) : job.owner != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (command != null ? command.hashCode() : 0);
        result = 31 * result + (objectType != null ? objectType.hashCode() : 0);
        result = 31 * result + (createdOn != null ? createdOn.hashCode() : 0);
        result = 31 * result + (lastUpdatedOn != null ? lastUpdatedOn.hashCode() : 0);
        result = 31 * result + (currentState != null ? currentState.hashCode() : 0);
        result = 31 * result + attempts;
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (history != null ? history.hashCode() : 0);
        result = 31 * result + (details != null ? details.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Job o) {
        if(createdOn != null && o.getCreatedOn() != null)
            return Longs.compare(createdOn.getTime(), o.getCreatedOn().getTime());
        return Longs.compare(id, o.getId());
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", command=" + command +
                ", objectType=" + objectType +
                ", createdOn=" + createdOn +
                ", lastUpdatedOn=" + lastUpdatedOn +
                ", currentState=" + currentState +
                ", attempts=" + attempts +
                ", owner='" + owner + '\'' +
                ", history=" + history +
                ", details=" + details +
                '}';
    }
}
