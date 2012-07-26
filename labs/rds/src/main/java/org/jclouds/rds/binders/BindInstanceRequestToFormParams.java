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
package org.jclouds.rds.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.http.HttpRequest;
import org.jclouds.rds.domain.InstanceRequest;

import com.google.common.collect.ImmutableMultimap;

/**
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonRDS/latest/APIReference/API_CreateDBInstance.html"
 *      >doc</a>
 * 
 * @author Adrian Cole
 */
public class BindInstanceRequestToFormParams implements org.jclouds.rest.Binder {

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      InstanceRequest instanceRequest = InstanceRequest.class.cast(checkNotNull(input, "instanceRequest must be set!"));

      ImmutableMultimap.Builder<String, String> formParameters = ImmutableMultimap.builder();

      formParameters.put("AllocatedStorage", instanceRequest.getAllocatedStorageGB() + "");
      formParameters.put("AutoMinorVersionUpgrade", instanceRequest.isAutoMinorVersionUpgrade() + "");
      formParameters.put("BackupRetentionPeriod", instanceRequest.getBackupRetentionPeriod() + "");
      if (instanceRequest.getCharacterSet().isPresent())
         formParameters.put("CharacterSetName", instanceRequest.getCharacterSet().get());
      formParameters.put("DBInstanceClass", instanceRequest.getInstanceClass());
      if (instanceRequest.getName().isPresent())
         formParameters.put("DBName", instanceRequest.getName().get());
      if (instanceRequest.getParameterGroup().isPresent())
         formParameters.put("DBParameterGroupName", instanceRequest.getParameterGroup().get());
      int groupIndex = 1;
      for (String securityGroup : instanceRequest.getSecurityGroups())
         formParameters.put("DBSecurityGroups.member." + groupIndex++, securityGroup);
      if (instanceRequest.getSubnetGroup().isPresent())
         formParameters.put("DBSubnetGroupName", instanceRequest.getSubnetGroup().get());
      formParameters.put("Engine", instanceRequest.getEngine());
      if (instanceRequest.getEngineVersion().isPresent())
         formParameters.put("EngineVersion", instanceRequest.getEngineVersion().get());
      if (instanceRequest.getLicenseModel().isPresent())
         formParameters.put("LicenseModel", instanceRequest.getLicenseModel().get());
      formParameters.put("MasterUserPassword", instanceRequest.getMasterPassword());
      formParameters.put("MasterUsername", instanceRequest.getMasterUsername());
      if (instanceRequest.getOptionGroup().isPresent())
         formParameters.put("OptionGroupName", instanceRequest.getOptionGroup().get());
      if (instanceRequest.getPort().isPresent())
         formParameters.put("Port", instanceRequest.getPort().get().toString());

      return (R) request.toBuilder().replaceFormParams(formParameters.build()).build();

   }

}
