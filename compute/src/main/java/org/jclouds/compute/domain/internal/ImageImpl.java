/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.compute.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import javax.annotation.Nullable;

import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;

/**
 * @author Adrian Cole
 */
public class ImageImpl extends ComputeMetadataImpl implements Image {

   /** The serialVersionUID */
   private static final long serialVersionUID = 7856744554191025307L;

   private final String version;
   private final OsFamily osFamily;
   private final String osDescription;
   private final Architecture architecture;

   public ImageImpl(String id, String name, String locationId, URI uri,
            Map<String, String> userMetadata, String description, String version,
            @Nullable OsFamily osFamily, String osDescription, Architecture architecture) {
      super(ComputeType.IMAGE, id, name, locationId, uri, userMetadata);
      this.version = checkNotNull(version, "version");
      this.osFamily = osFamily;
      this.osDescription = checkNotNull(osDescription, "osDescription");
      this.architecture = checkNotNull(architecture, "architecture");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getVersion() {
      return version;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public OsFamily getOsFamily() {
      return osFamily;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getOsDescription() {
      return osDescription;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Architecture getArchitecture() {
      return architecture;
   }

}
