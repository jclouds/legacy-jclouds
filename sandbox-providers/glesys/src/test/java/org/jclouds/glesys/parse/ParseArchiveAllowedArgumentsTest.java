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


import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.glesys.config.GleSYSParserModule;
import org.jclouds.glesys.domain.ArchiveAllowedArguments;
import org.jclouds.glesys.domain.ServerAllowedArguments;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ParseServerAllowedArgumentsTest")
public class ParseArchiveAllowedArgumentsTest extends BaseItemParserTest<ArchiveAllowedArguments> {

    @Override
    public String resource() {
        return "/archive_allowed_arguments.json";
    }

    @Override
    @SelectJson("argumentslist")
    @Consumes(MediaType.APPLICATION_JSON)
    public ArchiveAllowedArguments expected() {
       return ArchiveAllowedArguments.builder().archiveSizes(new Integer[] {
             10,20,30,40,50,60,70,80,90,100,125,150,175,200,225,250,275,300,325,350,375,400,425,450,475,500,550,600,650,700,750,800,850,900,950,1000
       }).build();
    }


    protected Injector injector() {
        return Guice.createInjector(new GleSYSParserModule(), new GsonModule());
    }
}
