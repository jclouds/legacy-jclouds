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

import org.jclouds.rds.domain.internal.BaseInstance;

import com.google.common.base.CaseFormat;
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
 * Amazon RDS supports access from any standard SQL api application. Amazon RDS does not allow
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
public class Instance extends BaseInstance {
   public static enum Status {

      /**
       * the instance is in the process of being created
       */
      CREATING,

      /**
       * the instance is available
       */
      AVAILABLE, STORAGE_FULL, INCOMPATIBLE_OPTION_GROUP, INCOMPATIBLE_PARAMETERS, INCOMPATIBLE_RESTORE, FAILED,
      /**
       * the instance is deleting
       */
      DELETING, UNRECOGNIZED;

      public String value() {
         return name().toLowerCase();
      }

      @Override
      public String toString() {
         return value();
      }

      public static Status fromValue(String status) {
         try {
            return valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(status, "status")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromInstance(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends BaseInstance.Builder<T> {

      protected String id;
      protected Optional<HostAndPort> endpoint = Optional.absent();
      protected String engineVersion;
      protected String rawStatus;
      protected Status status;
      protected Optional<Date> createdTime = Optional.absent();
      protected String licenseModel;
      protected Optional<String> availabilityZone = Optional.absent();
      protected boolean multiAZ;
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
       * @see Instance#getEndpoint()
       */
      public T endpoint(HostAndPort endpoint) {
         this.endpoint = Optional.fromNullable(endpoint);
         return self();
      }

      /**
       * @see Instance#getRawStatus()
       */
      public T rawStatus(String rawStatus) {
         this.rawStatus = rawStatus;
         return self();
      }

      /**
       * @see Instance#getStatus()
       */
      public T status(Status status) {
         this.status = status;
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
       * @see Instance#getCreatedTime()
       */
      public T createdTime(Date createdTime) {
         this.createdTime = Optional.fromNullable(createdTime);
         return self();
      }

      /**
       * @see Instance#getAvailabilityZone()
       */
      public T availabilityZone(String availabilityZone) {
         this.availabilityZone = Optional.fromNullable(availabilityZone);
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
         return new Instance(id, name, instanceClass, endpoint, rawStatus, status, availabilityZone, multiAZ, engine,
                  engineVersion, licenseModel, masterUsername, allocatedStorageGB, createdTime, subnetGroup,
                  securityGroupNameToStatus.build());
      }

      public T fromInstance(Instance in) {
         return fromBaseInstance(in).id(in.getId()).endpoint(in.getEndpoint().orNull()).status(in.getStatus())
                  .createdTime(in.getCreatedTime().orNull()).engineVersion(in.getEngineVersion())
                  .licenseModel(in.getLicenseModel()).availabilityZone(in.getAvailabilityZone().orNull())
                  .multiAZ(in.isMultiAZ()).subnetGroup(in.getSubnetGroup().orNull())
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
   protected final Optional<HostAndPort> endpoint;
   protected final String rawStatus;
   protected final Status status;
   protected final Optional<Date> createdTime;
   protected final String engineVersion;
   protected final String licenseModel;
   protected final Optional<String> availabilityZone;
   protected final boolean multiAZ;
   protected final Optional<SubnetGroup> subnetGroup;
   protected final Map<String, String> securityGroupNameToStatus;

   protected Instance(String id, Optional<String> name, String instanceClass, Optional<HostAndPort> endpoint,
            String rawStatus, Status status, Optional<String> availabilityZone, boolean multiAZ, String engine,
            String engineVersion, String licenseModel, String masterUsername, int allocatedStorageGB,
            Optional<Date> createdTime, Optional<SubnetGroup> subnetGroup, Map<String, String> securityGroupNameToStatus) {
      super(name, instanceClass, engine, masterUsername, allocatedStorageGB);
      this.id = checkNotNull(id, "id");
      this.availabilityZone = checkNotNull(availabilityZone, "availabilityZone of %s", id);
      this.multiAZ = multiAZ;
      this.endpoint = checkNotNull(endpoint, "endpoint of %s", id);
      this.rawStatus = checkNotNull(rawStatus, "rawStatus of %s", id);
      this.status = checkNotNull(status, "status of %s", id);
      this.engineVersion = checkNotNull(engineVersion, "engineVersion of %s", id);
      this.licenseModel = checkNotNull(licenseModel, "licenseModel of %s", id);
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
    * Specifies the current state of this database.
    */
   public Status getStatus() {
      return status;
   }

   /**
    * Specifies the current state of this database unparsed.
    */
   public String getRawStatus() {
      return rawStatus;
   }

   /**
    * Specifies the connection endpoint, or absent if the database is in {@link Status#CREATING} or {@link Status#DELETING} states
    */
   public Optional<HostAndPort> getEndpoint() {
      return endpoint;
   }

   /**
    * Provides the date and time the DB Instance was created, or absent if the database is in
    * {@code creating} state
    */
   public Optional<Date> getCreatedTime() {
      return createdTime;
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
    * Specifies the name of the Availability Zone the DB Instance is located in, or absent if the
    * database is in {@code creating} state
    */
   public Optional<String> getAvailabilityZone() {
      return availabilityZone;
   }

   /**
    * Specifies the name of the Availability Zone the DB Instance is located in.
    */
   public boolean isMultiAZ() {
      return multiAZ;
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

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      Instance that = Instance.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("id", id).add("name", name.orNull())
               .add("instanceClass", instanceClass).add("endpoint", endpoint.orNull()).add("status", rawStatus)
               .add("availabilityZone", availabilityZone.orNull()).add("multiAZ", multiAZ).add("engine", engine)
               .add("engineVersion", engineVersion).add("licenseModel", licenseModel)
               .add("masterUsername", masterUsername).add("allocatedStorageGB", allocatedStorageGB)
               .add("createdTime", createdTime.orNull()).add("subnetGroup", subnetGroup.orNull())
               .add("securityGroupNameToStatus", securityGroupNameToStatus).toString();
   }

}
