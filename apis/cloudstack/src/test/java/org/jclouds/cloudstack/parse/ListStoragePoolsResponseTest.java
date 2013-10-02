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
package org.jclouds.cloudstack.parse;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

import org.jclouds.cloudstack.config.CloudStackParserModule;
import org.jclouds.cloudstack.domain.StoragePool;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Richard Downer
 */
@Test(groups = "unit", testName = "ListStoragePoolsResponseTest")
public class ListStoragePoolsResponseTest extends BaseItemParserTest<Set<StoragePool>> {

   @Override
   public String resource() {
      return "/liststoragepoolsresponse.json";
   }

   @Override
   @SelectJson("storagepool")
   public Set<StoragePool> expected() {
      Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+02:00"));
      c.set(Calendar.YEAR, 2011);
      c.set(Calendar.MONTH, Calendar.NOVEMBER);
      c.set(Calendar.DAY_OF_MONTH, 26);
      c.set(Calendar.HOUR_OF_DAY, 23);
      c.set(Calendar.MINUTE, 33);
      c.set(Calendar.SECOND, 6);
      Date created = c.getTime();

      StoragePool storagePool = StoragePool.builder().id("201").zoneId("1").zoneName("Dev Zone 1").podId("1")
               .podName("Dev Pod 1").name("NFS Pri 1").ipAddress("10.26.26.165").path("/mnt/nfs/cs_pri")
               .created(created).type(StoragePool.Type.NETWORK_FILESYSTEM).clusterId("1").clusterName("Xen Clust 1")
               .diskSizeTotal(898356445184L).diskSizeAllocated(18276679680L).tag("tag1").state(StoragePool.State.UP)
               .build();
      return ImmutableSet.of(storagePool);
   }
   
   @Override
   protected Injector injector() {
      return Guice.createInjector(new GsonModule(), new CloudStackParserModule());
   }
}
