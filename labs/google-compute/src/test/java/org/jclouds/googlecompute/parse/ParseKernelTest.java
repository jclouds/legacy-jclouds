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

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.googlecompute.domain.Kernel;
import org.jclouds.googlecompute.internal.BaseGoogleComputeParseTest;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.net.URI;

/**
 * @author David Alves
 */
@Test(groups = "unit")
public class ParseKernelTest extends BaseGoogleComputeParseTest<Kernel> {

   @Override
   public String resource() {
      return "/kernel.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Kernel expected() {
      return Kernel.builder()
              .id("12941177846308850718")
              .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2012-07-16T21:42:16.950"))
              .selfLink(URI.create("https://www.googleapis.com/compute/v1beta13/projects/google/kernels/gce-20110524"))
              .name("gce-20110524")
              .description("DEPRECATED. Created Tue, 24 May 2011 00:48:22 +0000")
              .build();
   }
}
