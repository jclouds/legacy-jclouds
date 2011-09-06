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
package org.jclouds.scality.rs2;

import java.util.List;
import java.util.Properties;

import org.jclouds.s3.S3ContextBuilder;
import org.jclouds.scality.rs2.blobstore.config.ScalityRS2BlobStoreContextModule;
import org.jclouds.scality.rs2.config.ScalityRS2RestClientModule;

import com.google.inject.Module;

/**
 * 
 * 
 * @author Adrian Cole
 */
public class ScalityRS2ContextBuilder extends S3ContextBuilder {

   public ScalityRS2ContextBuilder(Properties props) {
      super(props);
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new ScalityRS2RestClientModule());
   }
   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new ScalityRS2BlobStoreContextModule());
   }

}
