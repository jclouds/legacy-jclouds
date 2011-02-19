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

package org.jclouds.mezeo.pcs.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.date.DateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.mezeo.pcs.domain.ContainerList;
import org.jclouds.mezeo.pcs.domain.ResourceInfo;
import org.jclouds.mezeo.pcs.domain.internal.ContainerInfoImpl;
import org.jclouds.mezeo.pcs.domain.internal.ContainerListImpl;
import org.jclouds.mezeo.pcs.domain.internal.FileInfoImpl;
import org.jclouds.mezeo.pcs.xml.ContainerHandler;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;

/**
 * Tests behavior of {@code ContainerHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.ContainerHandlerTest")
public class ContainerHandlerTest extends BaseHandlerTest {

   private DateService dateService;

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
      dateService = injector.getInstance(DateService.class);
      assert dateService != null;
   }

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/test_root_container.xml");
      ImmutableSortedSet<? extends ResourceInfo> list = ImmutableSortedSet
               .of(
                        new ContainerInfoImpl(
                                 URI
                                          .create("https://pcsbeta.mezeo.net/v2/containers/7F143552-AAF5-11DE-BBB0-0BC388ED913B"),
                                 "test1",
                                 dateService.fromSeconds(1254008225),
                                 false,
                                 dateService.fromSeconds(1254008226),
                                 "adrian@jclouds.org",
                                 1,
                                 true,
                                 dateService.fromSeconds(1254008227),
                                 1024,
                                 URI
                                          .create("https://pcsbeta.mezeo.net/v2/containers/7F143552-AAF5-11DE-BBB0-0BC388ED913B/contents"),
                                 URI
                                          .create("https://pcsbeta.mezeo.net/v2/containers/7F143552-AAF5-11DE-BBB0-0BC388ED913B/tags"),
                                 URI
                                          .create("https://pcsbeta.mezeo.net/v2/containers/7F143552-AAF5-11DE-BBB0-0BC388ED913B/metadata"),
                                 URI
                                          .create("https://pcsbeta.mezeo.net/v2/containers/C4DA95C2-B298-11DE-8D7C-2B1FE4F2B99C")),
                        new FileInfoImpl(
                                 URI
                                          .create("https://pcsbeta.mezeo.net/v2/files/5C81DADC-AAEE-11DE-9D55-B39340AEFF3A"),
                                 "more",
                                 dateService.fromSeconds(1254005157),
                                 false,
                                 dateService.fromSeconds(1254005158),
                                 "adrian@jclouds.org",
                                 1,
                                 false,
                                 dateService.fromSeconds(1254005159),
                                 true,
                                 "application/octet-stream",
                                 254288,
                                 URI
                                          .create("https://pcsbeta.mezeo.net/v2/files/5C81DADC-AAEE-11DE-9D55-B39340AEFF3A/content"),
                                 URI
                                          .create("https://pcsbeta.mezeo.net/v2/containers/C4DA95C2-B298-11DE-8D7C-2B1FE4F2B99C"),
                                 URI
                                          .create("https://pcsbeta.mezeo.net/v2/files/5C81DADC-AAEE-11DE-9D55-B39340AEFF3A/permissions"),
                                 URI
                                          .create("https://pcsbeta.mezeo.net/v2/files/5C81DADC-AAEE-11DE-9D55-B39340AEFF3A/tags"),
                                 URI
                                          .create("https://pcsbeta.mezeo.net/v2/files/5C81DADC-AAEE-11DE-9D55-B39340AEFF3A/metadata"),
                                 URI
                                          .create("https://pcsbeta.mezeo.net/v2/files/5C81DADC-AAEE-11DE-9D55-B39340AEFF3A/thumbnail")),

                        new FileInfoImpl(
                                 URI
                                          .create("https://pcsbeta.mezeo.net/v2/files/9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3"),
                                 "testfile.txt",
                                 dateService.fromSeconds(1254000180),
                                 true,
                                 dateService.fromSeconds(1254000181),
                                 "adrian@jclouds.org",
                                 3,
                                 false,
                                 dateService.fromSeconds(1254000182),
                                 false,
                                 "text/plain",
                                 5,
                                 URI
                                          .create("https://pcsbeta.mezeo.net/v2/files/9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3/content"),
                                 URI
                                          .create("https://pcsbeta.mezeo.net/v2/containers/C4DA95C2-B298-11DE-8D7C-2B1FE4F2B99C"),
                                 URI
                                          .create("https://pcsbeta.mezeo.net/v2/files/9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3/permissions"),
                                 URI
                                          .create("https://pcsbeta.mezeo.net/v2/files/9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3/tags"),
                                 URI
                                          .create("https://pcsbeta.mezeo.net/v2/files/9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3/metadata"),
                                 URI
                                          .create("https://pcsbeta.mezeo.net/v2/files/9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3/thumbnail"))

               );

      ContainerList expects = new ContainerListImpl(
               URI
                        .create("https://pcsbeta.mezeo.net/v2/containers/C4DA95C2-B298-11DE-8D7C-2B1FE4F2B99C"),
               "/",
               dateService.fromSeconds(1254848007),
               false,
               dateService.fromSeconds(1255026455),
               "adrian@jclouds.org",
               0,
               false,
               dateService.fromSeconds(1255026455),
               0,
               list,
               URI
                        .create("https://pcsbeta.mezeo.net/v2/containers/C4DA95C2-B298-11DE-8D7C-2B1FE4F2B99C/tags"),
               URI
                        .create("https://pcsbeta.mezeo.net/v2/containers/C4DA95C2-B298-11DE-8D7C-2B1FE4F2B99C/metadata"),
               Maps.<String, URI> newHashMap(),
               URI
                        .create("https://pcsbeta.mezeo.net/v2/containers/C4DA95C2-B298-11DE-8D7C-2B1FE4F2B99C"));

      ContainerList result = (ContainerList) factory.create(
               injector.getInstance(ContainerHandler.class)).parse(is);

      assertEquals(result, expects);
   }
}
