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
package org.jclouds.compute.domain;

import org.jclouds.compute.internal.TemplateBuilderImpl;
import org.jclouds.compute.options.TemplateOptions;

import com.google.inject.ImplementedBy;

/**
 * Creates a customized template based on requirements.
 * 
 * @author Adrian Cole
 */
@ImplementedBy(TemplateBuilderImpl.class)
public interface TemplateBuilder {
   /**
    * Configure this template to require the minimum size of the parameter.
    */
   TemplateBuilder fromSize(Size size);

   /**
    * Configure this template to fuzzy-match on the image parameter
    */
   TemplateBuilder fromImage(Image image);

   /**
    * Configure this template to match the resources of the template parameter.
    */
   TemplateBuilder fromTemplate(Template image);

   /**
    * configure this template to the smallest size.
    */
   TemplateBuilder smallest();

   /**
    * configure this template to the fastest size.
    */
   TemplateBuilder fastest();

   /**
    * configure this template to the largest size.
    */
   TemplateBuilder biggest();

   /**
    * Configure this template to use a specific operating system image.
    */
   TemplateBuilder osFamily(OsFamily os);

   /**
    * Configure this template to start in a specific location
    */
   TemplateBuilder locationId(String locationId);

   /**
    * Configure this template to require a specific architecture
    */
   TemplateBuilder architecture(Architecture architecture);

   /**
    * Configure this template to require a specific imageId.
    * <p/>
    * Note that image Ids are often scoped to {@code location}
    */
   TemplateBuilder imageId(String imageId);

   /**
    * Configure this template to require a specific sizeId.
    */
   TemplateBuilder sizeId(String sizeId);

   /**
    * Configure this template to have an operating system description that matches the regular
    * expression
    */
   TemplateBuilder osDescriptionMatches(String osDescriptionRegex);

   /**
    * Configure this template to have an image version that matches the regular expression
    */
   TemplateBuilder imageVersionMatches(String imageVersionRegex);

   /**
    * Configure this template to have a description that matches the regular expression
    */
   TemplateBuilder imageDescriptionMatches(String descriptionRegex);

   /**
    * Configure this template to require the minimum cores below
    */
   TemplateBuilder minCores(double minCores);

   /**
    * Configure this template to require the minimum ram in megabytes below
    */
   TemplateBuilder minRam(int megabytes);

   /**
    * Generate an immutable template from the current builder.
    */
   Template build();

   /**
    * options such as inbound ports and run scripts.
    */
   TemplateBuilder options(TemplateOptions options);
}