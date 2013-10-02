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
package org.jclouds.date;

import java.util.Date;

import org.jclouds.date.internal.SimpleDateFormatDateService;

import com.google.inject.ImplementedBy;

/**
 * Parses and formats the ISO8601, C, and RFC822 date formats found in XML responses and HTTP
 * response headers.
 * 
 * @author Adrian Cole
 * @author James Murty
 */
@ImplementedBy(SimpleDateFormatDateService.class)
public interface DateService {

   String cDateFormat(Date date);

   String cDateFormat();

   /**
    * @param toParse text to parse
    * @return parsed date
    * @throws IllegalArgumentException if the input is invalid
    */
   Date cDateParse(String toParse) throws IllegalArgumentException;

   String rfc822DateFormat(Date date);

   String rfc822DateFormat();
   
   /**
    * @param toParse text to parse
    * @return parsed date
    * @throws IllegalArgumentException if the input is invalid
    */
   Date rfc822DateParse(String toParse) throws IllegalArgumentException;
   
   String iso8601SecondsDateFormat(Date dateTime);

   String iso8601SecondsDateFormat();

   String iso8601DateFormat(Date date);

   String iso8601DateFormat();
   
   /**
    * @param toParse text to parse
    * @return parsed date
    * @throws IllegalArgumentException if the input is invalid
    */
   Date iso8601DateParse(String toParse) throws IllegalArgumentException;

   /**
    * @param toParse text to parse
    * @return parsed date
    * @throws IllegalArgumentException if the input is invalid
    */
   Date iso8601SecondsDateParse(String toParse) throws IllegalArgumentException;

   String rfc1123DateFormat(Date date);

   String rfc1123DateFormat();

   /**
    * @param toParse text to parse
    * @return parsed date
    * @throws IllegalArgumentException if the input is invalid
    */
   Date rfc1123DateParse(String toParse) throws IllegalArgumentException;

}
