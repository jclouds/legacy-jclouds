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
package org.jclouds.servermanager;

import com.google.common.base.Objects;

/**
 * This would be replaced with the real java object related to the underlying hardware
 * 
 * @author Adrian Cole
 */
public class Hardware {

   public int id;
   public String name;
   public int cores;
   public int ram;
   public float disk;

   public Hardware(int id, String name, int cores, int ram, float disk) {
      this.id = id;
      this.name = name;
      this.cores = cores;
      this.ram = ram;
      this.disk = disk;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, cores, ram, disk);
   }

   @Override
   public boolean equals(Object that) {
      if (that == null)
         return false;
      return Objects.equal(this.toString(), that.toString());
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("id", id).add("name", name).add("cores", cores).add("ram", ram)
            .add("disk", disk).toString();
   }

}
