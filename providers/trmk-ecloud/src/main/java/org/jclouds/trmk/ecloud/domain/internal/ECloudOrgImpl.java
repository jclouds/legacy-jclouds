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
package org.jclouds.trmk.ecloud.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import org.jclouds.trmk.ecloud.domain.ECloudOrg;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.internal.OrgImpl;

/**
 * Locations of resources in a Terremark eCloud Org
 * 
 * @author Adrian Cole
 * 
 */
public class ECloudOrgImpl extends OrgImpl implements ECloudOrg {

   private final ReferenceType dataCentersList;
   private final ReferenceType deviceTags;
   private final ReferenceType vAppCatalog;

   public ECloudOrgImpl(String name, String type, URI id, String description, Map<String, ReferenceType> catalogs,
         Map<String, ReferenceType> vdcs, Map<String, ReferenceType> tasksLists, ReferenceType keys,
         ReferenceType dataCentersList, ReferenceType deviceTags, ReferenceType vAppCatalog) {
      super(name, type, id, description, catalogs, vdcs, tasksLists, keys);
      this.deviceTags = checkNotNull(deviceTags, "deviceTags");
      this.vAppCatalog = checkNotNull(vAppCatalog, "vAppCatalog");
      this.dataCentersList = checkNotNull(dataCentersList, "dataCentersList");
   }

   @Override
   public ReferenceType getDataCenters() {
      return dataCentersList;
   }

   @Override
   public ReferenceType getTags() {
      return deviceTags;
   }

   @Override
   public ReferenceType getVAppCatalog() {
      return vAppCatalog;
   }

}
