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
package org.jclouds.compute.domain.internal;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * 
 * @author Adrian Cole
 */
public class TemplateImpl implements Template {

   private final Image image;
   private final Hardware hardware;
   private final Location location;
   private final TemplateOptions options;

   public TemplateImpl(Image image, Hardware hardware, Location location, TemplateOptions options) {
      this.image = checkNotNull(image, "image");
      this.hardware = checkNotNull(hardware, "hardware");
      this.location = checkNotNull(location, "location");
      this.options = checkNotNull(options, "options");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Image getImage() {
      return image;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Hardware getHardware() {
      return hardware;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Location getLocation() {
      return location;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateOptions getOptions() {
      return options;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      TemplateImpl that = TemplateImpl.class.cast(o);
      return equal(this.image, that.image) && equal(this.hardware, that.hardware)
               && equal(this.location, that.location) && equal(this.options, that.options);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(image, hardware, location, options);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      ToStringHelper helper = Objects.toStringHelper("").omitNullValues().add("image", image).add("hardware", hardware)
               .add("location", location);
      if (!options.equals(defaultOptions()))
         helper.add("options", options);
      return helper;
   }

   protected TemplateOptions defaultOptions() {
      return TemplateOptions.NONE;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Template clone() {
      return new TemplateImpl(image, hardware, location, options.clone());
   }

}
