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
package org.jclouds.simpledb.samples;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.jclouds.aws.domain.Region;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.simpledb.SimpleDBAsyncClient;
import org.jclouds.simpledb.SimpleDBClient;
import org.jclouds.simpledb.domain.AttributePair;
import org.jclouds.simpledb.domain.Item;
import org.jclouds.simpledb.options.ListDomainsOptions;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;


/**
 * This the Main class of an Application that demonstrates the use of the simpledb.
 * 
 * Usage is: java MainApp \"accesskeyid\" \"secretkey\" 
 * 
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * 
 */
public class MainApp 
{

   public static int PARAMETERS = 3;
   public static String INVALID_SYNTAX = "Invalid number of parameters. Syntax is: \"accesskeyid\" \"secretkey\" \"bucketName\" ";

   public static void main(String[] args) throws IOException {


      // Args
      String accesskeyid = args[0];
      String secretkey = args[1];
      Properties properties = new Properties();
      properties.setProperty("simpledb.identity", accesskeyid);
      properties.setProperty("simpledb.credential", secretkey);

      
      RestContext<SimpleDBClient, SimpleDBAsyncClient> context = new RestContextFactory().createContext("simpledb", "AKIAJODKICBEKG7MM4XA", "FfqiNSiC88B6tJPDIOKUWUJGY68BQaQpkNz6Fsgq", new Properties());
      AttributePair p = new AttributePair("AccessNumber", "1213123", true);
      Multimap<String,AttributePair> m =LinkedHashMultimap.create();
      m.put("AccessNumber", p);
      Item attributes = new Item(m);
      
      // Use Provider API
      context.getApi().putAttributes(Region.EU_WEST_1, "tse", "AccessNumber", attributes );   
      //context.getApi().createDomainInRegion(Region.EU_WEST_1, "tse");
      Map<String, Item> results = context.getApi().select(Region.EU_WEST_1, "select * from tse");
      System.out.println(results);
      ListDomainsOptions [] list = new  ListDomainsOptions[100]; 
      //context.getApi().listDomainsInRegion(Region.EU_WEST_1, list);
      System.out.println(list[0]);
      context.close();
   }
}
