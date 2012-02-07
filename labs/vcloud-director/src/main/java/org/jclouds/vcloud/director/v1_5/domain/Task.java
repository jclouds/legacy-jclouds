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
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.*;

import java.net.URI;
import java.util.Date;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Sets;

/**
 * Represents an asynchronous or long-running task in the vCloud environment.
 *
 * <pre>
 * &lt;xs:complexType name="TaskType"&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(namespace = NS, name = "Task")
public class Task extends EntityType<Task> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromTask(this);
   }

   public static class Builder extends EntityType.Builder<Task> {

      private Error error;
      private Org org;
      private Integer progress;
      private String status;
      private String operation;
      private String operationName;
      private Date startTime;
      private Date endTime;
      private Date expiryTime;

      /**
       * @see Task#getError()
       */
      public Builder error(Error error) {
         this.error = error;
         return this;
      }

      /**
       * @see Task#getOrg()
       */
      public Builder org(Org org) {
         this.org = org;
         return this;
      }

      /**
       * @see Task#getProgress()
       */
      public Builder progress(Integer progress) {
         this.progress = progress;
         return this;
      }

      /**
       * @see Task#getStatus()
       */
      public Builder status(String status) {
         this.status = status;
         return this;
      }

      /**
       * @see Task#getOperation()
       */
      public Builder operation(String operation) {
         this.operation = operation;
         return this;
      }

      /**
       * @see Task#getOperationName()
       */
      public Builder operationName(String operationName) {
         this.operationName = operationName;
         return this;
      }

      /**
       * @see Task#getStartTime()
       */
      public Builder startTime(Date startTime) {
         this.startTime = startTime;
         return this;
      }

      /**
       * @see Task#getEndTime()
       */
      public Builder endTime(Date endTime) {
         this.endTime = endTime;
         return this;
      }

      /**
       * @see Task#getExpiryTime()
       */
      public Builder expiryTime(Date expiryTime) {
         this.expiryTime = expiryTime;
         return this;
      }

      @Override
      public Task build() {
         Task task = new Task(href, name);
         task.setError(error);
         task.setOrg(org);
         task.setProgress(progress);
         task.setStatus(status);
         task.setOperation(operation);
         task.setOperationName(operationName);
         task.setStartTime(startTime);
         task.setEndTime(endTime);
         task.setExpiryTime(expiryTime);
         return task;
      }

      /**
       * @see EntityType#getName()
       */
      @Override
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see EntityType#getDescription()
       */
      @Override
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see EntityType#getId()
       */
      @Override
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see EntityType#getTasksInProgress()
       */
      @Override
      public Builder tasksInProgress(TasksInProgress tasksInProgress) {
         this.tasksInProgress = tasksInProgress;
         return this;
      }

      /**
       * @see ReferenceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see ReferenceType#getType()
       */
      @Override
      public Builder type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see ReferenceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return this;
      }

      /**
       * @see ReferenceType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         this.links.add(checkNotNull(link, "link"));
         return this;
      }

      @Override
      public Builder fromEntityType(EntityType<Task> in) {
         return Builder.class.cast(super.fromEntityType(in));
      }

      public Builder fromTask(Task in) {
         return fromEntityType(in)
	               .error(in.getError()).org(in.getOrg()).progress(in.getProgress()).status(in.getStatus())
	               .operation(in.getOperation()).operationName(in.getOperationName());
      }
   }

   private Task() {
      // For JAXB and builder use
   }

   private Task(URI href, String name) {
      super(href, name);
   }

   @XmlElement(namespace = NS, name = "Error")
   private Error error;
   @XmlElement(namespace = NS, name = "Organization")
   private Org org;
   @XmlElement(namespace = NS, name = "Progress")
   private Integer progress;
   @XmlElement(namespace = NS, name = "Owner")
   private Entity owner;
   @XmlElement(namespace = NS, name = "User")
   private Entity user;
   @XmlElement(namespace = NS, name = "Params")
   private Object params;
   @XmlAttribute(namespace = NS, name = "status")
   private String status;
   @XmlAttribute(namespace = NS, name = "operation")
   private String operation;
   @XmlAttribute(namespace = NS, name = "operationName")
   private String operationName;
   @XmlAttribute(namespace = NS, name = "startTime")
   private Date startTime;
   @XmlAttribute(namespace = NS, name = "endTime")
   private Date endTime;
   @XmlAttribute(namespace = NS, name = "expiryTime")
   private Date expiryTime;
   
   public Error getError() {
      return error;
   }

   public void setError(Error error) {
      this.error = error;
   }

   public Org getOrg() {
      return org;
   }

   public void setOrg(Org org) {
      this.org = org;
   }

   public Integer getProgress() {
      return progress;
   }

   public void setProgress(Integer progress) {
      this.progress = progress;
   }

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public String getOperation() {
      return operation;
   }

   public void setOperation(String operation) {
      this.operation = operation;
   }

   public String getOperationName() {
      return operationName;
   }

   public void setOperationName(String operationName) {
      this.operationName = operationName;
   }

   public Date getStartTime() {
      return startTime;
   }

   public void setStartTime(Date startTime) {
      this.startTime = startTime;
   }

   public Date getEndTime() {
      return endTime;
   }

   public void setEndTime(Date endTime) {
      this.endTime = endTime;
   }

   public Date getExpiryTime() {
      return expiryTime;
   }

   public void setExpiryTime(Date expiryTime) {
      this.expiryTime = expiryTime;
   }

   @Override
   public boolean equals(Object o) {
      if (!super.equals(o))
         return false;
      Task that = Task.class.cast(o);
      return super.equals(that) && equal(this.error, that.error) && equal(this.org, that.org) &&
            equal(this.progress, that.progress) && equal(this.status, that.status) &&
            equal(this.operation, that.operation) && equal(this.operationName, that.operationName) &&
            equal(this.startTime, that.startTime) && equal(this.endTime, that.endTime) &&
            equal(this.expiryTime, that.expiryTime);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + Objects.hashCode(error, org, progress, status, operation, operationName,
            startTime, endTime, expiryTime);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("error", error).add("org", org).add("progress", progress).add("status", status)
            .add("operation", operation).add("operationName", operationName).add("startTime", startTime)
            .add("endTime", endTime).add("expiryTime", expiryTime);
   }
}
