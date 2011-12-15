/**
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
package org.jclouds.glesys.domain;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Represents an 'uptime' duration of server in a Glesys cloud
 *
 * @author Adam Lowe
 * @see ServerStatus
 */
public class ServerUptime {
   private final long time;
   private final String timeString;

   private ServerUptime(long time) {
      this.time = time;
      long days = TimeUnit.SECONDS.toDays(time);
      long hours = TimeUnit.SECONDS.toHours(time - TimeUnit.DAYS.toSeconds(days));
      Long[] bits = new Long[]{
            0L,
            (days / 365),
            ((days % 365) / 30),
            ((days % 365) % 30),
            hours,
            TimeUnit.SECONDS.toMinutes(time - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.DAYS.toSeconds(days)),
            time % 60
      };
      this.timeString = Joiner.on(' ').join(bits);
   }

   private ServerUptime(String timeString) {
      Splitter splitter = Splitter.on(' ').omitEmptyStrings().trimResults();
      List<String> data = new ArrayList<String>();
      Iterables.addAll(data, splitter.split(timeString));
      long result = Integer.parseInt(data.get(6));
      result += TimeUnit.SECONDS.convert(Integer.parseInt(data.get(5)), TimeUnit.MINUTES);
      result += TimeUnit.SECONDS.convert(Integer.parseInt(data.get(4)), TimeUnit.HOURS);
      result += TimeUnit.SECONDS.convert(Integer.parseInt(data.get(3)), TimeUnit.DAYS);
      result += TimeUnit.SECONDS.convert(Integer.parseInt(data.get(2)) * 30, TimeUnit.DAYS);
      result += TimeUnit.SECONDS.convert(Integer.parseInt(data.get(1)) * 365, TimeUnit.DAYS);
      this.time = result;
      this.timeString = timeString;
   }

   /**
    * @param uptimeString a Glesys uptime string, ex. "0 0 0 0 0 10 1 1"
    */
   public static ServerUptime fromValue(String uptimeString) {
      return new ServerUptime(uptimeString);
   }

   /**
    * @param time number of seconds the server has been up
    */
   public static ServerUptime fromValue(long time) {
      return new ServerUptime(time);
   }

   public long getTime() {
      return time;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      return object instanceof ServerUptime
            && Objects.equal(time, ((ServerUptime) object).getTime());
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(time);
   }

   @Override
   public String toString() {
      return timeString;
   }

}