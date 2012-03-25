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
package org.jclouds.aws.ec2.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.util.ComputeServiceUtils;
import org.jclouds.ec2.compute.strategy.ReviseParsedImage;
import org.jclouds.logging.Logger;

/**
 * @author Adrian Cole
 */
@Singleton
public class AWSEC2ReviseParsedImage implements ReviseParsedImage {

   // 137112412989/amzn-ami-0.9.7-beta.i386-ebs
   // 137112412989/amzn-ami-0.9.7-beta.x86_64-ebs
   // amzn-ami-us-east-1/amzn-ami-0.9.7-beta.x86_64.manifest.xml
   // amzn-ami-us-east-1/amzn-ami-0.9.7-beta.i386.manifest.xml
   public static final Pattern AMZN_PATTERN = Pattern
            .compile(".*/(amzn-hvm-|amzn-)?ami-(.*)\\.(i386|x86_64)(-ebs|\\.manifest.xml)?");

   // amazon/EC2 CentOS 5.4 HVM AMI
   public static final Pattern AMAZON_PATTERN = Pattern.compile("amazon/EC2 ([^ ]+) ([^ ]+).*");

   // amazon/Windows_Server-2008-R2_SP1-English-64Bit-Base-2012.03.13
   //        1111111        22222222222                    3333333333
   public static final Pattern AMAZON_WINDOWS_PATTERN = Pattern.compile(".*/(Windows)_Server-([^-]*-[^-]*)-.*-([^-]*)(\\.manifest.xml)?");

   public static final Pattern CANONICAL_PATTERN = Pattern.compile(".*/([^-]*)-([^-]*)-.*-(.*)(\\.manifest.xml)?");

   // ex rightscale-us-east/CentOS_5.4_x64_v4.4.10.manifest.xml
   public static final Pattern RIGHTSCALE_PATTERN = Pattern
            .compile("[^/]*/([^_]*)_([^_]*)_[^vV]*[vV](.*)(\\.manifest.xml)?");

   // ex 411009282317/RightImage_Ubuntu_9.10_x64_v4.5.3_EBS_Alpha
   // 411009282317/RightImage_Windows_2008_x64_v5.5.5
   public static final Pattern RIGHTIMAGE_PATTERN = Pattern
            .compile("[^/]*/RightImage[_ ]([^_]*)_([^_]*)_[^vV]*[vV](.*)(\\.manifest.xml)?");
   private final Map<OsFamily, Map<String, String>> osVersionMap;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public AWSEC2ReviseParsedImage(Map<OsFamily, Map<String, String>> osVersionMap) {
      this.osVersionMap = checkNotNull(osVersionMap, "osVersionMap");
   }

   @Override
   public void reviseParsedImage(org.jclouds.ec2.domain.Image from, ImageBuilder builder, OsFamily family,
            OperatingSystem.Builder osBuilder) {
      try {
         Matcher matcher = getMatcherAndFind(from.getImageLocation());
         if (matcher.pattern() == AMZN_PATTERN) {
            osBuilder.family(OsFamily.AMZN_LINUX);
            osBuilder.version(matcher.group(2));
            builder.version(matcher.group(2));
         } else if (matcher.pattern() == AMAZON_PATTERN) {
            family = OsFamily.fromValue(matcher.group(1));
            osBuilder.family(family);
            osBuilder.version(ComputeServiceUtils.parseVersionOrReturnEmptyString(family, matcher.group(2),
                     osVersionMap));
         } else {
            family = OsFamily.fromValue(matcher.group(1));
            osBuilder.family(family);
            osBuilder.version(ComputeServiceUtils.parseVersionOrReturnEmptyString(family, matcher.group(2),
                     osVersionMap));
            builder.version(matcher.group(3).replace(".manifest.xml", ""));
         }
      } catch (IllegalArgumentException e) {
         logger.debug("<< didn't match os(%s)", from.getImageLocation());
      } catch (NoSuchElementException e) {
         logger.trace("<< didn't match at all(%s)", from.getImageLocation());
      }
   }

   /**
    * 
    * @throws NoSuchElementException
    *            if no configured matcher matches the manifest.
    */
   private Matcher getMatcherAndFind(String manifest) {
      for (Pattern pattern : new Pattern[] { AMZN_PATTERN, AMAZON_PATTERN, AMAZON_WINDOWS_PATTERN, CANONICAL_PATTERN,
              RIGHTIMAGE_PATTERN, RIGHTSCALE_PATTERN }) {
         Matcher matcher = pattern.matcher(manifest);
         if (matcher.find())
            return matcher;
      }
      throw new NoSuchElementException(manifest);
   }
}