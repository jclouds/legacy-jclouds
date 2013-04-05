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

package org.jclouds.management.internal;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;
import org.jclouds.management.annotations.ManagedAttribute;
import org.jclouds.management.annotations.ManagedType;
import org.jclouds.management.functions.ToOpenType;

import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularType;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.jclouds.reflect.Reflection2.fieldsAnnotatedWith;
import static org.jclouds.reflect.Reflection2.methodsAnnotatedWith;
import static org.jclouds.reflect.Reflection2.shortNameOf;
import static org.jclouds.reflect.Reflection2.typeToken;

/**
 * A Class that describes a {@link org.jclouds.management.annotations.ManagedType}.
 *
 * @param <T>
 */
public final class ManagedTypeModel<T> {

   private final Class<T> type;
   private final Map<String, String> descriptions;
   private final Map<String, TypeToken> typeTokens;
   private final Map<String, ? extends OpenType> openTypes;
   private final CompositeType compositeType;
   private final TabularType tabularType;

   private static final LoadingCache<Class, ManagedTypeModel> MANAGED_RESOURCE_CACHE = CacheBuilder.newBuilder().build(new CacheLoader<Class, ManagedTypeModel>() {
      @Override
      public ManagedTypeModel load(Class key) throws Exception {
         return create(key);
      }
   });


   /**
    * Constructor
    *
    * @param type
    * @param openTypes
    */
   private ManagedTypeModel(Class<T> type, Map<String, String> descriptions, Map<String, TypeToken> typeTokens, Map<String, OpenType> openTypes) {
      this.type = type;
      this.descriptions = descriptions;
      this.typeTokens = typeTokens;
      this.openTypes = openTypes;
      this.compositeType = createCompositeType(type, descriptions.keySet(), descriptions, typeTokens);
      this.tabularType = createTabularType(type, descriptions.keySet(), compositeType);
   }

   public static synchronized ManagedTypeModel of(Class type) {
      try {
         return MANAGED_RESOURCE_CACHE.get(type);
      } catch (ExecutionException ex) {
         throw new RuntimeException(ex);
      }
   }

   /**
    * Creates the ManagedType info for the specified class.
    *
    * @param in
    * @param <T>
    * @return
    */
   private static <T> ManagedTypeModel<T> create(Class<T> in) throws OpenDataException {
      Map<String, String> descriptions = Maps.newLinkedHashMap();
      Map<String, TypeToken> typeTokens = Maps.newLinkedHashMap();
      Map<String, OpenType> openTypes = Maps.newLinkedHashMap();

      boolean foundAnnotation = false;
      TypeToken<T> typeToken = TypeToken.of(in);
      for (TypeToken token : typeToken.getTypes()) {
         Class type = token.getRawType();
         if (type.isAnnotationPresent(ManagedType.class)) {
            foundAnnotation = true;
            for (Invokable invokable : methodsAnnotatedWith(type, ManagedAttribute.class)) {
               String name = shortNameOf(invokable);
               String description = managedAttributeDescriptionOf(invokable);
               TypeToken t = invokable.getReturnType();
               descriptions.put(name, description);
               typeTokens.put(name, t);
               //Avoid infinite loop when.
               if (type.equals(t)) {
                  openTypes.put(name, SimpleType.STRING);
               } else {
                  openTypes.put(name, ToOpenType.FUNCTION.apply(t.getType()));
               }
            }

            for (Field field : fieldsAnnotatedWith(type, ManagedAttribute.class)) {
               String name = field.getName();
               Type t = field.getGenericType();
               descriptions.put(name, managedAttributeDescriptionOf(field));
               typeTokens.put(name, typeToken(t));
               //Avoid infinite loop when.
               if (type.equals(t)) {
                  openTypes.put(name, SimpleType.STRING);
               } else {
                  openTypes.put(name, ToOpenType.FUNCTION.apply(t));
               }
            }

         }
      }
      if (foundAnnotation) {
         return new ManagedTypeModel<T>(in, descriptions, typeTokens, openTypes);
      } else {
         throw new IllegalArgumentException("Type " + in + " is not annotated as ManagedType.");
      }
   }

   /**
    * Creates a {@link CompositeType}.
    * @param type
    * @param names
    * @param descriptions
    * @param types
    * @return
    */
   private static CompositeType createCompositeType(Class type, Set<String> names,  Map<String, String> descriptions, Map<String, TypeToken> types) {
      try {
         String description = "Composite type for " + type.getCanonicalName();
         int index = 0;
         int size = names.size();
         String[] attributeNames = new String[size];
         String[] attributeDescriptions = new String[size];
         OpenType[] attributeTypes = new OpenType[size];


         for (String name : names) {
            attributeNames[index] = name;
            attributeDescriptions[index] = !Strings.isNullOrEmpty(descriptions.get(name)) ? descriptions.get(name) : name;
            try {
               attributeTypes[index] = ToOpenType.FUNCTION.apply(types.get(name).getType());
            } catch (Exception ex) {
               //We want to catch cycles here.
               attributeTypes[index] = SimpleType.STRING;
            }
            index++;
         }

         return new CompositeType(type.getCanonicalName(), description, attributeNames,
                 attributeDescriptions, attributeTypes);
      } catch (Exception e) {
         throw new IllegalStateException("Unable to build " + type.getName() + " composite type.", e);
      }
   }

   /**
    * Creates a {@link TabularType}.
    * @param type
    * @param names
    * @param compositeType
    * @return
    */
   private static TabularType createTabularType(Class type, Set<String> names,  CompositeType compositeType) {
      try {
         String description = "Tabular type for " + type.getCanonicalName();
         return new TabularType(type.getCanonicalName(), description,
                 compositeType, (String[]) names.toArray(new String[names.size()]));
      } catch (Exception e) {
         throw new IllegalStateException("Unable to build " + type.getName() + " tabular type.", e);
      }
   }

   public Class<T> getType() {
      return type;
   }

   public Set<String> getNames() {
      return typeTokens.keySet();
   }

   public String getDescription(String attribute) {
      return descriptions.get(attribute);
   }

   public Type getType(String attribute) {
      return typeTokens.get(attribute).getType();
   }

   public TypeToken getTypeToken(String attribute) {
      return typeTokens.get(attribute);
   }

   public OpenType getOpenType(String attribute) {
      return openTypes.get(attribute);
   }


   /**
    * Returns the {@link CompositeType} of this model.
    *
    * @return
    */
   public CompositeType getCompositeType() {
      return this.compositeType;
   }

   /**
    * Returns the {@link TabularType} of this model.
    *
    * @return
    */
   public TabularType getTabularType() {
      return this.tabularType;
   }


   /**
    * Returns the description on the {@link ManagedAttribute}.
    *
    * @param field
    * @return
    */
   @VisibleForTesting
   private static String managedAttributeDescriptionOf(Field field) {
      ManagedAttribute attribute = field.getAnnotation(ManagedAttribute.class);
      if (attribute != null) {
         return attribute.description();
      }
      return "";
   }

   /**
    * Returns the description on the {@link ManagedAttribute}.
    *
    * @param invokable
    * @return
    */
   @VisibleForTesting
   private static String managedAttributeDescriptionOf(Invokable invokable) {
      ManagedAttribute attribute = invokable.getAnnotation(ManagedAttribute.class);
      if (attribute != null) {
         return attribute.description();
      }
      return "";
   }


   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ManagedTypeModel that = (ManagedTypeModel) o;

      if (type != null ? !type.equals(that.type) : that.type != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      return type != null ? type.hashCode() : 0;
   }
}
