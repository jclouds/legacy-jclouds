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
package org.jclouds.trmk.enterprisecloud.domain;

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Date;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.trmk.enterprisecloud.domain.internal.BaseResource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Adrian Cole
 * 
 */
@XmlRootElement(name = "Task")
public class Task extends BaseResource<Task> {
    @XmlEnum
    public static enum Status {
      /**
       * the task is queued for execution.
       */
      @XmlEnumValue("Queued")
      QUEUED,
      /**
       * the task is running.
       */
      @XmlEnumValue("Running")
      RUNNING,
      /**
       * the task failed.
       */
      @XmlEnumValue("Failed")
      FAILED,
      /**
       * the task completed successfully.
       */
      @XmlEnumValue("Success")
      SUCCESS,
      /**
       * the task completed successfully.
       */
      @XmlEnumValue("Complete")
      COMPLETE,
      /**
       * the task failed with an error.
       */
      @XmlEnumValue("Error")
      ERROR,
      /**
       * Status was not parsed by jclouds.
       */
      UNRECOGNIZED;

      public String value() {
         return UPPER_UNDERSCORE.to(UPPER_CAMEL, name());
      }

      @Override
      public String toString() {
         return value();
      }

      public static Status fromValue(String status) {
         try {
            return valueOf(UPPER_CAMEL.to(UPPER_UNDERSCORE, checkNotNull(status, "status")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromTask(this);
   }

   public static class Builder extends BaseResource.Builder<Task> {
      protected String operation;
      protected Status status;
      protected NamedResource impactedItem;
      protected Date startTime;
      protected Date completedTime;
      protected String notes;
      protected String errorMessage;
      protected NamedResource initiatedBy;

      /**
       * @see Task#getOperation
       */
      public Builder operation(String operation) {
         this.operation = operation;
         return this;
      }

      /**
       * @see Task#getStatus
       */
      public Builder status(Status status) {
         this.status = status;
         return this;
      }

      /**
       * @see Task#getImpactedItem
       */
      public Builder impactedItem(NamedResource impactedItem) {
         this.impactedItem = impactedItem;
         return this;
      }

      /**
       * @see Task#getStartTime
       */
      public Builder startTime(Date startTime) {
         this.startTime = startTime;
         return this;
      }

      /**
       * @see Task#getCompletedTime
       */
      public Builder completedTime(Date completedTime) {
         this.completedTime = completedTime;
         return this;
      }

      /**
       * @see Task#getNotes
       */
      public Builder notes(String notes) {
         this.notes = notes;
         return this;
      }

      /**
       * @see Task#getErrorMessage
       */
      public Builder errorMessage(String errorMessage) {
         this.errorMessage = errorMessage;
         return this;
      }

      /**
       * @see Task#getInitiatedBy
       */
      public Builder initiatedBy(NamedResource initiatedBy) {
         this.initiatedBy = initiatedBy;
         return this;
      }

      @Override
      public Task build() {
         return new Task(href, type, operation, status, impactedItem, startTime, completedTime, notes, errorMessage,
               initiatedBy);
      }

      public Builder fromTask(Task in) {
         return fromResource(in).operation(in.getOperation()).status(in.getStatus()).impactedItem(in.getImpactedItem())
               .startTime(in.getStartTime()).completedTime(in.getCompletedTime()).notes(in.getNotes())
               .errorMessage(in.getErrorMessage()).initiatedBy(in.getInitiatedBy());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(BaseResource<Task> in) {
         return Builder.class.cast(super.fromResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromAttributes(Map<String, String> attributes) {
         return Builder.class.cast(super.fromAttributes(attributes));
      }

   }

   @XmlElement(name = "Operation", required = true)
   protected String operation;

   @XmlElement(name = "Status", required = true)
   protected Status status;

   @XmlElement(name = "ImpactedItem", required = true)
   protected NamedResource impactedItem;

   @XmlElement(name = "StartTime", required = true)
   protected Date startTime;

   @XmlElement(name = "CompletedTime", required = false)
   protected Date completedTime;

   @XmlElement(name = "Notes", required = false)
   protected String notes;

   @XmlElement(name = "ErrorMessage", required = false)
   protected String errorMessage;

   @XmlElement(name = "InitiatedBy", required = true)
   protected NamedResource initiatedBy;

   public Task(URI href, String type, String operation, Status status, NamedResource impactedItem, Date startTime,
         @Nullable Date completedTime, @Nullable String notes, @Nullable String errorMessage, NamedResource initiatedBy) {
      super(href, type);
      this.operation = checkNotNull(operation, "operation");
      this.status = checkNotNull(status, "status");
      this.impactedItem = checkNotNull(impactedItem, "impactedItem");
      this.startTime = checkNotNull(startTime, "startTime");
      this.completedTime = completedTime;// null if Queued or Running
      this.notes = notes;
      this.errorMessage = errorMessage;
      this.initiatedBy = checkNotNull(initiatedBy, "initiatedBy");
   }

   protected Task() {
       //For JAXB
   }

   /**
    * 
    * 
    * @return name of action performed
    */
   public String getOperation() {
      return operation;
   }

   /**
    * 
    * 
    * @return the status of the task
    */
   public Status getStatus() {
      return status;
   }

   /**
    * 
    * @return the item acted upon
    */
   public NamedResource getImpactedItem() {
      return impactedItem;
   }

   /**
    * 
    * @return time action started
    */
   public Date getStartTime() {
      return startTime;
   }

   /**
    * 
    * @return time action completed, or null if Queued or Running
    */
   @Nullable
   public Date getCompletedTime() {
      return completedTime;
   }

   /**
    * @return notes on action
    */
   public String getNotes() {
      return notes;
   }

   /**
    * @return error message
    */
   public String getErrorMessage() {
      return errorMessage;
   }

   /**
    * 
    * @return the item acted upon
    */
   public NamedResource getInitiatedBy() {
      return initiatedBy;
   }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Task task = (Task) o;

        if (completedTime != null ? !completedTime.equals(task.completedTime) : task.completedTime != null)
            return false;
        if (errorMessage != null ? !errorMessage.equals(task.errorMessage) : task.errorMessage != null)
            return false;
        if (!impactedItem.equals(task.impactedItem)) return false;
        if (!initiatedBy.equals(task.initiatedBy)) return false;
        if (notes != null ? !notes.equals(task.notes) : task.notes != null)
            return false;
        if (!operation.equals(task.operation)) return false;
        if (!startTime.equals(task.startTime)) return false;
        if (status != task.status) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + operation.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + impactedItem.hashCode();
        result = 31 * result + startTime.hashCode();
        result = 31 * result + (completedTime != null ? completedTime.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + (errorMessage != null ? errorMessage.hashCode() : 0);
        result = 31 * result + initiatedBy.hashCode();
        return result;
    }

    @Override
    public String string() {
       return super.string()+", operation="+operation+", status="+status+
             ", impactedItem="+impactedItem+", startTime="+startTime+", completedTime="+completedTime+
             ", notes="+notes+", errorMessage="+errorMessage+", initiatedBy="+initiatedBy;
    }

}