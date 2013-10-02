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
package org.jclouds.glesys.reference;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jclouds.compute.domain.ComputeMetadata;

/**
 * Configuration properties and constants in GleSYS connections.
 * 
 * @author Adam Lowe
 */
public class GleSYSConstants {

   public static final Pattern JCLOUDS_ID_TO_PLATFORM = Pattern.compile("([a-zA-Z]+) .*");
   
   public static String getPlatform(ComputeMetadata jcloudsObject) {
      checkNotNull(jcloudsObject, "jcloudsObject");
      Matcher matcher = JCLOUDS_ID_TO_PLATFORM.matcher(jcloudsObject.getId());
      if (!matcher.matches()) {
         throw new IllegalArgumentException(jcloudsObject.getId() + " not a GleSYS platform-based id!");
      }
      return matcher.group(1);
   }
}
