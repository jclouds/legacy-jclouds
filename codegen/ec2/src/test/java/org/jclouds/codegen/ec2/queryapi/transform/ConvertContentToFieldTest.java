/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.codegen.ec2.queryapi.transform;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

/**
 * Tests to ensure that we can transform EC2 {@link Content contents} to jclouds {@link Field
 * fields}
 * 
 * @author Adrian Cole
 */
@Test(testName = "ec2.ConvertContentToFieldTest")
public class ConvertContentToFieldTest {

   // sub parse_java_type {
   // $_ = shift;
   // s/xsd:string/String/;
   // s/xsd:boolean/Boolean/;
   // s/xsd:Int/Integer/;
   // s/xsd:dateTime/DateTime/;
   // if (/Type/ || /Item/ || /Info/) {
   // my $awsType = $_;
   // my $javaType = get_java_name($awsType);
   // #if ( !/Response/ ) {
   // $domain->{$awsType} = {
   // awsType => $awsType,
   // javaType => $javaType,
   // packageName => $domain_package,
   // className => $domain_package . "." . $javaType,
   // see => ["${refUrl}/ApiReference-ItemType-${awsType}.html"],
   // fields =>
   // build_fields("${refUrl}/ApiReference-ItemType-$awsType.html")
   // };
   // #}
   // $_ = $javaType;
   // }
   //
   // return $_;
   // }
   //
   // sub get_java_name {
   // $_ = shift;
   // if (/sSetType/) {
   // s/sSetType//;
   // return "Set<$_>";
   // }
   // if (/ListType/) {
   // s/ListType//;
   // return "List<$_>";
   // }
   // if (/sResponseInfoType/){
   // s/sResponseInfoType//;
   // return "Set<$_>";
   // }
   // if (/sSetItemType/) {
   // s/sSetItemType//;
   // }
   // if (/sResponseItemType/){
   // s/sResponseItemType//;
   // }
   // if (/sItemType/) {
   // s/sItemType//;
   // }
   // if (/sSet/) {
   // s/sSet//;
   // }
   // if (/Set/) {
   // s/Set//;
   // }
   // if (/Type/) {
   // s/Type//;
   // }
   // if (/Item/) {
   // s/Item//;
   // }
   // if (/Info/) {
   // s/Info//;
   // }
   // return $_;
   // }

   @Test
   void testParseJavaTypeForxsd_string() {
      ConvertContentToField converter = new ConvertContentToField();
      assertEquals(converter.parseJavaType("xsd:string"), "String");
   }

   @Test
   void testParseJavaTypeForxsd_Int() {
      ConvertContentToField converter = new ConvertContentToField();
      assertEquals(converter.parseJavaType("xsd:Int"), "Integer");
   }

   @Test
   void testParseJavaTypeForxsd_boolean() {
      ConvertContentToField converter = new ConvertContentToField();
      assertEquals(converter.parseJavaType("xsd:boolean"), "Boolean");
   }

   @Test
   void testParseJavaTypeForxsd_dateTime() {
      ConvertContentToField converter = new ConvertContentToField();
      assertEquals(converter.parseJavaType("xsd:dateTime"), "org.joda.time.DateTime");
   }

}
