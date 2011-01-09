/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vcloud.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseOsFamilyOrNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OperatingSystemBuilder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.compute.util.ComputeServiceUtils;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VCloudExpressVAppTemplate;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
public class ImageForVCloudExpressVAppTemplate implements Function<VCloudExpressVAppTemplate, Image> {
   private final FindLocationForResource findLocationForResource;
   private final PopulateDefaultLoginCredentialsForImageStrategy credentialsProvider;
   private ReferenceType parent;

   private final Map<OsFamily, Map<String, String>> osVersionMap;

   @Inject
   protected ImageForVCloudExpressVAppTemplate(FindLocationForResource findLocationForResource,
         PopulateDefaultLoginCredentialsForImageStrategy credentialsProvider,
         Map<OsFamily, Map<String, String>> osVersionMap) {
      this.osVersionMap = osVersionMap;
      this.findLocationForResource = checkNotNull(findLocationForResource, "findLocationForResource");
      this.credentialsProvider = checkNotNull(credentialsProvider, "credentialsProvider");
   }

   public ImageForVCloudExpressVAppTemplate withParent(ReferenceType parent) {
      this.parent = parent;
      return this;
   }

   @Override
   public Image apply(VCloudExpressVAppTemplate from) {
      ImageBuilder builder = new ImageBuilder();
      builder.ids(from.getHref().toASCIIString());
      builder.uri(from.getHref());
      builder.name(from.getName());
      builder.location(findLocationForResource.apply(checkNotNull(parent, "parent")));
      builder.description(from.getDescription() != null ? from.getDescription() : from.getName());
      builder.operatingSystem(parseOs(from));
      builder.defaultCredentials(credentialsProvider.execute(from));
      return builder.build();
   }

   public static final Pattern OS_PATTERN = Pattern.compile("(([^ ]*) ([0-9.]+) ?.*)");

   protected OperatingSystem parseOs(VCloudExpressVAppTemplate from) {
      OperatingSystemBuilder builder = new OperatingSystemBuilder();
      OsFamily osFamily = parseOsFamilyOrNull("vcloudexpress", checkNotNull(from, "vapp template").getName());
      builder.family(osFamily);
      builder.description(from.getName());
      builder.is64Bit(from.getName().indexOf("64") != -1);
      Matcher matcher = OS_PATTERN.matcher(from.getName());
      if (matcher.find()) {
         builder.version(ComputeServiceUtils.parseVersionOrReturnEmptyString(osFamily, matcher.group(3), osVersionMap));
      }
      return builder.build();
   }

}