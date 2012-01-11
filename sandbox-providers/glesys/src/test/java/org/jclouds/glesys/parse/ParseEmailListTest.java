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
package org.jclouds.glesys.parse;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.glesys.config.GleSYSParserModule;
import org.jclouds.glesys.domain.Email;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Set;

/**
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ParseEmailListTest")
public class ParseEmailListTest extends BaseSetParserTest<Email> {

   @Override
   public String resource() {
      return "/email_list.json";
   }

   @Override
   @SelectJson("emailaccounts")
   @Consumes(MediaType.APPLICATION_JSON)
   public Set<Email> expected() {
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      Email.Builder builder = Email.builder().quota("200 MB").usedQuota("0 MB").antispamLevel(3).antiVirus(true).autoRespond(false).autoRespondSaveEmail(true).autoRespondMessage("false");
      try {
         return ImmutableSet.of(
               builder.account("test@adamlowe.net").created(dateFormat.parse("2011-12-22T12:13:14")).modified(dateFormat.parse("2011-12-22T12:13:35")).build(),
               builder.account("test2@adamlowe.net").created(dateFormat.parse("2011-12-22T12:14:29")).modified(dateFormat.parse("2011-12-22T12:14:31")).build()
         );
      } catch(ParseException ex) {
         throw new RuntimeException(ex);
      }
   }

   protected Injector injector() {
      return Guice.createInjector(new GleSYSParserModule(), new GsonModule());
   }

}
