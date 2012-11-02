package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public class Role { //TODO: placeholder for implementation
   
   @XmlType
   @XmlEnum(String.class)
   public static enum DefaultRoles {
      @XmlEnumValue("vApp User") USER("vApp User"),
      @XmlEnumValue("vApp Author") AUTHOR("vApp Author"),
      @XmlEnumValue("Catalog Author") CATALOG_AUTHOR("Catalog Author"),
      @XmlEnumValue("Console Access Only") CONSOLE("Console Access Only"),
      @XmlEnumValue("Organization Administrator") ORG_ADMIN("Organization Administrator");
      
      public static final List<DefaultRoles> ALL = ImmutableList.of(
            USER, AUTHOR, CATALOG_AUTHOR, CONSOLE, ORG_ADMIN);

      protected final String stringValue;

      DefaultRoles(String stringValue) {
         this.stringValue = stringValue;
      }

      public String value() {
         return stringValue;
      }

      protected static final Map<String, DefaultRoles> DEFAULT_ROLES_BY_ID = Maps.uniqueIndex(
            ImmutableSet.copyOf(DefaultRoles.values()), new Function<DefaultRoles, String>() {
               @Override
               public String apply(DefaultRoles input) {
                  return input.stringValue;
               }
            });

      public static DefaultRoles fromValue(String value) {
         return DEFAULT_ROLES_BY_ID.get(checkNotNull(value, "stringValue"));
      }
   }
}
