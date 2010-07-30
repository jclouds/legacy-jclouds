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
package org.jclouds.compute.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static org.jclouds.util.Utils.multiMax;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.internal.TemplateImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Doubles;

/**
 * 
 * @author Adrian Cole
 */
@SuppressWarnings("unchecked")
public class TemplateBuilderImpl implements TemplateBuilder {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final Provider<Set<? extends Image>> images;
   protected final Provider<Set<? extends Size>> sizes;
   protected final Provider<Set<? extends Location>> locations;
   protected final Provider<TemplateOptions> optionsProvider;
   protected final Provider<TemplateBuilder> defaultTemplateProvider;
   protected final Location defaultLocation;

   @VisibleForTesting
   protected OsFamily os;
   @VisibleForTesting
   protected Architecture arch;
   @VisibleForTesting
   protected Location location;
   @VisibleForTesting
   protected String imageId;
   @VisibleForTesting
   protected String sizeId;
   @VisibleForTesting
   protected String osDescription;
   @VisibleForTesting
   protected String imageVersion;
   @VisibleForTesting
   protected String imageName;
   @VisibleForTesting
   protected String imageDescription;
   @VisibleForTesting
   protected double minCores;
   @VisibleForTesting
   protected int minRam;
   @VisibleForTesting
   protected boolean biggest;
   @VisibleForTesting
   protected boolean fastest;
   @VisibleForTesting
   protected TemplateOptions options;

   @Inject
   protected TemplateBuilderImpl(Provider<Set<? extends Location>> locations, Provider<Set<? extends Image>> images,
            Provider<Set<? extends Size>> sizes, Location defaultLocation, Provider<TemplateOptions> optionsProvider,
            @Named("DEFAULT") Provider<TemplateBuilder> defaultTemplateProvider) {
      this.locations = locations;
      this.images = images;
      this.sizes = sizes;
      this.defaultLocation = defaultLocation;
      this.optionsProvider = optionsProvider;
      this.defaultTemplateProvider = defaultTemplateProvider;
   }

   /**
    * If the current location id is null, then we don't care where to launch a node.
    * 
    * If the input location is null, then the data isn't location sensitive
    * 
    * If the input location is a parent of the specified location, then we are ok.
    */
   private final Predicate<ComputeMetadata> locationPredicate = new Predicate<ComputeMetadata>() {
      @Override
      public boolean apply(ComputeMetadata input) {
         boolean returnVal = true;
         if (location != null && input.getLocation() != null)
            returnVal = location.equals(input.getLocation()) || location.getParent() != null
                     && location.getParent().equals(input.getLocation());
         return returnVal;
      }
   };

   private final Predicate<Image> idPredicate = new Predicate<Image>() {
      @Override
      public boolean apply(Image input) {
         boolean returnVal = true;
         if (imageId != null) {
            returnVal = imageId.equals(input.getId());
            // match our input params so that the later predicates pass.
            if (returnVal) {
               fromImage(input);
            }
         }
         return returnVal;
      }
   };

   private final Predicate<Image> osPredicate = new Predicate<Image>() {

      @Override
      public boolean apply(Image input) {
         boolean returnVal = true;
         if (os != null)
            returnVal = os.equals(input.getOsFamily());
         return returnVal;
      }

   };
   private final Predicate<Image> imageArchPredicate = new Predicate<Image>() {

      @Override
      public boolean apply(Image input) {
         boolean returnVal = true;
         if (arch != null)
            returnVal = arch.equals(input.getArchitecture());
         return returnVal;
      }

   };

   private final Predicate<Image> osDescriptionPredicate = new Predicate<Image>() {
      @Override
      public boolean apply(Image input) {
         boolean returnVal = true;
         if (osDescription != null) {
            if (input.getOsDescription() == null)
               returnVal = false;
            else
               returnVal = input.getOsDescription().contains(osDescription)
                        || input.getOsDescription().matches(osDescription);
         }
         return returnVal;
      }
   };
   private final Predicate<Image> imageVersionPredicate = new Predicate<Image>() {
      @Override
      public boolean apply(Image input) {
         boolean returnVal = true;
         if (imageVersion != null) {
            if (input.getVersion() == null)
               returnVal = false;
            else
               returnVal = input.getVersion().contains(imageVersion) || input.getVersion().matches(imageVersion);
         }
         return returnVal;
      }
   };
   private final Predicate<Image> imageNamePredicate = new Predicate<Image>() {
      @Override
      public boolean apply(Image input) {
         boolean returnVal = true;
         if (imageName != null) {
            if (input.getName() == null)
               returnVal = false;
            else
               returnVal = input.getName().contains(imageName) || input.getName().matches(imageName);
         }
         return returnVal;
      }
   };
   private final Predicate<Image> imageDescriptionPredicate = new Predicate<Image>() {
      @Override
      public boolean apply(Image input) {
         boolean returnVal = true;
         if (imageDescription != null) {
            if (input.getName() == null)
               returnVal = false;
            else
               returnVal = input.getDescription().equals(imageDescription)
                        || input.getDescription().contains(imageDescription)
                        || input.getDescription().matches(imageDescription);
         }
         return returnVal;
      }
   };
   private final Predicate<Size> sizeIdPredicate = new Predicate<Size>() {
      @Override
      public boolean apply(Size input) {
         boolean returnVal = true;
         if (sizeId != null) {
            returnVal = sizeId.equals(input.getId());
            // match our input params so that the later predicates pass.
            if (returnVal) {
               fromSize(input);
            }
         }
         return returnVal;
      }
   };

   private final Predicate<Size> sizeCoresPredicate = new Predicate<Size>() {
      @Override
      public boolean apply(Size input) {
         return input.getCores() >= TemplateBuilderImpl.this.minCores;
      }
   };

   private final Predicate<Size> sizeRamPredicate = new Predicate<Size>() {
      @Override
      public boolean apply(Size input) {
         return input.getRam() >= TemplateBuilderImpl.this.minRam;
      }
   };
   private final Predicate<Size> sizePredicate = and(sizeIdPredicate, locationPredicate, sizeCoresPredicate,
            sizeRamPredicate);

   static final Ordering<Size> DEFAULT_SIZE_ORDERING = new Ordering<Size>() {
      public int compare(Size left, Size right) {
         return ComparisonChain.start().compare(left.getCores(), right.getCores()).compare(left.getRam(),
                  right.getRam()).compare(left.getDisk(), right.getDisk()).result();
      }
   };
   static final Ordering<Size> BY_CORES_ORDERING = new Ordering<Size>() {
      public int compare(Size left, Size right) {
         return Doubles.compare(left.getCores(), right.getCores());
      }
   };
   static final Ordering<Image> DEFAULT_IMAGE_ORDERING = new Ordering<Image>() {
      public int compare(Image left, Image right) {
         return ComparisonChain.start().compare(left.getName(), right.getName(),
                  Ordering.<String> natural().nullsLast()).compare(left.getVersion(), right.getVersion(),
                  Ordering.<String> natural().nullsLast()).compare(left.getOsDescription(), right.getOsDescription(),
                  Ordering.<String> natural().nullsLast()).compare(left.getArchitecture(), right.getArchitecture())
                  .result();
      }
   };

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder fromTemplate(Template template) {
      fromSize(template.getSize());
      fromImage(template.getImage());
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder fromSize(Size size) {
      if (size.getLocation() != null)
         this.location = size.getLocation();
      this.minCores = size.getCores();
      this.minRam = size.getRam();
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder fromImage(Image image) {
      if (image.getLocation() != null)
         this.location = image.getLocation();
      if (image.getOsFamily() != null)
         this.os = image.getOsFamily();
      if (image.getName() != null)
         this.imageName = image.getName();
      if (image.getDescription() != null)
         this.imageDescription = image.getDescription();
      if (image.getOsDescription() != null)
         this.osDescription = image.getOsDescription();
      if (image.getVersion() != null)
         this.imageVersion = image.getVersion();
      if (image.getArchitecture() != null)
         this.arch = image.getArchitecture();
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder smallest() {
      this.biggest = false;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder biggest() {
      this.biggest = true;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder fastest() {
      this.fastest = true;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder locationId(final String locationId) {
      this.location = Iterables.find(locations.get(), new Predicate<Location>() {

         @Override
         public boolean apply(Location input) {
            return input.getId().equals(locationId);
         }

      });
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder osFamily(OsFamily os) {
      this.os = os;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder architecture(Architecture architecture) {
      this.arch = architecture;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Template build() {
      if (nothingChangedExceptOptions()) {
         TemplateBuilder defaultTemplate = defaultTemplateProvider.get();
         if (options != null)
            defaultTemplate.options(options);
         return defaultTemplate.build();
      }
      if (location == null)
         location = defaultLocation;
      if (options == null)
         options = optionsProvider.get();
      logger.debug(">> searching params(%s)", this);
      Size size = resolveSize(sizeSorter(), getImages());
      Image image = resolveImage(size);
      logger.debug("<<   matched image(%s)", image);

      // ensure we have an architecture matching
      this.arch = image.getArchitecture();
      return new TemplateImpl(image, size, location, options);
   }

   protected Size resolveSize(Ordering<Size> sizeOrdering, final Iterable<? extends Image> images) {
      Size size;
      try {
         Iterable<? extends Size> sizesThatAreCompatibleWithOurImages = filter(sizes.get(), new Predicate<Size>() {
            @Override
            public boolean apply(final Size size) {
               boolean returnVal = false;
               if (size != null) {
                  returnVal = Iterables.any(images, new Predicate<Image>() {

                     @Override
                     public boolean apply(Image input) {
                        return size.supportsImage(input);
                     }

                  });
               }
               return returnVal;
            }
         });
         size = sizeOrdering.max(filter(sizesThatAreCompatibleWithOurImages, sizePredicate));
      } catch (NoSuchElementException exception) {
         throw new NoSuchElementException("sizes don't support any images: " + toString() + "\n" + sizes.get() + "\n"
                  + images);
      }
      logger.debug("<<   matched size(%s)", size);
      return size;
   }

   protected Ordering<Size> sizeSorter() {
      Ordering<Size> sizeOrdering = DEFAULT_SIZE_ORDERING;
      if (!biggest)
         sizeOrdering = sizeOrdering.reverse();
      if (fastest)
         sizeOrdering = Ordering.compound(ImmutableList.of(BY_CORES_ORDERING, sizeOrdering));
      return sizeOrdering;
   }

   /**
    * 
    * @param size
    * @throws NoSuchElementException
    *            if there's no image that matches the predicate
    */
   protected Image resolveImage(final Size size) {
      Predicate<Image> imagePredicate = and(buildImagePredicate(), new Predicate<Image>() {

         @Override
         public boolean apply(Image arg0) {
            return size.supportsImage(arg0);
         }

      });
      try {
         Iterable<? extends Image> matchingImages = filter(getImages(), imagePredicate);
         if (logger.isTraceEnabled())
            logger.trace("<<   matched images(%s)", matchingImages);
         List<? extends Image> maxImages = multiMax(DEFAULT_IMAGE_ORDERING, matchingImages);
         if (logger.isTraceEnabled())
            logger.trace("<<   best images(%s)", maxImages);
         return maxImages.get(maxImages.size() - 1);
      } catch (NoSuchElementException exception) {
         Set<? extends Image> images = getImages();
         throw new NoSuchElementException("image didn't match: " + toString() + "\n" + images);
      }
   }

   protected Set<? extends Image> getImages() {
      return images.get();
   }

   private Predicate<Image> buildImagePredicate() {
      List<Predicate<Image>> predicates = newArrayList();
      if (imageId != null) {
         predicates.add(idPredicate);
      } else {
         predicates.add(new Predicate<Image>() {

            @Override
            public boolean apply(Image input) {
               return locationPredicate.apply(input);
            }

         });
         predicates.add(osPredicate);
         predicates.add(imageArchPredicate);
         predicates.add(osDescriptionPredicate);
         predicates.add(imageVersionPredicate);
         predicates.add(imageNamePredicate);
         predicates.add(imageDescriptionPredicate);
      }

      Predicate<Image> imagePredicate = and(predicates);
      return imagePredicate;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder imageId(String imageId) {
      this.imageId = imageId;
      this.imageName = null;
      this.imageDescription = null;
      this.imageVersion = null;
      this.arch = null;
      this.os = null;
      this.osDescription = null;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder imageNameMatches(String nameRegex) {
      this.imageName = nameRegex;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder imageDescriptionMatches(String descriptionRegex) {
      this.imageDescription = descriptionRegex;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder imageVersionMatches(String imageVersionRegex) {
      this.imageVersion = imageVersionRegex;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder minCores(double minCores) {
      this.minCores = minCores;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder minRam(int megabytes) {
      this.minRam = megabytes;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder osDescriptionMatches(String osDescriptionRegex) {
      this.osDescription = osDescriptionRegex;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder sizeId(String sizeId) {
      this.sizeId = sizeId;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder options(TemplateOptions options) {
      this.options = optionsProvider.get();
      copyTemplateOptions(checkNotNull(options, "options"), this.options);
      return this;
   }

   protected void copyTemplateOptions(TemplateOptions from, TemplateOptions to) {
      if (!Arrays.equals(to.getInboundPorts(), from.getInboundPorts()))
         to.inboundPorts(from.getInboundPorts());
      if (from.getRunScript() != null)
         to.runScript(from.getRunScript());
      if (from.getPrivateKey() != null)
         to.installPrivateKey(from.getPrivateKey());
      if (from.getPublicKey() != null)
         to.authorizePublicKey(from.getPublicKey());
      if (from.getPort() != -1)
         to.blockOnPort(from.getPort(), from.getSeconds());
      if (from.isIncludeMetadata())
         to.withMetadata();
      if (!from.shouldBlockUntilRunning())
         to.blockUntilRunning(false);
   }

   @VisibleForTesting
   boolean nothingChangedExceptOptions() {
      return os == null && arch == null && location == null && imageId == null && sizeId == null
               && osDescription == null && imageVersion == null && imageName == null && imageDescription == null
               && minCores == 0 && minRam == 0 && !biggest && !fastest;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder any() {
      return defaultTemplateProvider.get();
   }

   @Override
   public String toString() {
      return "[arch=" + arch + ", biggest=" + biggest + ", fastest=" + fastest + ", imageName=" + imageName
               + ", imageDescription=" + imageDescription + ", imageId=" + imageId + ", imageVersion=" + imageVersion
               + ", location=" + location + ", minCores=" + minCores + ", minRam=" + minRam + ", os=" + os
               + ", osDescription=" + osDescription + ", sizeId=" + sizeId + "]";
   }

}
