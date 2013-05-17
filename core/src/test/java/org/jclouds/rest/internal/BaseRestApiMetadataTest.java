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
package org.jclouds.rest.internal;
import static org.jclouds.reflect.Reflection2.typeToken;

import java.util.Set;

import org.jclouds.View;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.Apis;
import org.jclouds.apis.internal.BaseApiMetadataTest;
import org.jclouds.rest.RestApiMetadata;
import org.jclouds.rest.RestContext;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public abstract class BaseRestApiMetadataTest extends BaseApiMetadataTest {

   public BaseRestApiMetadataTest(RestApiMetadata toTest, Set<TypeToken<? extends View>> views) {
     super(toTest, views);
   }

   @Test
   public void testContextAssignableFromRestContext() {
      Set<ApiMetadata> all = ImmutableSet.copyOf(Apis.contextAssignableFrom(typeToken(RestContext.class)));
      assert all.contains(toTest) : String.format("%s not found in %s", toTest, all);
   }

}
