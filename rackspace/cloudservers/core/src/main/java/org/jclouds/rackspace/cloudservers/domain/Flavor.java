/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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

package org.jclouds.rackspace.cloudservers.domain;

public class Flavor {

    public Flavor(int id, String name) {
      super();
      this.id = id;
      this.name = name;
   }

   protected Integer disk;
    protected int id;
    protected String name;
    protected Integer ram;

  
    public Integer getDisk() {
        return disk;
    }

    public void setDisk(Integer value) {
        this.disk = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int value) {
        this.id = value;
    }
    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public Integer getRam() {
        return ram;
    }

    public void setRam(Integer value) {
        this.ram = value;
    }

}
