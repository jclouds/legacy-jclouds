/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.codegen.model;

import java.util.List;
import java.util.Map;

/**
 * @author Adrian Cole
 * @author James Murty
 */
public class API {
   private List<Package> packages;
   private Map<String, DomainType> domain;

   @Override
   public String toString() {
      return getPackages().toString();
   }

   public void setPackages(List<Package> packages) {
      this.packages = packages;
   }

   public List<Package> getPackages() {
      return packages;
   }

   public void setDomain(Map<String, DomainType> domain) {
      this.domain = domain;
   }

   public Map<String, DomainType> getDomain() {
      return domain;
   }
}
