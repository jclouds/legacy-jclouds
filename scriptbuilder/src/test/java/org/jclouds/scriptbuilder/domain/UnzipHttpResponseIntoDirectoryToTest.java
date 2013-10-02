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
package org.jclouds.scriptbuilder.domain;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class UnzipHttpResponseIntoDirectoryToTest {

   UnzipHttpResponseIntoDirectory jboss = new UnzipHttpResponseIntoDirectory(
            "GET",
            URI
                     .create("http://superb-sea2.dl.sourceforge.net/project/jboss/JBoss/JBoss-5.0.0.CR2/jboss-5.0.0.CR2-jdk6.zip"),
            ImmutableMultimap.<String, String> of(), "/tmp");

   public void testUnzipHttpResponseIntoDirectoryUNIX() {
      assertEquals(
               jboss.render(OsFamily.UNIX),
               "(mkdir -p /tmp &&cd /tmp &&curl -X -L GET -s --retry 20  http://superb-sea2.dl.sourceforge.net/project/jboss/JBoss/JBoss-5.0.0.CR2/jboss-5.0.0.CR2-jdk6.zip >extract.zip && unzip -o -qq extract.zip&& rm extract.zip)\n");
   }
   public void testUnzipHttpResponseIntoDirectoryWINDOWS() {
     
               jboss.render(OsFamily.WINDOWS);  }
}
