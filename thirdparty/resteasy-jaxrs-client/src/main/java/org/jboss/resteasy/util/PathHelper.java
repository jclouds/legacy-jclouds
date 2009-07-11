/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
/**
 *
 */
package org.jboss.resteasy.util;

import java.util.regex.Pattern;


/**
 * A utility class for handling URI template parameters. As the Java
 * regulare expressions package does not handle named groups, this
 * class attempts to simulate that functionality by using groups.
 *
 * @author Ryan J. McDonough
 * @author Bill Burke
 * @since 1.0
 *        Nov 8, 2006
 */
public class PathHelper
{
   public static final String URI_PARAM_NAME_REGEX = "\\w[\\w\\.-]*";
   public static final String URI_PARAM_REGEX_REGEX = "[^{}][^{}]*";
   public static final String URI_PARAM_REGEX = "\\{\\s*(" + URI_PARAM_NAME_REGEX + ")\\s*(:\\s*(" + URI_PARAM_REGEX_REGEX + "))?\\}";
   public static final String URI_PARAM_WITH_REGEX = "\\{\\s*(" + URI_PARAM_NAME_REGEX + ")\\s*(:\\s*(" + URI_PARAM_REGEX_REGEX + "))\\}";
   public static final String URI_PARAM_WITHOUT_REGEX = "\\{(" + URI_PARAM_NAME_REGEX + ")\\}";
   public static final Pattern URI_PARAM_PATTERN = Pattern.compile(URI_PARAM_REGEX);
   public static final Pattern URI_PARAM_WITH_REGEX_PATTERN = Pattern.compile(URI_PARAM_WITH_REGEX);
   public static final Pattern URI_PARAM_WITHOUT_REGEX_PATTERN = Pattern.compile(URI_PARAM_WITHOUT_REGEX);

   /**
    * A regex pattern that searches for a URI template parameter in the form of {*}
    */
   public static final Pattern URI_TEMPLATE_PATTERN = Pattern.compile("(\\{([^}]+)\\})");

   public static final String URI_TEMPLATE_REPLACE_PATTERN = "(.*?)";


   public static String getEncodedPathInfo(String path, String contextPath)
   {
      if (contextPath != null && !"".equals(contextPath) && path.startsWith(contextPath))
      {
         path = path.substring(contextPath.length());
      }
      return path;

   }

   public static final char openCurlyReplacement = 6;
   public static final char closeCurlyReplacement = 7;

   public static String replaceEnclosedCurlyBraces(String str)
   {
      char[] chars = str.toCharArray();
      int open = 0;
      for (int i = 0; i < chars.length; i++)
      {
         if (chars[i] == '{')
         {
            if (open != 0) chars[i] = openCurlyReplacement;
            open++;
         }
         else if (chars[i] == '}')
         {
            open--;
            if (open != 0)
            {
               chars[i] = closeCurlyReplacement;
            }
         }
      }
      return new String(chars);
   }

   public static String recoverEnclosedCurlyBraces(String str)
   {
      return str.replace(openCurlyReplacement, '{').replace(closeCurlyReplacement, '}');
   }

   public static void main(String[] args) throws Exception
   {
      String str = replaceEnclosedCurlyBraces("{hello{world}}  {foo : {bar}{{blah}}}");
      System.out.println(str);
      System.out.println(recoverEnclosedCurlyBraces(str));
   }

}