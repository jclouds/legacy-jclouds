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
package org.jclouds.cloudstack.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.location.predicates.LocationPredicates.idEquals;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cloudstack.domain.Template;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Image.Status;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.domain.Location;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;

/**
 */
@Singleton
public class TemplateToImage implements Function<Template, Image> {
   private final Supplier<Set<? extends Location>> locations;
   private final Function<Template, OperatingSystem> templateToOperatingSystem;

   @Inject
   public TemplateToImage(@Memoized Supplier<Set<? extends Location>> locations,
         Function<Template, OperatingSystem> templateToOperatingSystem) {
      this.locations = checkNotNull(locations, "locations");
      this.templateToOperatingSystem = checkNotNull(templateToOperatingSystem, "templateToOperatingSystem");
   }

   @Override
   public Image apply(Template template) {
      checkNotNull(template, "template");

      OperatingSystem os = templateToOperatingSystem.apply(template);

      ImageBuilder builder = new ImageBuilder().ids(template.getId() + "").name(template.getName())
            .description(template.getDisplayText()).operatingSystem(os);

      if (!template.isCrossZones())
         builder.location(FluentIterable.from(locations.get()).firstMatch(idEquals(template.getZoneId())).orNull());

      //TODO: implement status mapping!!!
      builder.status(Status.AVAILABLE);
      return builder.build();
   }
}
