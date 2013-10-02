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
package org.jclouds.gogrid.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.SortedSet;

import javax.inject.Singleton;

import org.jclouds.gogrid.domain.Job;
import org.jclouds.gogrid.domain.JobProperties;
import org.jclouds.gogrid.domain.JobState;
import org.jclouds.gogrid.domain.ObjectType;
import org.jclouds.gogrid.domain.Option;
import org.jclouds.gogrid.functions.internal.CustomDeserializers;
import org.jclouds.http.HttpResponse;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * @author Oleksiy Yarmula
 */
//NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ParseJobsFromJsonResponseTest")
public class ParseJobsFromJsonResponseTest {

   @Test
   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/test_get_job_list.json");

      ParseJobListFromJsonResponse parser = i.getInstance(ParseJobListFromJsonResponse.class);
      SortedSet<Job> response = parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload(is).build());

      Map<String, String> details = ImmutableMap.of(
            "image", "GSI-f8979644-e646-4711-ad58-d98a5fa3612c",
            "ip", "204.51.240.189",
            "name", "ServerCreated40562",
            "type", "virtual_server");

      Job job = Job.builder().id(250628L).command(Option.createWithIdNameAndDescription(7L, "DeleteVirtualServer", "Delete Virtual Server"))
            .objectType(ObjectType.VIRTUAL_SERVER).createdOn(new Date(1267404528895L)).lastUpdatedOn(new Date(1267404538592L))
            .currentState(JobState.SUCCEEDED).attempts(1).owner("3116784158f0af2d-24076@api.gogrid.com").history(
                  JobProperties.builder().id(940263L).updatedOn(new Date(1267404528897L)).state(JobState.CREATED).build(),
                  JobProperties.builder().id(940264L).updatedOn(new Date(1267404528967L)).state(JobState.QUEUED).build())
            .details(details).build();
      assertEquals(job, Iterables.getOnlyElement(response));
   }

   Injector i = Guice.createInjector(new GsonModule() {
      @Override
      protected void configure() {
         bind(DateAdapter.class).to(LongDateAdapter.class);
         super.configure();
      }

      @Provides
      @Singleton
      public Map<Type, Object> provideCustomAdapterBindings() {
         Map<Type, Object> bindings = Maps.newHashMap();
         bindings.put(ObjectType.class, new CustomDeserializers.ObjectTypeAdapter());
         bindings.put(JobState.class, new CustomDeserializers.JobStateAdapter());
         return bindings;
      }
   });

}
