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
package org.jclouds.rds.features;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rds.domain.SecurityGroup;
import org.jclouds.rds.options.ListSecurityGroupsOptions;

/**
 * Provides access to Amazon RDS via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonRDS/latest/APIReference" >doc</a>
 * @see SecurityGroupAsyncApi
 * @author Adrian Cole
 */
public interface SecurityGroupApi {
   /**
    * Creates a new DB Security Group. DB Security Groups control access to a DB Instance.
    * 
    * @param name
    *           The name for the DB Security Group. This value is stored as a lowercase string.
    * 
    *           Constraints: Must contain no more than 255 alphanumeric characters or hyphens. Must
    *           not be "Default".
    * @param description
    *           The description for the DB Security Group.
    * 
    * @return the new security group
    */
   SecurityGroup createWithNameAndDescription(String name, String description);

   /**
    * Creates a new DB Security Group. DB Security Groups control access to a DB Instance.
    * 
    * @param vpcId
    *           The Id of VPC. Indicates which VPC this DB Security Group should belong to. Must be
    *           specified to create a DB Security Group for a VPC; may not be specified otherwise.
    * 
    * @param name
    *           The name for the DB Security Group. This value is stored as a lowercase string.
    * 
    *           Constraints: Must contain no more than 255 alphanumeric characters or hyphens. Must
    *           not be "Default".
    * @param description
    *           The description for the DB Security Group.
    * @return the new security group
    */
   SecurityGroup createInVPCWithNameAndDescription(String vpcId, String name, String description);

   /**
    * Retrieves information about the specified {@link SecurityGroup}.
    * 
    * @param name
    *           Name of the security group to get information about.
    * @return null if not found
    */
   @Nullable
   SecurityGroup get(String name);

   /**
    * Returns a list of {@link SecurityGroup}s.
    * 
    * <br/>
    * You can paginate the results using the {@link ListSecurityGroupsOptions parameter}
    * 
    * @param options
    *           the options describing the security groups query
    * 
    * @return the response object
    */
   IterableWithMarker<SecurityGroup> list(ListSecurityGroupsOptions options);

   /**
    * Returns a list of {@link SecurityGroup}s.
    * 
    * @return the response object
    */
   PagedIterable<SecurityGroup> list();

   /**
    * Enables ingress to a DBSecurityGroup to an IP range, if the application accessing your
    * database is running on the Internet.
    * 
    * @param name
    *           The name of the DB Security Group to add authorization to.
    * @param CIDR
    *           The IP range to authorize.
    * @return updated security group, noting the authorization status may not be complete
    */
   SecurityGroup authorizeIngressToIPRange(String name, String CIDR);

   /**
    * Enables ingress to a DBSecurityGroup if the application using the database is running on EC2
    * instances.
    * 
    * <h4>Note</h4>
    * 
    * You cannot authorize ingress from an EC2 security group in one Region to an Amazon RDS DB
    * Instance in another.
    * 
    * @param name
    *           The name of the DB Security Group to add authorization to.
    * @param ec2SecurityGroupName
    *           Name of the EC2 Security Group to authorize.
    * @param ec2SecurityGroupOwnerId
    *           AWS Account Number of the owner of the EC2 Security Group specified in the
    *           EC2SecurityGroupName parameter. The AWS Access Key ID is not an acceptable value.
    * @return updated security group, noting the authorization status may not be complete
    */
   SecurityGroup authorizeIngressToEC2SecurityGroupOfOwner(String name, String ec2SecurityGroupName,
            String ec2SecurityGroupOwnerId);

   /**
    * Enables ingress to a DBSecurityGroup if the application using the database is running on VPC
    * instances.
    * 
    * <h4>Note</h4>
    * 
    * You cannot authorize ingress from a VPC security group in one VPC to an Amazon RDS DB Instance
    * in another.
    * 
    * @param name
    *           The name of the DB Security Group to add authorization to.
    * @param vpcSecurityGroupId
    *           Id of the EC2 Security Group to authorize.
    * @return updated security group, noting the authorization status may not be complete
    */
   SecurityGroup authorizeIngressToVPCSecurityGroup(String name, String vpcSecurityGroupId);

   /**
    * Revokes ingress from a DBSecurityGroup for previously authorized IP range. 
    * 
    * @param name
    *           The name of the DB Security Group to revoke ingress from.
    * @param CIDR
    *           The IP range to revoke.
    * @return updated security group, noting the authorization status may not be complete
    */
   SecurityGroup revokeIngressFromIPRange(String name, String CIDR);

   /**
    * Revokes ingress from a DBSecurityGroup for previously authorized EC2 Security Group. 
    * 
    * @param name
    *           The name of the DB Security Group to revoke ingress from.
    * @param ec2SecurityGroupName
    *           Name of the EC2 Security Group to revoke.
    * @param ec2SecurityGroupOwnerId
    *           AWS Account Number of the owner of the EC2 Security Group specified in the
    *           EC2SecurityGroupName parameter. The AWS Access Key ID is not an acceptable value.
    * @return updated security group, noting the authorization status may not be complete
    */
   SecurityGroup revokeIngressFromEC2SecurityGroupOfOwner(String name, String ec2SecurityGroupName,
            String ec2SecurityGroupOwnerId);

   /**
    * Revokes ingress from a DBSecurityGroup for previously authorized VPC Security Group. 
    * 
    * @param name
    *           The name of the DB Security Group to revoke ingress from.
    * @param vpcSecurityGroupId
    *           Id of the EC2 Security Group to revoke.
    * @return updated security group, noting the authorization status may not be complete
    */
   SecurityGroup revokeIngressFromVPCSecurityGroup(String name, String vpcSecurityGroupId);

   /**
    * Deletes a DB security group.
    * 
    * <h4>Naming Constraints</h4>
    * 
    * <ul>
    * <li>Must be 1 to 255 alphanumeric characters</li>
    * <li>First character must be a letter</li>
    * <li>Cannot end with a hyphen or contain two consecutive hyphens</li>
    * </ul>
    * 
    * @param name
    *           The name of the database security group to delete.
    * 
    *           <h4>Note</h4>
    * 
    *           You cannot delete the default security group.
    */
   void delete(String name);

}
