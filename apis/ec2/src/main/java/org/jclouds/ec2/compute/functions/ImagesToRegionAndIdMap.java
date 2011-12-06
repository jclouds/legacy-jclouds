package org.jclouds.ec2.compute.functions;

import static com.google.common.collect.Maps.uniqueIndex;

import java.util.Map;

import org.jclouds.compute.domain.Image;
import org.jclouds.ec2.compute.domain.RegionAndName;

import com.google.common.base.Function;
import com.google.inject.Singleton;

@Singleton
public class ImagesToRegionAndIdMap implements Function<Iterable<? extends Image>, Map<RegionAndName, ? extends Image>> {

   public static Map<RegionAndName, ? extends Image> imagesToMap(Iterable<? extends Image> input) {
      return new ImagesToRegionAndIdMap().apply(input);
   }
   
   @Override
   public Map<RegionAndName, ? extends Image> apply(Iterable<? extends Image> input) {
      return uniqueIndex(input, new Function<Image, RegionAndName>() {
         
         @Override
         public RegionAndName apply(Image from) {
            return new RegionAndName(from.getLocation().getId(), from.getProviderId());
         }
         
      });
   }


}
