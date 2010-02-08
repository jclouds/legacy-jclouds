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

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;

/**
 * 
 * @author Adrian Cole
 */
public class TemplateImpl implements Template {

   private final Image image;
   private final Size size;
   private final Location location;
   private final TemplateOptions options;

   public TemplateImpl(Image image, Size size, Location location, TemplateOptions options) {
      this.image = checkNotNull(image, "image");
      this.size = checkNotNull(size, "size");
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
   public Size getSize() {
      return size;
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
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((image == null) ? 0 : image.hashCode());
      result = prime * result + ((location == null) ? 0 : location.hashCode());
      result = prime * result + ((options == null) ? 0 : options.hashCode());
      result = prime * result + ((size == null) ? 0 : size.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      TemplateImpl other = (TemplateImpl) obj;
      if (image == null) {
         if (other.image != null)
            return false;
      } else if (!image.equals(other.image))
         return false;
      if (location == null) {
         if (other.location != null)
            return false;
      } else if (!location.equals(other.location))
         return false;
      if (options == null) {
         if (other.options != null)
            return false;
      } else if (!options.equals(other.options))
         return false;
      if (size == null) {
         if (other.size != null)
            return false;
      } else if (!size.equals(other.size))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[location=" + location + ", image=" + image + ", size=" + size + ", options="
               + options + "]";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected Object clone() throws CloneNotSupportedException {
      return new TemplateImpl(image, size, location, options);
   }

}
