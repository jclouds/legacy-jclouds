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
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIOXMLNS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;

import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents an asynchronous or long-running task in the vCloud environment.
 * <p/>
 * <pre>
 * &lt;xs:complexType name="TaskType"&gt;
 * </pre>
 *
 *  TODO: this object and the hierarchy is wrong.  it is literally a Task with a Task container.  please review class diagram
 * 
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "Task")
public class Task extends EntityType<Task> {

   public static final String MEDIA_TYPE = VCloudDirectorMediaType.TASK;

   public static class Status {
      public static final String QUEUED = "queued";
      public static final String PRE_RUNNING = "preRunning";
      public static final String RUNNING = "running";
      public static final String SUCCESS = "success";
      public static final String ERROR = "error";
      public static final String CANCELED = "canceled";
      public static final String ABORTED = "aborted";

      public static final List<String> ALL = Arrays.asList(
            QUEUED, PRE_RUNNING, RUNNING, SUCCESS,
            ERROR, CANCELED, ABORTED
      );
   }

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromTask(this);
   }

   public static class Builder extends EntityType.Builder<Task> {

      private Error error;
      private Reference org;
      private Reference owner;
      private Reference user;
      private Object params;
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
      public Builder org(Reference org) {
         this.org = org;
         return this;
      }

      /**
       * @see Task#getOwner()
       */
      public Builder owner(Reference owner) {
         this.owner = owner;
         return this;
      }

      /**
       * @see Task#getUser()
       */
      public Builder user(Reference user) {
         this.user = user;
         return this;
      }

      /**
       * @see Task#getParams()
       */
      public Builder params(Object params) {
         this.params = params;
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
         return new Task(href, type, links, description, tasks, id, name,
               error, org, progress, owner, user, params, status, operation, operationName, startTime, endTime, expiryTime);
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
       * @see EntityType#getTasks()
       */
      @Override
      public Builder tasks(Set<Task> tasks) {
         super.tasks(tasks);
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
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         return Builder.class.cast(super.links(links));
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         return Builder.class.cast(super.link(link));
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

   public Task(URI href, String type, Set<Link> links, String description, Set<Task> tasks,
               String id, String name, Error error, Reference org, Integer progress, Reference owner,
               Reference user, Object params, String status, String operation, String operationName,
               Date startTime, Date endTime, Date expiryTime) {
      super(href, type, links, description, tasks, id, name);
      this.error = error;
      this.org = org;
      this.progress = progress;
      this.owner = owner;
      this.user = user;
      this.params = params;
      this.status = status;
      this.operation = operation;
      this.operationName = operationName;
      this.startTime = startTime;
      this.endTime = endTime;
      this.expiryTime = expiryTime;
   }

   private Task() {
      // for JAXB
   }


   @XmlElement(name = "Error")
   private Error error;
   @XmlElement(name = "Organization")
   private Reference org;
   @XmlElement(name = "Progress")
   private Integer progress;
   @XmlElement(name = "Owner")
   private Reference owner;
   @XmlElement(name = "User")
   private Reference user;
   @XmlElement(name = "Params")
   private Object params;
   @XmlAttribute
   private String status;
   @XmlAttribute
   private String operation;
   @XmlAttribute
   private String operationName;
   @XmlAttribute
   private Date startTime;
   @XmlAttribute
   private Date endTime;
   @XmlAttribute
   private Date expiryTime;

   /**
    * Represents an error information if the task failed.
    */
   public Error getError() {
      return error;
   }

   /**
    * The organization that started the task.
    */
   public Reference getOrg() {
      return org;
   }

   /**
    * Reference to the owner of the task.
    */
   public Reference getOwner() {
      return owner;
   }

   /**
    * The user who started the task.
    */
   public Reference getUser() {
      return user;
   }

   /**
    * The parameters with which this task has been run.
    */
   public Object getParams() {
      return params;
   }

   /**
    * The progress of a long running asynchronous task.
    * <p/>
    * The value is between 0 - 100. Not all tasks have progress, the value is not
    * present for task which progress is not available.
    */
   public Integer getProgress() {
      return progress;
   }

   /**
    * The execution status of the task.
    * <p/>
    * One of:
    * <ul>
    * <li>queued - The task has been queued for execution.
    * <li>preRunning - The task is awaiting preprocessing or, if it is a blocking task, administrative action.
    * <li>running - The task is runnning.
    * <li>success - The task completed with a status of success.
    * <li>error - The task encountered an error while running.
    * <li>canceled - The task was canceled by the owner or an administrator.
    * <li>aborted - The task was aborted by an administrative action.
    * </ul>
    */
   // TODO: enum!!!
   public String getStatus() {
      return status;
   }

   /**
    * The display name of the operation that is tracked by this task.
    */
   public String getOperation() {
      return operation;
   }

   /**
    * The name of the operation that is tracked by this task.
    */
   public String getOperationName() {
      return operationName;
   }

   /**
    * The date and time the system started executing the task.
    * <p/>
    * May not be present if the task hasn't been executed yet.
    */
   public Date getStartTime() {
      return startTime;
   }

   /**
    * The date and time that processing of the task was completed.
    * <p/>
    * May not be present if the task is still being executed.
    */
   public Date getEndTime() {
      return endTime;
   }

   /**
    * The date and time at which the task resource will be destroyed and no longer available for retrieval.
    * <p/>
    * May not be present if the task has not been executed or is still being executed.
    */
   public Date getExpiryTime() {
      return expiryTime;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Task that = Task.class.cast(o);
      return super.equals(that) &&
            equal(this.error, that.error) && equal(this.org, that.org) &&
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
