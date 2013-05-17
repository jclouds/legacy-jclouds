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
package org.jclouds.util;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.net.InetAddresses;

/**
 * 
 * 
 * @author Adrian Cole
 */
public class InetAddresses2 {
   @Singleton
   public static enum IsPrivateIPAddress implements Predicate<String> {
      INSTANCE;

      public boolean apply(String in) {
         if (InetAddresses.isInetAddress(checkNotNull(in, "input address"))) {
            // 24-bit Block (/8 prefix, 1/A) 10.0.0.0 10.255.255.255 16777216
            if (in.indexOf("10.") == 0)
               return true;
            // 20-bit Block (/12 prefix, 16/B) 172.16.0.0 172.31.255.255 1048576
            if (in.indexOf("172.") == 0) {
               int second = Integer.parseInt(Iterables.get(Splitter.on('.').split(in), 1));
               if (second >= 16 && second <= 31)
                  return true;
            }
            // 16-bit Block (/16 prefix, 256/C) 192.168.0.0 192.168.255.255 65536
            if (in.indexOf("192.168.") == 0)
               return true;
         }
         return false;
      }

      @Override
      public String toString() {
         return "isPrivateIPAddress()";
      }

   }

   /**
    * @return true if the input is an ip4 address and in one of the 3 reserved private blocks.
    */
   public static boolean isPrivateIPAddress(String in) {
      return IsPrivateIPAddress.INSTANCE.apply(in);
   }

}
