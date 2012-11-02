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

import java.util.Set;

import org.jclouds.rds.domain.internal.BaseInstance;

import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

/**
 * Parameters used to create a new {@link Instance}
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonRDS/latest/APIReference/API_CreateDBInstance.html"
 *      >doc</a>
 * 
 * @author Adrian Cole
 */
public class InstanceRequest extends BaseInstance {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromInstance(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends BaseInstance.Builder<T> {

      protected Optional<String> engineVersion = Optional.absent();
      protected Optional<String> licenseModel = Optional.absent();
      protected Optional<Integer> port = Optional.absent();
      protected Optional<String> characterSet = Optional.absent();
      protected int backupRetentionPeriod = 1;
      protected Optional<String> optionGroup = Optional.absent();
      protected Optional<String> parameterGroup = Optional.absent();
      protected boolean autoMinorVersionUpgrade = true;
      protected Optional<String> subnetGroup = Optional.absent();
      protected ImmutableSet.Builder<String> securityGroups = ImmutableSet.<String> builder();
      protected String masterPassword;

      /**
       * @see InstanceRequest#getEngineVersion()
       */
      public T engineVersion(String engineVersion) {
         this.engineVersion = Optional.fromNullable(engineVersion);
         return self();
      }

      /**
       * @see InstanceRequest#getLicenseModel()
       */
      public T licenseModel(String licenseModel) {
         this.licenseModel = Optional.fromNullable(licenseModel);
         return self();
      }

      /**
       * @see InstanceRequest#getPort()
       */
      public T port(Integer port) {
         this.port = Optional.fromNullable(port);
         return self();
      }

      /**
       * @see InstanceRequest#getCharacterSet()
       */
      public T characterSet(String characterSet) {
         this.characterSet = Optional.fromNullable(characterSet);
         return self();
      }

      /**
       * @see InstanceRequest#getBackupRetentionPeriod()
       */
      public T backupRetentionPeriod(int backupRetentionPeriod) {
         this.backupRetentionPeriod = backupRetentionPeriod;
         return self();
      }

      /**
       * @see InstanceRequest#getOptionGroup()
       */
      public T optionGroup(String optionGroup) {
         this.optionGroup = Optional.fromNullable(optionGroup);
         return self();
      }

      /**
       * @see InstanceRequest#getParameterGroup()
       */
      public T parameterGroup(String parameterGroup) {
         this.parameterGroup = Optional.fromNullable(parameterGroup);
         return self();
      }

      /**
       * @see InstanceRequest#isAutoMinorVersionUpgrade()
       */
      public T autoMinorVersionUpgrade(boolean autoMinorVersionUpgrade) {
         this.autoMinorVersionUpgrade = autoMinorVersionUpgrade;
         return self();
      }

      /**
       * @see InstanceRequest#getSubnetGroup()
       */
      public T subnetGroup(String subnetGroup) {
         this.subnetGroup = Optional.fromNullable(subnetGroup);
         return self();
      }

      /**
       * @see InstanceRequest#getSecurityGroups()
       */
      public T securityGroups(Iterable<String> securityGroups) {
         this.securityGroups.addAll(checkNotNull(securityGroups, "securityGroups"));
         return self();
      }

      /**
       * @see InstanceRequest#getSecurityGroups()
       */
      public T securityGroup(String securityGroupName) {
         this.securityGroups.add(checkNotNull(securityGroupName, "securityGroupName"));
         return self();
      }

      /**
       * @see InstanceRequest#getMasterPassword()
       */
      public T masterPassword(String masterPassword) {
         this.masterPassword = checkNotNull(masterPassword, "masterPassword");
         return self();
      }

      public InstanceRequest build() {
         return new InstanceRequest(name, instanceClass, port, characterSet, optionGroup, parameterGroup,
                  autoMinorVersionUpgrade, engine, engineVersion, licenseModel, masterUsername, allocatedStorageGB,
                  backupRetentionPeriod, subnetGroup, securityGroups.build(), masterPassword);
      }

      public T fromInstance(InstanceRequest in) {
         return fromBaseInstance(in).engineVersion(in.getEngineVersion().orNull())
                  .licenseModel(in.getLicenseModel().orNull()).port(in.getPort().orNull())
                  .characterSet(in.getCharacterSet().orNull()).backupRetentionPeriod(in.getBackupRetentionPeriod())
                  .optionGroup(in.getOptionGroup().orNull()).parameterGroup(in.getParameterGroup().orNull())
                  .autoMinorVersionUpgrade(in.isAutoMinorVersionUpgrade()).subnetGroup(in.getSubnetGroup().orNull())
                  .securityGroups(in.getSecurityGroups());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected final Optional<String> engineVersion;
   protected final Optional<String> licenseModel;
   protected final Optional<Integer> port;
   protected final Optional<String> characterSet;
   protected final int backupRetentionPeriod;
   protected final Optional<String> optionGroup;
   protected final Optional<String> parameterGroup;
   protected final boolean autoMinorVersionUpgrade;
   protected final Optional<String> subnetGroup;
   protected final Set<String> securityGroups;
   protected final String masterPassword;

   protected InstanceRequest(Optional<String> name, String instanceClass, Optional<Integer> port,
            Optional<String> characterSet, Optional<String> optionGroup, Optional<String> parameterGroup,
            boolean autoMinorVersionUpgrade, String engine, Optional<String> engineVersion,
            Optional<String> licenseModel, String masterUsername, int allocatedStorageGB, int backupRetentionPeriod,
            Optional<String> subnetGroup, Iterable<String> securityGroups, String masterPassword) {
      super(name, instanceClass, engine, masterUsername, allocatedStorageGB);
      this.engineVersion = checkNotNull(engineVersion, "engineVersion");
      this.licenseModel = checkNotNull(licenseModel, "licenseModel");
      this.optionGroup = checkNotNull(optionGroup, "optionGroup");
      this.parameterGroup = checkNotNull(parameterGroup, "parameterGroup");
      this.autoMinorVersionUpgrade = autoMinorVersionUpgrade;
      this.port = checkNotNull(port, "port");
      this.characterSet = checkNotNull(characterSet, "characterSet");
      this.backupRetentionPeriod = checkNotNull(backupRetentionPeriod, "backupRetentionPeriod");
      this.subnetGroup = checkNotNull(subnetGroup, "subnetGroup");
      this.securityGroups = ImmutableSet.copyOf(checkNotNull(securityGroups, "securityGroups"));
      this.masterPassword = checkNotNull(masterPassword, "masterPassword");
   }

   /**
    * The version number of the database engine to use.
    * 
    * MySQL
    * 
    * Example: 5.1.42
    * 
    * Oracle
    * 
    * Example: 11.2.0.2.v2
    * 
    * SQL Server
    * 
    * Example: 10.50.2789.0.v1
    * 
    */
   public Optional<String> getEngineVersion() {
      return engineVersion;
   }

   /**
    * License model information for this DB Instance.
    * 
    * Valid values: license-included | bring-your-own-license | general-public-license
    */
   public Optional<String> getLicenseModel() {
      return licenseModel;
   }

   /**
    * For supported engines, indicates that the DB Instance should be associated with the specified
    * CharacterSet.
    */
   public Optional<String> getCharacterSet() {
      return characterSet;
   }

   /**
    * The port number on which the database accepts connections.
    * <table>
    * <tr>
    * <td>Engine</td>
    * <td>Default</td>
    * <td>Range</td>
    * </tr>
    * <tr>
    * <td>MySQL</td>
    * <td>3306</td>
    * <td>1150-65535</td>
    * </tr>
    * <tr>
    * <td>Oracle</td>
    * <td>1521</td>
    * <td>1150-65535</td>
    * </tr>
    * <tr>
    * <td>SQL Server</td>
    * <td>1433</td>
    * <td>1150-65535 except for 1434 and 3389</td>
    * </tr>
    * </table>
    */
   public Optional<Integer> getPort() {
      return port;
   }

   /**
    * The number of days for which automated backups are retained. Setting this parameter to a
    * positive number enables backups. Setting this parameter to 0 disables automated backups.
    * 
    * 
    * <h4>Constraints</h4>
    * 
    * Must be a value from 0 to 8 Cannot be set to 0 if the DB Instance is a master instance with
    * read replicas
    * 
    * @return value which defaults to {@code 1}
    */
   public int getBackupRetentionPeriod() {
      return backupRetentionPeriod;
   }

   /**
    * Indicates that the DB Instance should be associated with the specified option group.
    */
   public Optional<String> getOptionGroup() {
      return optionGroup;
   }

   /**
    * The name of the DB Parameter Group to associate with this DB instance. If this argument is
    * omitted, the default DBParameterGroup for the specified engine will be used.
    * 
    * <h4>Constraints</h4>
    * 
    * Must be 1 to 255 alphanumeric characters First character must be a letter Cannot end with a
    * hyphen or contain two consecutive hyphens
    */
   public Optional<String> getParameterGroup() {
      return parameterGroup;
   }

   /**
    * Indicates that minor engine upgrades will be applied automatically to the DB Instance during
    * the maintenance window.
    * 
    * @return value defaulting to {@code true}
    */
   public boolean isAutoMinorVersionUpgrade() {
      return autoMinorVersionUpgrade;
   }

   /**
    * A DB Subnet Group to associate with this DB Instance.
    * 
    * If there is no DB Subnet Group, then it is a non-VPC DB instance.
    */
   public Optional<String> getSubnetGroup() {
      return subnetGroup;
   }

   /**
    * A list of DB Security Groups to associate with this DB Instance.
    * 
    * Default: The default DB Security Group for the database engine.
    */
   public Set<String> getSecurityGroups() {
      return securityGroups;
   }

   /**
    * The password for the master database user.
    * 
    * MySQL
    * 
    * Constraints: Must contain from 8 to 41 alphanumeric characters.
    * 
    * Oracle
    * 
    * Constraints: Must contain from 8 to 30 alphanumeric characters.
    * 
    * SQL Server
    * 
    * Constraints: Must contain from 8 to 128 alphanumeric characters.
    */
   public String getMasterPassword() {
      return masterPassword;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected ToStringHelper string() {
      return super.string().add("engineVersion", engineVersion.orNull()).add("licenseModel", licenseModel.orNull())
               .add("port", port.orNull()).add("characterSet", characterSet.orNull()).add("optionGroup", optionGroup)
               .add("parameterGroup", parameterGroup).add("autoMinorVersionUpgrade", autoMinorVersionUpgrade)
               .add("backupRetentionPeriod", backupRetentionPeriod).add("subnetGroup", subnetGroup.orNull())
               .add("securityGroups", securityGroups);
   }

}
