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
package org.jclouds.trmk.vcloud_0_8.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.Status;
import org.jclouds.trmk.vcloud_0_8.domain.VAppTemplate;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
public class ImageForVCloudExpressVAppTemplate implements Function<VAppTemplate, Image> {
   private final Map<Status, org.jclouds.compute.domain.Image.Status> toPortableImageStatus;
   private final FindLocationForResource findLocationForResource;
   private final PopulateDefaultLoginCredentialsForImageStrategy credentialsProvider;
   private final Function<String, OperatingSystem> osParser;

   private ReferenceType parent;

   @Inject
   protected ImageForVCloudExpressVAppTemplate(Map<Status, Image.Status> toPortableImageStatus, FindLocationForResource findLocationForResource,
            PopulateDefaultLoginCredentialsForImageStrategy credentialsProvider, Function<String, OperatingSystem> osParser) {
      this.toPortableImageStatus = checkNotNull(toPortableImageStatus, "toPortableImageStatus");
      this.findLocationForResource = checkNotNull(findLocationForResource, "findLocationForResource");
      this.credentialsProvider = checkNotNull(credentialsProvider, "credentialsProvider");
      this.osParser = osParser;
   }

   public ImageForVCloudExpressVAppTemplate withParent(ReferenceType parent) {
      this.parent = parent;
      return this;
   }

   @Override
   public Image apply(@Nullable VAppTemplate from) {
      if (from == null)
         return null;
      ImageBuilder builder = new ImageBuilder();
      builder.ids(from.getHref().toASCIIString());
      builder.uri(from.getHref());
      builder.name(from.getName());
      builder.location(findLocationForResource.apply(checkNotNull(parent, "parent")));
      builder.description(from.getDescription() != null ? from.getDescription() : from.getName());
      builder.operatingSystem(osParser.apply(from.getName()));
      builder.status(toPortableImageStatus.get(from.getStatus()));
      builder.defaultCredentials(credentialsProvider.apply(from));
      return builder.build();
   }
}
