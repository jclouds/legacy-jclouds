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
package org.jclouds.rds.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;

/**
 * 
 * @author Adrian Cole
 */
public class BaseInstance {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromBaseInstance(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected Optional<String> name = Optional.absent();
      protected String instanceClass;
      protected String engine;
      protected String masterUsername;
      protected int allocatedStorageGB;

      /**
       * @see BaseInstance#getName()
       */
      public T name(String name) {
         this.name = Optional.fromNullable(name);
         return self();
      }

      /**
       * @see BaseInstance#getInstanceClass()
       */
      public T instanceClass(String instanceClass) {
         this.instanceClass = instanceClass;
         return self();
      }

      /**
       * @see BaseInstance#getEngine()
       */
      public T engine(String engine) {
         this.engine = engine;
         return self();
      }

      /**
       * @see BaseInstance#getMasterUsername()
       */
      public T masterUsername(String masterUsername) {
         this.masterUsername = masterUsername;
         return self();
      }

      /**
       * @see BaseInstance#getAllocatedStorageGB()
       */
      public T allocatedStorageGB(int allocatedStorageGB) {
         this.allocatedStorageGB = allocatedStorageGB;
         return self();
      }

      public BaseInstance build() {
         return new BaseInstance(name, instanceClass, engine, masterUsername, allocatedStorageGB);
      }

      public T fromBaseInstance(BaseInstance in) {
         return this.name(in.getName().orNull()).instanceClass(in.getInstanceClass()).engine(in.getEngine())
                  .masterUsername(in.getMasterUsername()).allocatedStorageGB(in.getAllocatedStorageGB());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected final Optional<String> name;
   protected final String instanceClass;
   protected final String engine;
   protected final String masterUsername;
   protected int allocatedStorageGB;

   protected BaseInstance(Optional<String> name, String instanceClass, String engine, String masterUsername,
            int allocatedStorageGB) {
      this.name = checkNotNull(name, "name");
      this.instanceClass = checkNotNull(instanceClass, "instanceClass");
      this.engine = checkNotNull(engine, "engine");
      this.masterUsername = checkNotNull(masterUsername, "masterUsername");
      this.allocatedStorageGB = allocatedStorageGB;
   }

   /**
    * The meaning of this parameter differs according to the database engine you use.
    * 
    * <h4>MySQL</h4>
    * 
    * Contains the name of the initial database of this instance that was provided at create time,
    * if one was specified when the DB BaseInstance was created. This same name is returned for the
    * life of the DB BaseInstance.
    * 
    * <h4>Oracle</h4>
    * 
    * Contains the Oracle System ID (SID) of the created DB BaseInstance.
    */
   public Optional<String> getName() {
      return name;
   }

   /**
    * Contains the name of the compute and memory capacity class of the DB BaseInstance.
    */
   public String getInstanceClass() {
      return instanceClass;
   }

   /**
    * Provides the name of the database engine to be used for this DB BaseInstance.
    */
   public String getEngine() {
      return engine;
   }

   /**
    * Contains the master username for the DB BaseInstance.
    */
   public String getMasterUsername() {
      return masterUsername;
   }

   /**
    * Specifies the allocated storage size specified in gigabytes.
    */
   public int getAllocatedStorageGB() {
      return allocatedStorageGB;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(name.orNull(), instanceClass, engine, masterUsername, allocatedStorageGB);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      BaseInstance other = (BaseInstance) obj;
      return Objects.equal(this.name, other.name) && Objects.equal(this.instanceClass, other.instanceClass)
               && Objects.equal(this.engine, other.engine) && Objects.equal(this.masterUsername, other.masterUsername)
               && Objects.equal(this.allocatedStorageGB, other.allocatedStorageGB);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("name", name.orNull()).add("instanceClass", instanceClass)
               .add("engine", engine).add("masterUsername", masterUsername)
               .add("allocatedStorageGB", allocatedStorageGB);
   }

}
