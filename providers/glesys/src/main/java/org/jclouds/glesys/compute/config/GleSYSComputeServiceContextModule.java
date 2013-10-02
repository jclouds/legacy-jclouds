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
package org.jclouds.glesys.compute.config;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamilyVersion64Bit;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.glesys.compute.GleSYSComputeServiceAdapter;
import org.jclouds.glesys.compute.functions.DatacenterToLocation;
import org.jclouds.glesys.compute.functions.OSTemplateToImage;
import org.jclouds.glesys.compute.functions.ParseOsFamilyVersion64BitFromImageName;
import org.jclouds.glesys.compute.functions.ServerDetailsToNodeMetadata;
import org.jclouds.glesys.compute.options.GleSYSTemplateOptions;
import org.jclouds.glesys.domain.OSTemplate;
import org.jclouds.glesys.domain.ServerDetails;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class GleSYSComputeServiceContextModule extends
         ComputeServiceAdapterContextModule<ServerDetails, Hardware, OSTemplate, String> {

   @SuppressWarnings("unchecked")
   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<ServerDetails, Hardware, OSTemplate, String>>() {
      }).to(GleSYSComputeServiceAdapter.class);
      bind(new TypeLiteral<Function<ServerDetails, NodeMetadata>>() {
      }).to(ServerDetailsToNodeMetadata.class);
      bind(new TypeLiteral<Function<OSTemplate, org.jclouds.compute.domain.Image>>() {
      }).to(OSTemplateToImage.class);
      bind(new TypeLiteral<Function<Hardware, Hardware>>() {
      }).to(Class.class.cast(IdentityFunction.class));
      bind(new TypeLiteral<Function<String, Location>>() {
      }).to(DatacenterToLocation.class);
      bind(new TypeLiteral<Function<String, OsFamilyVersion64Bit>>() {
      }).to(ParseOsFamilyVersion64BitFromImageName.class);
      bind(TemplateOptions.class).to(GleSYSTemplateOptions.class);
      install(new LocationsFromComputeServiceAdapterModule<ServerDetails, Hardware, OSTemplate, String>(){});
   }

}
