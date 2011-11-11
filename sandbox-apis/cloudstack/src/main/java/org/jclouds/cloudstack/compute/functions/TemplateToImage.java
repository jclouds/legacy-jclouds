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
package org.jclouds.cloudstack.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cloudstack.domain.Template;
import org.jclouds.collect.FindResourceInSet;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.domain.Location;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 */
@Singleton
public class TemplateToImage implements Function<Template, Image> {
   private final FindLocationForTemplate findLocationForTemplate;
   private final Function<Template, OperatingSystem> templateToOperatingSystem;

   @Inject
   public TemplateToImage(FindLocationForTemplate findLocationForTemplate,
         Function<Template, OperatingSystem> templateToOperatingSystem) {
      this.findLocationForTemplate = checkNotNull(findLocationForTemplate, "findLocationForTemplate");
      this.templateToOperatingSystem = checkNotNull(templateToOperatingSystem, "templateToOperatingSystem");
   }

   @Override
   public Image apply(Template template) {
      checkNotNull(template, "template");

      OperatingSystem os = templateToOperatingSystem.apply(template);

      ImageBuilder builder = new ImageBuilder().ids(template.getId() + "").name(template.getName())
            .description(template.getDisplayText()).operatingSystem(os);

      if (!template.isCrossZones())
         builder.location(findLocationForTemplate.apply(template));

      return builder.build();
   }

   @Singleton
   public static class FindLocationForTemplate extends FindResourceInSet<Template, Location> {

      @Inject
      public FindLocationForTemplate(@Memoized Supplier<Set<? extends Location>> location) {
         super(location);
      }

      @Override
      public boolean matches(Template from, Location input) {
         return input.getId().equals(Long.toString(from.getZoneId()));
      }
   }
}
