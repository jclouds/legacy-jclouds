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
package org.jclouds.opsource.servers.domain;

import static com.google.common.base.Objects.equal;
import static org.jclouds.opsource.servers.OpSourceNameSpaces.SERVER;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

/**
 * 
 * @author Kedar Dave
 */
@XmlRootElement(namespace = SERVER, name = "status")
public class Status {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromStatus(this);
   }

   public static class Builder {

	  private String action;
	  private String requestTime;
	  private String userName;
	  private int numberOfSteps;
	  private String updateTime;
	  private Step step;
	  private String failureReason;
	  
      public Builder action(String action) {
    	  this.action = action;
    	  return this;
      }

      public Builder requestTime(String requestTime) {
    	  this.requestTime = requestTime;
    	  return this;
      }

      public Builder userName(String userName) {
    	  this.userName = userName;
    	  return this;
      }

      public Builder numberOfSteps(int numberOfSteps) {
    	  this.numberOfSteps = numberOfSteps;
    	  return this;
      }

      public Builder updateTime(String updateTime) {
    	  this.updateTime = updateTime;
    	  return this;
      }

      public Builder step(Step step) {
    	  this.step = step;
    	  return this;
      }

      public Builder failureReason(String failureReason) {
    	  this.failureReason = failureReason;
    	  return this;
      }

      public Status build() {
         return new Status(action, requestTime, userName, numberOfSteps, updateTime, step, failureReason);
      }

      public Builder fromStatus(Status in) {
         return new Builder().action(action).requestTime(requestTime).userName(userName).numberOfSteps(numberOfSteps)
         	.updateTime(updateTime).step(step).failureReason(failureReason);
      }
   }

   private Status() {
      // For JAXB and builder use
   }

   @XmlElement(namespace = SERVER, name="action")
   private String action;
   @XmlElement(namespace = SERVER, name="requestTime")
   private String requestTime;
   @XmlElement(namespace = SERVER, name="userName")
   private String userName;
   @XmlElement(namespace = SERVER, name="numberOfSteps")
   private int numberOfSteps;
   @XmlElement(namespace = SERVER, name="updateTime")
   private String updateTime;
   @XmlElement(namespace = SERVER, name="step")
   private Step step;
   @XmlElement(namespace = SERVER, name="failureReason")
   private String failureReason;

   private Status(String action, String requestTime, String userName, int numberOfSteps, String updateTime,
		   Step step, String failureReason) {
      this.action = action;
      this.requestTime = requestTime;
      this.userName = userName;
      this.numberOfSteps = numberOfSteps;
      this.updateTime = updateTime;
      this.step = step;
      this.failureReason = failureReason;
   }

   	public String getAction() {
   		return action;
   	}

	public String getRequestTime() {
		return requestTime;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public int getNumberOfSteps() {
		return numberOfSteps;
	}
	
	public String getUpdateTime() {
		return updateTime;
	}
	
	public Step getStep() {
		return step;
	}
	
	public String getFailureReason() {
		return failureReason;
	}
	
	@Override
   	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Status that = Status.class.cast(o);
		return equal(this, that);
	}

	@Override
	public int hashCode() {
      	return Objects.hashCode(action, requestTime, userName, numberOfSteps, updateTime, step, failureReason);
	}

	@Override
   	public String toString() {
		return Objects.toStringHelper("").add("action", action).add("requestTime", requestTime).add("userName", userName).
			add("numberOfSteps", numberOfSteps).add("updateTime", updateTime).add("step", step).add("failureReason", failureReason).toString();
	}

}
