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
package org.jclouds.rds.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HostAndPort;

/**
 * A DB Instance is an isolated database environment running in the cloud. A DB Instance can contain
 * multiple user-created databases, and can be accessed using the same tools and applications as a
 * stand-alone database instance.
 * 
 * 
 * <h4>Note</h4>
 * 
 * Amazon RDS supports access from any standard SQL client application. Amazon RDS does not allow
 * direct host access via Telnet, Secure Shell (SSH), or Windows Remote Desktop Connection.
 * 
 * 
 * DB Instances are simple to create and modify with the Amazon RDS command line tools, APIs, or the
 * AWS Management Console.
 * 
 * 
 * Amazon RDS creates a master user account for your DB Instance as part of the creation process.
 * This master user has permissions to create databases and to perform create, delete, select,
 * update and insert operations on tables the master user creates. You must set the master user
 * password when you create a DB Instance, but you can change it at any time using the Amazon RDS
 * command line tools, APIs, or the AWS Management Console. You can also change the master user
 * password and manage users using standard SQL commands.
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonRDS/latest/UserGuide/Concepts.DBInstance.html"
 *      >doc</a>
 * 
 * @author Adrian Cole
 */
public class Instance {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromInstance(this);
   }

   public static abstract class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected Optional<String> name = Optional.absent();
      protected String instanceClass;
      protected HostAndPort endpoint;
      protected String status;
      protected String availabilityZone;
      protected boolean multiAZ;
      protected String engine;
      protected String engineVersion;
      protected String licenseModel;
      protected String masterUsername;
      protected int allocatedStorageGB;
      protected Date createdTime;
      protected Optional<SubnetGroup> subnetGroup = Optional.absent();
      protected ImmutableMap.Builder<String, String> securityGroupNameToStatus = ImmutableMap
               .<String, String> builder();

      /**
       * @see Instance#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see Instance#getName()
       */
      public T name(String name) {
         this.name = Optional.fromNullable(name);
         return self();
      }

      /**
       * @see Instance#getInstanceClass()
       */
      public T instanceClass(String instanceClass) {
         this.instanceClass = instanceClass;
         return self();
      }

      /**
       * @see Instance#getEndpoint()
       */
      public T endpoint(HostAndPort endpoint) {
         this.endpoint = endpoint;
         return self();
      }

      /**
       * @see Instance#getStatus()
       */
      public T status(String status) {
         this.status = status;
         return self();
      }

      /**
       * @see Instance#getAvailabilityZone()
       */
      public T availabilityZone(String availabilityZone) {
         this.availabilityZone = availabilityZone;
         return self();
      }

      /**
       * @see Instance#isMultiAZ()
       */
      public T multiAZ(boolean multiAZ) {
         this.multiAZ = multiAZ;
         return self();
      }

      /**
       * @see Instance#getEngine()
       */
      public T engine(String engine) {
         this.engine = engine;
         return self();
      }

      /**
       * @see Instance#getEngineVersion()
       */
      public T engineVersion(String engineVersion) {
         this.engineVersion = engineVersion;
         return self();
      }

      /**
       * @see Instance#getLicenseModel()
       */
      public T licenseModel(String licenseModel) {
         this.licenseModel = licenseModel;
         return self();
      }

      /**
       * @see Instance#getMasterUsername()
       */
      public T masterUsername(String masterUsername) {
         this.masterUsername = masterUsername;
         return self();
      }

      /**
       * @see Instance#getAllocatedStorageGB()
       */
      public T allocatedStorageGB(int allocatedStorageGB) {
         this.allocatedStorageGB = allocatedStorageGB;
         return self();
      }

      /**
       * @see Instance#getCreatedTime()
       */
      public T createdTime(Date createdTime) {
         this.createdTime = createdTime;
         return self();
      }

      /**
       * @see Instance#getSubnetGroup()
       */
      public T subnetGroup(SubnetGroup subnetGroup) {
         this.subnetGroup = Optional.fromNullable(subnetGroup);
         return self();
      }

      /**
       * @see Instance#getSecurityGroupNameToStatus()
       */
      public T securityGroupNameToStatus(Map<String, String> securityGroupNameToStatus) {
         this.securityGroupNameToStatus.putAll(checkNotNull(securityGroupNameToStatus, "securityGroupNameToStatus"));
         return self();
      }

      /**
       * @see Instance#getSecurityGroupNameToStatus()
       */
      public T securityGroupNameToStatus(String securityGroupName, String status) {
         this.securityGroupNameToStatus.put(checkNotNull(securityGroupName, "securityGroupName"),
                  checkNotNull(status, "status"));
         return self();
      }

      public Instance build() {
         return new Instance(id, name, instanceClass, endpoint, status, availabilityZone, multiAZ, engine,
                  engineVersion, licenseModel, masterUsername, allocatedStorageGB, createdTime, subnetGroup,
                  securityGroupNameToStatus.build());
      }

      public T fromInstance(Instance in) {
         return this.id(in.getId()).name(in.getName().orNull()).instanceClass(in.getInstanceClass())
                  .endpoint(in.getEndpoint()).status(in.getStatus()).availabilityZone(in.getAvailabilityZone())
                  .multiAZ(in.isMultiAZ()).engine(in.getEngine()).engineVersion(in.getEngineVersion())
                  .licenseModel(in.getLicenseModel()).masterUsername(in.getMasterUsername())
                  .allocatedStorageGB(in.getAllocatedStorageGB()).createdTime(in.getCreatedTime())
                  .subnetGroup(in.getSubnetGroup().orNull())
                  .securityGroupNameToStatus(in.getSecurityGroupNameToStatus());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected final String id;
   protected final Optional<String> name;
   protected final String instanceClass;
   protected final HostAndPort endpoint;
   protected final String status;
   protected final String availabilityZone;
   protected final boolean multiAZ;
   protected final String engine;
   protected final String engineVersion;
   protected final String licenseModel;
   protected final String masterUsername;
   protected int allocatedStorageGB;
   protected final Date createdTime;
   protected final Optional<SubnetGroup> subnetGroup;
   protected final Map<String, String> securityGroupNameToStatus;

   protected Instance(String id, Optional<String> name, String instanceClass, HostAndPort endpoint, String status,
            String availabilityZone, boolean multiAZ, String engine, String engineVersion, String licenseModel,
            String masterUsername, int allocatedStorageGB, Date createdTime, Optional<SubnetGroup> subnetGroup,
            Map<String, String> securityGroupNameToStatus) {
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name of %s", id);
      this.endpoint = checkNotNull(endpoint, "endpoint of %s", id);
      this.status = checkNotNull(status, "status of %s", id);
      this.instanceClass = checkNotNull(instanceClass, "instanceClass of %s", id);
      this.availabilityZone = checkNotNull(availabilityZone, "availabilityZone of %s", id);
      this.multiAZ = multiAZ;
      this.engine = checkNotNull(engine, "engine of %s", id);
      this.engineVersion = checkNotNull(engineVersion, "engineVersion of %s", id);
      this.licenseModel = checkNotNull(licenseModel, "licenseModel of %s", id);
      this.masterUsername = checkNotNull(masterUsername, "masterUsername of %s", id);
      this.allocatedStorageGB = allocatedStorageGB;
      this.createdTime = checkNotNull(createdTime, "createdTime of %s", id);
      this.subnetGroup = checkNotNull(subnetGroup, "subnetGroup of %s", id);
      this.securityGroupNameToStatus = ImmutableMap.copyOf(checkNotNull(securityGroupNameToStatus,
               "securityGroupNameToStatus of %s", id));
   }

   /**
    * Contains a user-supplied database identifier. This is the unique key that identifies a DB
    * Instance.
    */
   public String getId() {
      return id;
   }

   /**
    * The meaning of this parameter differs according to the database engine you use.
    * 
    * <h4>MySQL</h4>
    * 
    * Contains the name of the initial database of this instance that was provided at create time,
    * if one was specified when the DB Instance was created. This same name is returned for the life
    * of the DB Instance.
    * 
    * <h4>Oracle</h4>
    * 
    * Contains the Oracle System ID (SID) of the created DB Instance.
    */
   public Optional<String> getName() {
      return name;
   }

   /**
    * Specifies the current state of this database.
    */
   public String getStatus() {
      return status;
   }

   /**
    * Contains the name of the compute and memory capacity class of the DB Instance.
    */
   public String getInstanceClass() {
      return instanceClass;
   }

   /**
    * Specifies the connection endpoint.
    */
   public HostAndPort getEndpoint() {
      return endpoint;
   }

   /**
    * Specifies the name of the Availability Zone the DB Instance is located in.
    */
   public String getAvailabilityZone() {
      return availabilityZone;
   }

   /**
    * Specifies the name of the Availability Zone the DB Instance is located in.
    */
   public boolean isMultiAZ() {
      return multiAZ;
   }

   /**
    * Provides the name of the database engine to be used for this DB Instance.
    */
   public String getEngine() {
      return engine;
   }

   /**
    * Indicates the database engine version.
    */
   public String getEngineVersion() {
      return engineVersion;
   }

   /**
    * License model information for this DB Instance.
    */
   public String getLicenseModel() {
      return licenseModel;
   }

   /**
    * Contains the master username for the DB Instance.
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
    * Provides the date and time the DB Instance was created.
    */
   public Date getCreatedTime() {
      return createdTime;
   }

   /**
    * Provides the information of the subnet group associated with the DB instance, including the
    * name, description and subnets in the subnet group.
    */
   public Optional<SubnetGroup> getSubnetGroup() {
      return subnetGroup;
   }

   /**
    * SecurityGroupName -> Status
    */
   public Map<String, String> getSecurityGroupNameToStatus() {
      return securityGroupNameToStatus;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(id, createdTime);
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
      Instance other = (Instance) obj;
      return Objects.equal(this.id, other.id) && Objects.equal(this.createdTime, other.createdTime);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("id", id).add("name", name.orNull())
               .add("instanceClass", instanceClass).add("endpoint", endpoint).add("status", status)
               .add("availabilityZone", availabilityZone).add("multiAZ", multiAZ).add("engine", engine)
               .add("engineVersion", engineVersion).add("licenseModel", licenseModel)
               .add("masterUsername", masterUsername).add("allocatedStorageGB", allocatedStorageGB)
               .add("createdTime", createdTime).add("subnetGroup", subnetGroup.orNull())
               .add("securityGroupNameToStatus", securityGroupNameToStatus).toString();
   }

}
