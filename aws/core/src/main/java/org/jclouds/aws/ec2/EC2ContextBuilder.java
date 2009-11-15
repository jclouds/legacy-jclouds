/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.ec2;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AWS_ACCESSKEYID;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AWS_SECRETACCESSKEY;

import java.util.List;
import java.util.Properties;

import org.jclouds.aws.ec2.config.EC2ContextModule;
import org.jclouds.aws.ec2.config.EC2RestClientModule;
import org.jclouds.rest.RestContextBuilder;

import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class EC2ContextBuilder extends RestContextBuilder<EC2AsyncClient, EC2Client> {

   public EC2ContextBuilder(Properties props) {
      super(new TypeLiteral<EC2AsyncClient>() {
      }, new TypeLiteral<EC2Client>() {
      }, props);
      checkNotNull(properties.getProperty(PROPERTY_AWS_ACCESSKEYID));
      checkNotNull(properties.getProperty(PROPERTY_AWS_SECRETACCESSKEY));
   }

   protected void addClientModule(List<Module> modules) {
      modules.add(new EC2RestClientModule());
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new EC2ContextModule());
   }

}
