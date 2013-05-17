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
package org.jclouds.cloudwatch.features;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;
import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.Fallbacks;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.cloudwatch.binders.AlarmNamesBinder;
import org.jclouds.cloudwatch.domain.Alarm;
import org.jclouds.cloudwatch.domain.AlarmHistoryItem;
import org.jclouds.cloudwatch.functions.ListAlarmsToPagedIterable;
import org.jclouds.cloudwatch.options.ListAlarmHistoryOptions;
import org.jclouds.cloudwatch.options.ListAlarmsForMetric;
import org.jclouds.cloudwatch.options.ListAlarmsOptions;
import org.jclouds.cloudwatch.options.SaveAlarmOptions;
import org.jclouds.cloudwatch.xml.ListAlarmHistoryResponseHandler;
import org.jclouds.cloudwatch.xml.ListAlarmsForMetricResponseHandler;
import org.jclouds.cloudwatch.xml.ListAlarmsResponseHandler;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * Provides access to Amazon CloudWatch via the Query API
 * <p/>
 *
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference" />
 * @author Jeremy Whitlock
 */
@RequestFilters(FormSigner.class)
@VirtualHost
@Beta
public interface AlarmAsyncApi {

   /**
    * @see AlarmApi#delete(Iterable)
    */
   @Named("DeleteAlarms")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "DeleteAlarms")
   ListenableFuture<Void> delete(@BinderParam(AlarmNamesBinder.class) Iterable<String> alarmNames);

   /**
    * @see AlarmApi#listHistory()
    */
   @Named("DescribeAlarmHistory")
   @POST
   @Path("/")
   @XMLResponseParser(ListAlarmHistoryResponseHandler.class)
   @FormParams(keys = "Action", values = "DescribeAlarmHistory")
   @Transform(ListAlarmsToPagedIterable.class)
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<AlarmHistoryItem>> listHistory();

   /**
    * @see AlarmApi#listHistory(org.jclouds.cloudwatch.options.ListAlarmHistoryOptions)
    */
   @Named("DescribeAlarmHistory")
   @POST
   @Path("/")
   @XMLResponseParser(ListAlarmHistoryResponseHandler.class)
   @FormParams(keys = "Action", values = "DescribeAlarmHistory")
   @Transform(ListAlarmsToPagedIterable.class)
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<AlarmHistoryItem>> listHistory(ListAlarmHistoryOptions options);

   /**
    * @see AlarmApi#listHistoryAt(String)
    */
   @Named("DescribeAlarmHistory")
   @POST
   @Path("/")
   @XMLResponseParser(ListAlarmHistoryResponseHandler.class)
   @FormParams(keys = "Action", values = "DescribeAlarmHistory")
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListenableFuture<? extends IterableWithMarker<AlarmHistoryItem>> listHistoryAt(@FormParam("NextToken")
                                                                                        String nextToken);

   /**
    * @see org.jclouds.cloudwatch.features.AlarmApi#list()
    */
   @Named("DescribeAlarms")
   @POST
   @Path("/")
   @XMLResponseParser(ListAlarmsResponseHandler.class)
   @FormParams(keys = "Action", values = "DescribeAlarms")
   @Transform(ListAlarmsToPagedIterable.class)
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<Alarm>> list();

   /**
    * @see AlarmApi#list(org.jclouds.cloudwatch.options.ListAlarmsOptions)
    */
   @Named("DescribeAlarms")
   @POST
   @Path("/")
   @XMLResponseParser(ListAlarmsResponseHandler.class)
   @FormParams(keys = "Action", values = "DescribeAlarms")
   @Transform(ListAlarmsToPagedIterable.class)
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<Alarm>> list(ListAlarmsOptions options);

   /**
    * @see AlarmApi#listAt(String)
    */
   @Named("DescribeAlarms")
   @POST
   @Path("/")
   @XMLResponseParser(ListAlarmsResponseHandler.class)
   @FormParams(keys = "Action", values = "DescribeAlarms")
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListenableFuture<? extends IterableWithMarker<Alarm>> listAt(@FormParam("NextToken") String nextToken);

   /**
    * @see AlarmApi#listForMetric(org.jclouds.cloudwatch.options.ListAlarmsForMetric)
    */
   @Named("DescribeAlarmsForMetric")
   @POST
   @Path("/")
   @XMLResponseParser(ListAlarmsForMetricResponseHandler.class)
   @FormParams(keys = "Action", values = "DescribeAlarmsForMetric")
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListenableFuture<? extends FluentIterable<Alarm>> listForMetric(ListAlarmsForMetric options);

   /**
    * @see AlarmApi#disable(Iterable)
    */
   @Named("DisableAlarmActions")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "DisableAlarmActions")
   ListenableFuture<Void> disable(@BinderParam(AlarmNamesBinder.class) Iterable<String> alarmNames);

   /**
    * @see AlarmApi#enable(Iterable)
    */
   @Named("EnableAlarmActions")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "EnableAlarmActions")
   ListenableFuture<Void> enable(@BinderParam(AlarmNamesBinder.class) Iterable<String> alarmNames);

   /**
    * @see AlarmApi#save(org.jclouds.cloudwatch.options.SaveAlarmOptions)
    */
   @Named("PutMetricAlarm")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "PutMetricAlarm")
   ListenableFuture<Void> save(SaveAlarmOptions options);

   /**
    * @see AlarmApi#setState(String, String, String, org.jclouds.cloudwatch.domain.Alarm.State)
    */
   @Named("SetAlarmState")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "SetAlarmState")
   ListenableFuture<Void> setState(@FormParam("AlarmName") String alarmName,
                                   @FormParam("StateReason") String stateReason,
                                   @FormParam("StateReasonData") @Nullable String stateReasonData,
                                   @FormParam("StateValue") Alarm.State state);

}
