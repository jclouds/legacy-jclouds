/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.predicates.cloud;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import org.jclouds.abiquo.domain.cloud.Volume;

import com.abiquo.model.enumerator.VolumeState;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * Container for {@link Volume} filters.
 * 
 * @author Ignasi Barrera
 */
public class VolumePredicates
{
    public static Predicate<Volume> name(final String... names)
    {
        checkNotNull(names, "names must be defined");

        return new Predicate<Volume>()
        {
            @Override
            public boolean apply(final Volume volume)
            {
                return Arrays.asList(names).contains(volume.getName());
            }
        };
    }

    public static Predicate<Volume> greaterThan(final long sizeInMb)
    {
        checkNotNull(sizeInMb, "sizeInMb must be defined");

        return new Predicate<Volume>()
        {
            @Override
            public boolean apply(final Volume volume)
            {
                return volume.getSizeInMB() > sizeInMb;
            }
        };
    }

    public static Predicate<Volume> greaterThanOrEqual(final long sizeInMb)
    {
        checkNotNull(sizeInMb, "sizeInMb must be defined");

        return new Predicate<Volume>()
        {
            @Override
            public boolean apply(final Volume volume)
            {
                return volume.getSizeInMB() >= sizeInMb;
            }
        };
    }

    public static Predicate<Volume> lesserThan(final long sizeInMb)
    {
        return Predicates.not(greaterThanOrEqual(sizeInMb));
    }

    public static Predicate<Volume> lesserThanOrEquals(final long sizeInMb)
    {
        return Predicates.not(greaterThan(sizeInMb));
    }

    public static Predicate<Volume> state(final VolumeState... states)
    {
        checkNotNull(states, "states must be defined");

        return new Predicate<Volume>()
        {
            @Override
            public boolean apply(final Volume volume)
            {
                return Arrays.asList(states).contains(VolumeState.valueOf(volume.getState()));
            }
        };
    }
}
