/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.compute.domain.internal;

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
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
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

   protected final Supplier<Set<? extends Image>> images;
   protected final Supplier<Set<? extends Size>> sizes;
   protected final Supplier<Set<? extends Location>> locations;
   protected final Supplier<Location> defaultLocation;
   protected final Provider<TemplateOptions> optionsProvider;
   protected final Provider<TemplateBuilder> defaultTemplateProvider;

   @VisibleForTesting
   protected Architecture arch;
   @VisibleForTesting
   protected Location location;
   @VisibleForTesting
   protected String imageId;
   @VisibleForTesting
   protected String sizeId;
   @VisibleForTesting
   protected String imageVersion;
   @VisibleForTesting
   protected OsFamily osFamily;
   @VisibleForTesting
   protected String osVersion;
   @VisibleForTesting
   protected Boolean os64Bit;
   @VisibleForTesting
   protected String osName;
   @VisibleForTesting
   protected String osDescription;
   @VisibleForTesting
   protected String osArch;
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
   protected TemplateBuilderImpl(Supplier<Set<? extends Location>> locations, Supplier<Set<? extends Image>> images,
         Supplier<Set<? extends Size>> sizes, Supplier<Location> defaultLocation2,
         Provider<TemplateOptions> optionsProvider, @Named("DEFAULT") Provider<TemplateBuilder> defaultTemplateProvider) {
      this.locations = locations;
      this.images = images;
      this.sizes = sizes;
      this.defaultLocation = defaultLocation2;
      this.optionsProvider = optionsProvider;
      this.defaultTemplateProvider = defaultTemplateProvider;
   }

   /**
    * If the current location id is null, then we don't care where to launch a
    * node.
    * 
    * If the input location is null, then the data isn't location sensitive
    * 
    * If the input location is a parent of the specified location, then we are
    * ok.
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

   private final Predicate<OperatingSystem> osFamilyPredicate = new Predicate<OperatingSystem>() {

      @Override
      public boolean apply(OperatingSystem input) {
         boolean returnVal = true;
         if (osFamily != null)
            returnVal = osFamily.equals(input.getFamily());
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
   private final Predicate<OperatingSystem> osNamePredicate = new Predicate<OperatingSystem>() {
      @Override
      public boolean apply(OperatingSystem input) {
         boolean returnVal = true;
         if (osName != null) {
            if (input.getName() == null)
               returnVal = false;
            else
               returnVal = input.getName().contains(osName) || input.getName().matches(osName);
         }
         return returnVal;
      }
   };

   private final Predicate<OperatingSystem> osDescriptionPredicate = new Predicate<OperatingSystem>() {
      @Override
      public boolean apply(OperatingSystem input) {
         boolean returnVal = true;
         if (osDescription != null) {
            if (input.getDescription() == null)
               returnVal = false;
            else
               returnVal = input.getDescription().contains(osDescription)
                     || input.getDescription().matches(osDescription);
         }
         return returnVal;
      }
   };

   private final Predicate<OperatingSystem> osVersionPredicate = new Predicate<OperatingSystem>() {
      @Override
      public boolean apply(OperatingSystem input) {
         boolean returnVal = true;
         if (osVersion != null) {
            if (input.getVersion() == null)
               returnVal = false;
            else
               returnVal = input.getVersion().contains(osVersion) || input.getVersion().matches(osVersion);
         }
         return returnVal;
      }
   };

   private final Predicate<OperatingSystem> os64BitPredicate = new Predicate<OperatingSystem>() {
      @Override
      public boolean apply(OperatingSystem input) {
         boolean returnVal = true;
         if (os64Bit != null) {
            if (os64Bit)
               return input.is64Bit();
            else
               return !input.is64Bit();
         }
         return returnVal;
      }
   };

   private final Predicate<OperatingSystem> osArchPredicate = new Predicate<OperatingSystem>() {
      @Override
      public boolean apply(OperatingSystem input) {
         boolean returnVal = true;
         if (osArch != null) {
            if (input.getArch() == null)
               returnVal = false;
            else
               returnVal = input.getArch().contains(osArch) || input.getArch().matches(osArch);
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
               Ordering.<String> natural().nullsLast()).compare(left.getOperatingSystem().getName(),
               right.getOperatingSystem().getName(),// 
               Ordering.<String> natural().nullsLast()).compare(left.getOperatingSystem().getVersion(),
               right.getOperatingSystem().getVersion(),// 
               Ordering.<String> natural().nullsLast()).compare(left.getOperatingSystem().getDescription(),
               right.getOperatingSystem().getDescription(),// 
               Ordering.<String> natural().nullsLast()).compare(left.getOperatingSystem().getArch(),
               right.getOperatingSystem().getArch()).result();
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
      if (image.getOperatingSystem().getFamily() != null)
         this.osFamily = image.getOperatingSystem().getFamily();
      if (image.getName() != null)
         this.imageName = image.getName();
      if (image.getDescription() != null)
         this.imageDescription = image.getDescription();
      if (image.getOperatingSystem().getName() != null)
         this.osName = image.getOperatingSystem().getName();
      if (image.getOperatingSystem().getDescription() != null)
         this.osDescription = image.getOperatingSystem().getDescription();
      if (image.getVersion() != null)
         this.imageVersion = image.getVersion();
      if (image.getOperatingSystem().getVersion() != null)
         this.osVersion = image.getOperatingSystem().getVersion();
      this.os64Bit = image.getOperatingSystem().is64Bit();
      if (image.getOperatingSystem().getArch() != null)
         this.osArch = image.getOperatingSystem().getArch();
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
      this.osFamily = os;
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
         location = defaultLocation.get();
      if (options == null)
         options = optionsProvider.get();
      logger.debug(">> searching params(%s)", this);
      Set<? extends Image> images = getImages();
      Iterable<? extends Image> supportedImages = filter(images, buildImagePredicate());
      Size size = resolveSize(sizeSorter(), supportedImages);
      Image image = resolveImage(size, supportedImages);
      logger.debug("<<   matched image(%s)", image);

      // ensure we have an architecture matching
      this.arch = image.getArchitecture();
      return new TemplateImpl(image, size, location, options);
   }

   protected Size resolveSize(Ordering<Size> sizeOrdering, final Iterable<? extends Image> images) {
      Set<? extends Size> sizesl = sizes.get();
      Size size;
      try {
         Iterable<? extends Size> sizesThatAreCompatibleWithOurImages = filter(sizesl, new Predicate<Size>() {
            @Override
            public boolean apply(final Size size) {
               return Iterables.any(images, new Predicate<Image>() {

                  @Override
                  public boolean apply(Image input) {
                     return size.supportsImage(input);
                  }

               });

            }
         });
         size = sizeOrdering.max(filter(sizesThatAreCompatibleWithOurImages, sizePredicate));
      } catch (NoSuchElementException exception) {
         throw new NoSuchElementException("sizes don't support any images: " + toString() + "\n" + sizesl + "\n"
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
    * @param supportedImages
    * @throws NoSuchElementException
    *            if there's no image that matches the predicate
    */
   protected Image resolveImage(final Size size, Iterable<? extends Image> supportedImages) {
      Predicate<Image> imagePredicate = new Predicate<Image>() {

         @Override
         public boolean apply(Image arg0) {
            return size.supportsImage(arg0);
         }

      };
      try {
         Iterable<? extends Image> matchingImages = filter(supportedImages, imagePredicate);
         if (logger.isTraceEnabled())
            logger.trace("<<   matched images(%s)", matchingImages);
         List<? extends Image> maxImages = multiMax(DEFAULT_IMAGE_ORDERING, matchingImages);
         if (logger.isTraceEnabled())
            logger.trace("<<   best images(%s)", maxImages);
         return maxImages.get(maxImages.size() - 1);
      } catch (NoSuchElementException exception) {
         throw new NoSuchElementException("image didn't match: " + toString() + "\n" + supportedImages);
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
         predicates.add(new Predicate<Image>() {

            @Override
            public boolean apply(Image input) {
               return Predicates.and(
                     ImmutableSet.of(osFamilyPredicate, osNamePredicate, osDescriptionPredicate, osVersionPredicate,
                           os64BitPredicate, osArchPredicate)).apply(input.getOperatingSystem());
            }

         });
         predicates.add(imageArchPredicate);
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
      this.osFamily = null;
      this.osName = null;
      this.osDescription = null;
      this.osVersion = null;
      this.os64Bit = null;
      this.osArch = null;
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
   public TemplateBuilder osVersionMatches(String osVersionRegex) {
      this.osVersion = osVersionRegex;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder osArchMatches(String osArchitectureRegex) {
      this.osArch = osArchitectureRegex;
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
   public TemplateBuilder osNameMatches(String osNameRegex) {
      this.osName = osNameRegex;
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
      return osFamily == null && arch == null && location == null && imageId == null && sizeId == null
            && osName == null && osDescription == null && imageVersion == null && osVersion == null && osArch == null
            && os64Bit == null && imageName == null && imageDescription == null && minCores == 0 && minRam == 0
            && !biggest && !fastest;
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
            + ", location=" + location + ", minCores=" + minCores + ", minRam=" + minRam + ", osFamily=" + osFamily
            + ", osName=" + osName + ", osDescription=" + osDescription + ", osVersion=" + osVersion + ", osArch=" + osArch + ", os64Bit="
            + os64Bit + ", sizeId=" + sizeId + "]";
   }

   @Override
   public TemplateBuilder os64bit(boolean is64Bit) {
      this.os64Bit = is64Bit;
      return this;
   }

}
