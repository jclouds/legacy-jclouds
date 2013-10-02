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
package org.jclouds.atmos.functions;

import static com.google.common.io.BaseEncoding.base16;
import static org.testng.Assert.assertEquals;

import org.jclouds.atmos.domain.FileType;
import org.jclouds.atmos.domain.SystemMetadata;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.testng.annotations.Test;

import com.google.inject.Guice;

/**
 * Tests behavior of {@code ParseSystemMetadataFromHeaders}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseSystemMetadataFromHeadersTest {
   static final DateService dateService = new SimpleDateFormatDateService();
   static final SystemMetadata EXPECTED = new SystemMetadata(base16().lowerCase().decode("1f3870be274f6c49b3e31a0c6728957f"),
   dateService.iso8601SecondsDateParse("2009-10-12T16:09:42Z"),
         dateService.iso8601SecondsDateParse("2009-10-19T04:37:00Z"), "rootr",
         dateService.iso8601SecondsDateParse("2009-10-12T16:09:42Z"),
         dateService.iso8601SecondsDateParse("2009-10-19T04:37:00Z"), 1,
         "4980cdb2b010109b04a44f7bb83f5f04ad354c638ae5", "e913e09366364e9ba384b8fead643d43", "default", 4096l,
         FileType.DIRECTORY, "root"

   );

   public void test() {
      ParseSystemMetadataFromHeaders parser = Guice.createInjector().getInstance(ParseSystemMetadataFromHeaders.class);

      SystemMetadata data = parser.apply(ParseObjectFromHeadersAndHttpContentTest.RESPONSE);

      assertEquals(data, EXPECTED);
   }
}
