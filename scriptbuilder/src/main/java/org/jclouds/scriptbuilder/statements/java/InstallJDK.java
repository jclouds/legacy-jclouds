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
package org.jclouds.scriptbuilder.statements.java;

import static org.jclouds.scriptbuilder.domain.Statements.call;

import java.net.URI;

import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;

/**
 * Installs a default JDK to a host
 * 
 * @author Adrian Cole
 */
public class InstallJDK {

   public static Statement fromOpenJDK() {
      return new FromOpenJDK();
   }

   public static Statement fromURL(URI url) {
      return new FromURL(url);
   }

   public static class FromOpenJDK extends StatementList {

      public FromOpenJDK() {
         super(call("setupPublicCurl"), call("installOpenJDK"));
      }

   }

   public static class FromURL extends StatementList {

      public FromURL() {
         super(call("setupPublicCurl"), call("installJDKFromURL"));
      }

      public FromURL(URI jdk7Url) {
         super(call("setupPublicCurl"), call("installJDKFromURL", jdk7Url.toASCIIString()));
      }
   }
}
