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
package org.jclouds.compute.domain;

import org.jclouds.compute.domain.internal.TemplateBuilderImpl;
import org.jclouds.compute.options.TemplateOptions;

import com.google.common.annotations.Beta;
import com.google.common.base.Predicate;
import com.google.inject.ImplementedBy;

/**
 * Creates a customized template based on requirements.
 * 
 * @author Adrian Cole
 */
@ImplementedBy(TemplateBuilderImpl.class)
public interface TemplateBuilder {

   /**
    * prime this builder with parameters known to work on the current compute provider.
    */
   TemplateBuilder any();

   /**
    * Configure this template to require the minimum hardware of the parameter.
    */
   TemplateBuilder fromHardware(Hardware hardware);

   /**
    * Configure this template to fuzzy-match on the image parameter
    */
   TemplateBuilder fromImage(Image image);

   /**
    * Configure this template to match the resources of the template parameter.
    */
   TemplateBuilder fromTemplate(Template image);

   /**
    * Configure and return this template, updating it with the populated values of the given {@code spec}.
    *
    * @since 1.5
    */
   @Beta
   TemplateBuilder from(TemplateBuilderSpec spec);
   
   /**
    * Configure and return this template, updating it with the populated values of the given {@code spec}.
    * This is especially useful for command-line configuration of a {@code TemplateBuilder}.
    *
    * @param spec a String in the format specified by {@link TemplateBuilderSpec}
    * @since 1.5
    */
   @Beta
   TemplateBuilder from(String spec);
   
   /**
    * configure this template to the smallest hardware, based on cores, ram, then disk
    */
   TemplateBuilder smallest();

   /**
    * configure this template to the fastest hardware, based on cpu
    */
   TemplateBuilder fastest();

   /**
    * configure this template to the largest hardware, based on cores, ram, then disk
    */
   TemplateBuilder biggest();
   
   /**
    * Configure this template to have an hypervisor that matches the regular expression
    */
   TemplateBuilder hypervisorMatches(String hypervisorRegex);

   /**
    * Configure this template to use a specific operating system image.
    */
   TemplateBuilder osFamily(OsFamily os);

   /**
    * Configure this template to start in a specific location
    * 
    * @throws NoSuchElementException
    *            if no location matches the id specified
    */
   TemplateBuilder locationId(String locationId);

   /**
    * Configure this template to require a specific imageId.
    * <p/>
    * Note that image Ids are often scoped to {@code location}
    */
   TemplateBuilder imageId(String imageId);

   /**
    * Configure this template to require a specific hardwareId.
    */
   TemplateBuilder hardwareId(String hardwareId);

   /**
    * Configure this template to have an operating system name that matches the regular expression
    */
   TemplateBuilder osNameMatches(String osNameRegex);

   /**
    * Configure this template to have an operating system description that matches the regular
    * expression
    */
   TemplateBuilder osDescriptionMatches(String osDescriptionRegex);

   /**
    * Configure this template to have an os version that matches the regular expression
    */
   TemplateBuilder osVersionMatches(String osVersionRegex);

   /**
    * Configure this template to require a specific architecture. ex. virtualizationType or
    * 
    */
   TemplateBuilder osArchMatches(String architecture);

   /**
    * Configure this template to require a 64 bit operating system.
    */
   TemplateBuilder os64Bit(boolean is64bit);

   /**
    * Configure this template to have an image name that matches the regular expression
    */
   TemplateBuilder imageNameMatches(String imageNameRegex);

   /**
    * Configure this template to have an image version that matches the regular expression
    */
   TemplateBuilder imageVersionMatches(String imageVersionRegex);

   /**
    * Configure this template to have an image description that matches the regular expression
    */
   TemplateBuilder imageDescriptionMatches(String imageDescriptionRegex);

   /**
    * Configure this template to have an image description that matches the supplied condition
    * 
    * ex. 
    * 
    * <pre>
    * builder.imageMatches(ImagePredicates.userMetadataContains("author", "sue"));
    * </pre>
    */
   TemplateBuilder imageMatches(Predicate<Image> condition);

   /**
    * Configure this template to require the minimum cores below
    */
   TemplateBuilder minCores(double minCores);

   /**
    * Configure this template to require the minimum ram in megabytes below
    */
   TemplateBuilder minRam(int megabytes);

   /**
    * Configure this template with required minimum disk space in gigabytes
    */
   TemplateBuilder minDisk(double gigabytes);

   /**
    * Generate an immutable template from the current builder.
    */
   Template build();

   /**
    * Sets options on the template (i.e. items which adorn a created node rather than restricting selection of the node).
    * Note that this method typically replaces any options previously specified in the builder.
    * Normal usage is to build up all options and pass them to the builder with a single call to this method.
    */
   TemplateBuilder options(TemplateOptions options);

}
