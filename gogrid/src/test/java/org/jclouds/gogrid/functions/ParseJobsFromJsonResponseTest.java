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
package org.jclouds.gogrid.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.SortedSet;

import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.gogrid.config.GoGridContextModule;
import org.jclouds.gogrid.domain.Job;
import org.jclouds.gogrid.domain.JobProperties;
import org.jclouds.gogrid.domain.JobState;
import org.jclouds.gogrid.domain.ObjectType;
import org.jclouds.gogrid.domain.Option;
import org.jclouds.gogrid.functions.internal.CustomDeserializers;
import org.jclouds.http.functions.config.ParserModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * @author Oleksiy Yarmula
 */
@Test(groups = "unit", testName = "gogrid.ParseJobsFromJsonResponseTest")
public class ParseJobsFromJsonResponseTest {

   @Test
   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/test_get_job_list.json");

      ParseJobListFromJsonResponse parser = new ParseJobListFromJsonResponse(i
               .getInstance(Gson.class));
      SortedSet<Job> response = parser.apply(is);

      Map<String, String> details = Maps.newTreeMap();
      details.put("description", null);
      details.put("image", "GSI-f8979644-e646-4711-ad58-d98a5fa3612c");
      details.put("ip", "204.51.240.189");
      details.put("name", "ServerCreated40562");
      details.put("type", "virtual_server");

      Job job = new Job(250628L, new Option(7L, "DeleteVirtualServer", "Delete Virtual Server"),
               ObjectType.VIRTUAL_SERVER, new Date(1267404528895L), new Date(1267404538592L),
               JobState.SUCCEEDED, 1, "3116784158f0af2d-24076@api.gogrid.com", ImmutableSortedSet
                        .of(new JobProperties(940263L, new Date(1267404528897L), JobState.CREATED,
                                 null), new JobProperties(940264L, new Date(1267404528967L),
                                 JobState.QUEUED, null)), details);
      assertEquals(job, Iterables.getOnlyElement(response));
   }

   Injector i = Guice.createInjector(new ParserModule() {
      @Override
      protected void configure() {
         bind(DateAdapter.class).to(GoGridContextModule.DateSecondsAdapter.class);
         super.configure();
      }

      @SuppressWarnings( { "unused", "unchecked" })
      @Provides
      @Singleton
      @com.google.inject.name.Named(Constants.PROPERTY_GSON_ADAPTERS)
      public Map<Class, Object> provideCustomAdapterBindings() {
         Map<Class, Object> bindings = Maps.newHashMap();
         bindings.put(ObjectType.class, new CustomDeserializers.ObjectTypeAdapter());
         bindings.put(JobState.class, new CustomDeserializers.JobStateAdapter());
         return bindings;
      }
   });

}
