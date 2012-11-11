/*
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

package org.jclouds.googlecompute.parse;

import com.google.common.collect.ImmutableMap;
import org.jclouds.googlecompute.config.GoogleComputeParserModule;
import org.jclouds.googlecompute.internal.BaseGoogleComputeParseTest;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

/**
 * @author David Alves
 */
@Test(groups = "unit")
public class ParseMetadataTest extends BaseGoogleComputeParseTest<GoogleComputeParserModule.Metadata> {

   @Override
   public String resource() {
      return "/metadata.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public GoogleComputeParserModule.Metadata expected() {
      return new GoogleComputeParserModule.Metadata(
              ImmutableMap.<String, String>builder()
                      .put("propA", "valueA")
                      .put("propB", "valueB")
                      .build());
   }
}
