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
package org.jclouds.gogrid.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.primitives.Longs;

/**
 * State of a job.
 * 
 * @see <a href="http://wiki.gogrid.com/wiki/index.php/API:Job_State_(Object)"/>
 * @author Oleksiy Yarmula
*/
public class JobProperties implements Comparable<JobProperties> {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromJobProperties(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected long id;
      protected Date updatedOn;
      protected JobState state;
      protected String note;
   
      /** 
       * @see JobProperties#getId()
       */
      public T id(long id) {
         this.id = id;
         return self();
      }

      /** 
       * @see JobProperties#getUpdatedOn()
       */
      public T updatedOn(Date updatedOn) {
         this.updatedOn = updatedOn;
         return self();
      }

      /** 
       * @see JobProperties#getState()
       */
      public T state(JobState state) {
         this.state = state;
         return self();
      }

      /** 
       * @see JobProperties#getNote()
       */
      public T note(String note) {
         this.note = note;
         return self();
      }

      public JobProperties build() {
         return new JobProperties(id, updatedOn, state, note);
      }
      
      public T fromJobProperties(JobProperties in) {
         return this
                  .id(in.getId())
                  .updatedOn(in.getUpdatedOn())
                  .state(in.getState())
                  .note(in.getNote());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final long id;
   private final Date updatedOn;
   private final JobState state;
   private final String note;

   @ConstructorProperties({
      "id", "updatedon", "state", "note"
   })
   protected JobProperties(long id, Date updatedOn, JobState state, @Nullable String note) {
      this.id = id;
      this.updatedOn = checkNotNull(updatedOn, "updatedOn");
      this.state = checkNotNull(state, "state");
      this.note = note;
   }

   public long getId() {
      return this.id;
   }

   public Date getUpdatedOn() {
      return this.updatedOn;
   }

   public JobState getState() {
      return this.state;
   }

   @Nullable
   public String getNote() {
      return this.note;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, updatedOn, state, note);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      JobProperties that = JobProperties.class.cast(obj);
      return Objects.equal(this.id, that.id)
               && Objects.equal(this.updatedOn, that.updatedOn)
               && Objects.equal(this.state, that.state)
               && Objects.equal(this.note, that.note);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("updatedOn", updatedOn).add("state", state).add("note", note);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(JobProperties o) {
      return Longs.compare(id, o.getId());
   }
}
