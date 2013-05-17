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
package org.jclouds.trmk.vcloud_0_8.compute.strategy;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.trmk.vcloud_0_8.domain.VAppTemplate;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class PopulateDefaultLoginCredentialsForVAppTemplateTest {

   @Test
   public void testRhel() throws IOException {
      InputStream is = getClass().getResourceAsStream("/rhel_description.txt");
      String description = new String(ByteStreams.toByteArray(is));
      VAppTemplate template = createMock(VAppTemplate.class);
      expect(template.getDescription()).andReturn(description).atLeastOnce();
      replay(template);
      ParseVAppTemplateDescriptionToGetDefaultLoginCredentials converter = new ParseVAppTemplateDescriptionToGetDefaultLoginCredentials(
               null, ImmutableMap.<String, Credentials> of(), ImmutableMap.<OsFamily, LoginCredentials> of());
      Credentials creds = converter.apply(template);
      assertEquals(creds.identity, "vcloud");
      assertEquals(creds.credential, "$Ep455l0ud!2");
      verify(template);
   }

   @Test
   public void testLamp() throws IOException {
      InputStream is = getClass().getResourceAsStream("/lamp_description.txt");
      String description = new String(ByteStreams.toByteArray(is));
      VAppTemplate template = createMock(VAppTemplate.class);
      expect(template.getDescription()).andReturn(description).atLeastOnce();
      replay(template);
      ParseVAppTemplateDescriptionToGetDefaultLoginCredentials converter = new ParseVAppTemplateDescriptionToGetDefaultLoginCredentials(
               null, ImmutableMap.<String, Credentials> of(), ImmutableMap.<OsFamily, LoginCredentials> of());
      Credentials creds = converter.apply(template);
      assertEquals(creds.identity, "ecloud");
      assertEquals(creds.credential, "$Ep455l0ud!2");
      verify(template);
   }

   @Test
   public void testFt() throws IOException {
      InputStream is = getClass().getResourceAsStream("/ft_description.txt");
      String description = new String(ByteStreams.toByteArray(is));
      VAppTemplate template = createMock(VAppTemplate.class);
      expect(template.getDescription()).andReturn(description).atLeastOnce();
      replay(template);
      ParseVAppTemplateDescriptionToGetDefaultLoginCredentials converter = new ParseVAppTemplateDescriptionToGetDefaultLoginCredentials(
               null, ImmutableMap.<String, Credentials> of(), ImmutableMap.<OsFamily, LoginCredentials> of());
      Credentials creds = converter.apply(template);
      assertEquals(creds.identity, "vpncubed");
      assertEquals(creds.credential, "vpncubed");
      verify(template);
   }

   @Test
   public void testEC() throws IOException {
      InputStream is = getClass().getResourceAsStream("/ec_description.txt");
      String description = new String(ByteStreams.toByteArray(is));
      VAppTemplate template = createMock(VAppTemplate.class);
      expect(template.getDescription()).andReturn(description).atLeastOnce();
      replay(template);
      ParseVAppTemplateDescriptionToGetDefaultLoginCredentials converter = new ParseVAppTemplateDescriptionToGetDefaultLoginCredentials(
               null, ImmutableMap.<String, Credentials> of(), ImmutableMap.<OsFamily, LoginCredentials> of());
      Credentials creds = converter.apply(template);
      assertEquals(creds.identity, "ecloud");
      assertEquals(creds.credential, "TmrkCl0ud1s#1!");
      verify(template);
   }

   @Test
   public void testWindows() throws IOException {
      InputStream is = getClass().getResourceAsStream("/windows_description.txt");
      String description = new String(ByteStreams.toByteArray(is));
      VAppTemplate template = createMock(VAppTemplate.class);
      expect(template.getDescription()).andReturn(description).atLeastOnce();
      replay(template);
      ParseVAppTemplateDescriptionToGetDefaultLoginCredentials converter = new ParseVAppTemplateDescriptionToGetDefaultLoginCredentials(
               null, ImmutableMap.<String, Credentials> of(), ImmutableMap.<OsFamily, LoginCredentials> of());
      Credentials creds = converter.apply(template);
      assertEquals(creds.identity, "Administrator");
      assertEquals(creds.credential, null);
      verify(template);
   }
}
