package org.jclouds.virtualbox.functions.admin;

import static com.google.common.base.Preconditions.checkState;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.byon.Node;
import org.jclouds.byon.domain.YamlNode;
import org.jclouds.compute.domain.Image;
import org.jclouds.virtualbox.domain.YamlImage;
import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 *
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
 *
 * 
 * @author Andrea Turli
 */
@Singleton
public class ImageFromYamlStream implements Function<InputStream, Cache<String, Image>> {

   /**
    * Type-safe config class for YAML
    * 
    */
   public static class Config {
      public List<YamlImage> images;
   }
   
   private Object construct(String data) {
       Yaml yaml = new Yaml();
       return yaml.load(data);
   }
   
   @Override
   public Cache<String, Image> apply(InputStream source) {

      Constructor constructor = new Constructor(Config.class);

      TypeDescription imageDesc = new TypeDescription(YamlImage.class);
      imageDesc.putListPropertyType("images", String.class);
      constructor.addTypeDescription(imageDesc);

      Yaml yaml = new Yaml(new Loader(constructor));
      Config config = (Config) yaml.load(source);
      checkState(config != null, "missing config: class");
      checkState(config.images != null, "missing images: collection");

      Map<String, Image> backingMap = Maps.uniqueIndex(Iterables.transform(config.images, YamlImage.toImage),
              new Function<Image, String>() {
                 public String apply(Image image) {
                    return image.getId();
                 }
              });
        Cache<String, Image> cache = CacheBuilder.newBuilder().build(CacheLoader.from(Functions.forMap(backingMap)));
        for (String node : backingMap.keySet())
           cache.getUnchecked(node);
        return cache;
   }
   
}