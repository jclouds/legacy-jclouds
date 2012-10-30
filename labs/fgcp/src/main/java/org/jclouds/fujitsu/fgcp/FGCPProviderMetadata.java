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
package org.jclouds.fujitsu.fgcp;

import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;

import java.util.Properties;

import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Base implementation of {@link org.jclouds.providers.ProviderMetadata} for
 * FGCP.
 * 
 * @author Dies Koper
 */
public class FGCPProviderMetadata extends BaseProviderMetadata {

   private static final long serialVersionUID = 7527265705102650456L;

   public static Builder builder() {
      return new Builder();
   }

   public FGCPProviderMetadata() {
      super(builder());
   }

   public FGCPProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();

      properties.setProperty(TEMPLATE,
            "osFamily=CENTOS,osVersionMatches=6.2,os64Bit=true");

      return properties;
   }
}
