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
package org.jclouds.glesys.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Image.Status;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OperatingSystem.Builder;
import org.jclouds.compute.domain.OsFamilyVersion64Bit;
import org.jclouds.glesys.domain.OSTemplate;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class OSTemplateToImage implements Function<OSTemplate, Image> {
   private final Function<String, OsFamilyVersion64Bit> osParser;

   @Inject
   public OSTemplateToImage(Function<String, OsFamilyVersion64Bit> osParser) {
      this.osParser = osParser;
   }

   @Override
   public Image apply(OSTemplate template) {
      checkNotNull(template, "template");
      OsFamilyVersion64Bit parsed = osParser.apply(template.getName());
      Builder builder = OperatingSystem.builder();
      builder.name(template.getName()).description(template.getName()).is64Bit(parsed.is64Bit).version(parsed.version)
               .family(parsed.family);
      return new ImageBuilder().ids(template.getName()).name(template.getName()).description(template.getName())
            .operatingSystem(builder.build()).status(Status.AVAILABLE).build();
   }
}
