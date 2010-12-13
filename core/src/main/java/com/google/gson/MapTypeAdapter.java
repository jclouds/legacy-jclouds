package com.google.gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.jclouds.json.internal.ParseObjectFromElement;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MapTypeAdapter implements JsonSerializer<Map>, JsonDeserializer<Map>, InstanceCreator<Map> {

   public JsonElement serialize(Map src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject map = new JsonObject();
      Type childGenericType = null;
      if (typeOfSrc instanceof ParameterizedType) {
         childGenericType = new TypeInfoMap(typeOfSrc).getValueType();
      }

      for (Map.Entry entry : (Set<Map.Entry>) src.entrySet()) {
         Object value = entry.getValue();

         JsonElement valueElement;
         if (value == null) {
            valueElement = JsonNull.createJsonNull();
         } else {
            Type childType = (childGenericType == null) ? value.getClass() : childGenericType;
            valueElement = context.serialize(value, childType);
         }
         map.add(String.valueOf(entry.getKey()), valueElement);
      }
      return map;
   }

   public Map deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      // Use ObjectConstructor to create instance instead of hard-coding a specific type.
      // This handles cases where users are using their own subclass of Map.
      Map<Object, Object> map = constructMapType(typeOfT, context);
      TypeInfoMap mapTypeInfo = new TypeInfoMap(typeOfT);
      for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
         Object key = context.deserialize(new JsonPrimitive(entry.getKey()), mapTypeInfo.getKeyType());
         // START JCLOUDS PATCH
         Object value = null;
         if (mapTypeInfo.getValueType() == Object.class) {
            value = ParseObjectFromElement.SINGLETON.apply(entry.getValue());
         }
         if (value == null) {
            value = context.deserialize(entry.getValue(), mapTypeInfo.getValueType());
         }
         // END JCLOUDS PATCH
         map.put(key, value);
      }
      return map;
   }

   private Map constructMapType(Type mapType, JsonDeserializationContext context) {
      JsonDeserializationContextDefault contextImpl = (JsonDeserializationContextDefault) context;
      ObjectConstructor objectConstructor = contextImpl.getObjectConstructor();
      return (Map) objectConstructor.construct(mapType);
   }

   public Map createInstance(Type type) {
      return new LinkedHashMap();
   }

   @Override
   public String toString() {
      return MapTypeAdapter.class.getSimpleName();
   }
}