/**
 * Copyright 2007-2016, Kaazing Corporation. All rights reserved.
 *
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
 */
package org.kaazing.nuklei.ws.internal.conductor;

import org.kaazing.nuklei.Nukleus;
import org.kaazing.nuklei.Reaktive;
import org.kaazing.nuklei.ws.internal.Context;
import org.kaazing.nuklei.ws.internal.reader.Reader;
import org.kaazing.nuklei.ws.internal.types.control.BindFW;
import org.kaazing.nuklei.ws.internal.types.control.BoundFW;
import org.kaazing.nuklei.ws.internal.types.control.CaptureFW;
import org.kaazing.nuklei.ws.internal.types.control.CapturedFW;
import org.kaazing.nuklei.ws.internal.types.control.ErrorFW;
import org.kaazing.nuklei.ws.internal.types.control.PrepareFW;
import org.kaazing.nuklei.ws.internal.types.control.PreparedFW;
import org.kaazing.nuklei.ws.internal.types.control.RouteFW;
import org.kaazing.nuklei.ws.internal.types.control.RoutedFW;
import org.kaazing.nuklei.ws.internal.types.control.UnbindFW;
import org.kaazing.nuklei.ws.internal.types.control.UnboundFW;
import org.kaazing.nuklei.ws.internal.types.control.UncaptureFW;
import org.kaazing.nuklei.ws.internal.types.control.UncapturedFW;
import org.kaazing.nuklei.ws.internal.types.control.UnprepareFW;
import org.kaazing.nuklei.ws.internal.types.control.UnpreparedFW;
import org.kaazing.nuklei.ws.internal.types.control.UnrouteFW;
import org.kaazing.nuklei.ws.internal.types.control.UnroutedFW;

import uk.co.real_logic.agrona.DirectBuffer;
import uk.co.real_logic.agrona.concurrent.AtomicBuffer;
import uk.co.real_logic.agrona.concurrent.UnsafeBuffer;
import uk.co.real_logic.agrona.concurrent.broadcast.BroadcastTransmitter;
import uk.co.real_logic.agrona.concurrent.ringbuffer.RingBuffer;

@Reaktive
public final class Conductor implements Nukleus
{
    private static final int SEND_BUFFER_CAPACITY = 1024; // TODO: Configuration and Context

    private final CaptureFW captureRO = new CaptureFW();
    private final UncaptureFW uncaptureRO = new UncaptureFW();
    private final RouteFW routeRO = new RouteFW();
    private final UnrouteFW unrouteRO = new UnrouteFW();
    private final BindFW bindRO = new BindFW();
    private final UnbindFW unbindRO = new UnbindFW();
    private final PrepareFW prepareRO = new PrepareFW();
    private final UnprepareFW unprepareRO = new UnprepareFW();

    private final ErrorFW.Builder errorRW = new ErrorFW.Builder();
    private final CapturedFW.Builder capturedRW = new CapturedFW.Builder();
    private final UncapturedFW.Builder uncapturedRW = new UncapturedFW.Builder();
    private final RoutedFW.Builder routedRW = new RoutedFW.Builder();
    private final UnroutedFW.Builder unroutedRW = new UnroutedFW.Builder();
    private final BoundFW.Builder boundRW = new BoundFW.Builder();
    private final UnboundFW.Builder unboundRW = new UnboundFW.Builder();
    private final PreparedFW.Builder preparedRW = new PreparedFW.Builder();
    private final UnpreparedFW.Builder unpreparedRW = new UnpreparedFW.Builder();

    private final RingBuffer conductorCommands;
    private final BroadcastTransmitter conductorResponses;
    private final AtomicBuffer sendBuffer;

    private Reader reader;

    public Conductor(Context context)
    {
        this.conductorCommands = context.conductorCommands();
        this.conductorResponses = context.conductorResponses();
        this.sendBuffer = new UnsafeBuffer(new byte[SEND_BUFFER_CAPACITY]);
    }

    public void setReader(Reader reader)
    {
        this.reader = reader;
    }

    @Override
    public int process()
    {
        return conductorCommands.read(this::handleCommand);
    }

    @Override
    public String name()
    {
        return "conductor";
    }

    public void onErrorResponse(long correlationId)
    {
        ErrorFW errorRO = errorRW.wrap(sendBuffer, 0, sendBuffer.capacity())
                                 .correlationId(correlationId)
                                 .build();

        conductorResponses.transmit(errorRO.typeId(), errorRO.buffer(), errorRO.offset(), errorRO.length());
    }

    public void onCapturedResponse(long correlationId)
    {
        CapturedFW capturedRO = capturedRW.wrap(sendBuffer, 0, sendBuffer.capacity())
                                          .correlationId(correlationId)
                                          .build();

        conductorResponses.transmit(capturedRO.typeId(), capturedRO.buffer(), capturedRO.offset(), capturedRO.length());
    }

    public void onUncapturedResponse(long correlationId)
    {
        UncapturedFW uncapturedRO = uncapturedRW.wrap(sendBuffer, 0, sendBuffer.capacity())
                                                .correlationId(correlationId)
                                                .build();

        conductorResponses.transmit(
                uncapturedRO.typeId(), uncapturedRO.buffer(), uncapturedRO.offset(), uncapturedRO.length());
    }

    public void onRoutedResponse(long correlationId)
    {
        RoutedFW routedRO = routedRW.wrap(sendBuffer, 0, sendBuffer.capacity())
                                    .correlationId(correlationId)
                                    .build();

        conductorResponses.transmit(routedRO.typeId(), routedRO.buffer(), routedRO.offset(), routedRO.length());
    }

    public void onUnroutedResponse(long correlationId)
    {
        UnroutedFW unroutedRO = unroutedRW.wrap(sendBuffer, 0, sendBuffer.capacity())
                                          .correlationId(correlationId)
                                          .build();

        conductorResponses.transmit(unroutedRO.typeId(), unroutedRO.buffer(), unroutedRO.offset(), unroutedRO.length());
    }

    public void onBoundResponse(
        long correlationId,
        long referenceId)
    {
        BoundFW boundRO = boundRW.wrap(sendBuffer, 0, sendBuffer.capacity())
                                 .correlationId(correlationId)
                                 .referenceId(referenceId)
                                 .build();

        conductorResponses.transmit(boundRO.typeId(), boundRO.buffer(), boundRO.offset(), boundRO.length());
    }

    public void onUnboundResponse(
        long correlationId,
        String destination,
        long destinationRef,
        String source,
        String protocol)
    {
        UnboundFW unboundRO = unboundRW.wrap(sendBuffer, 0, sendBuffer.capacity())
                                       .correlationId(correlationId)
                                       .destination(destination)
                                       .destinationRef(destinationRef)
                                       .source(source)
                                       .protocol(protocol)
                                       .build();

        conductorResponses.transmit(unboundRO.typeId(), unboundRO.buffer(), unboundRO.offset(), unboundRO.length());
    }

    public void onPreparedResponse(
        long correlationId,
        long referenceId)
    {
        PreparedFW preparedRO = preparedRW.wrap(sendBuffer, 0, sendBuffer.capacity())
                                          .correlationId(correlationId)
                                          .referenceId(referenceId)
                                          .build();

        conductorResponses.transmit(preparedRO.typeId(), preparedRO.buffer(), preparedRO.offset(), preparedRO.length());
    }

    public void onUnpreparedResponse(
        long correlationId,
        String destination,
        long destinationRef,
        String source,
        String protocol)
    {
        UnpreparedFW unpreparedRO = unpreparedRW.wrap(sendBuffer, 0, sendBuffer.capacity())
                                                .correlationId(correlationId)
                                                .source(source)
                                                .destination(destination)
                                                .destinationRef(destinationRef)
                                                .protocol(protocol)
                                                .build();

        conductorResponses.transmit(
            unpreparedRO.typeId(), unpreparedRO.buffer(), unpreparedRO.offset(), unpreparedRO.length());
    }

    private void handleCommand(int msgTypeId, DirectBuffer buffer, int index, int length)
    {
        switch (msgTypeId)
        {
        case CaptureFW.TYPE_ID:
            handleCaptureCommand(buffer, index, length);
            break;
        case UncaptureFW.TYPE_ID:
            handleUncaptureCommand(buffer, index, length);
            break;
        case RouteFW.TYPE_ID:
            handleRouteCommand(buffer, index, length);
            break;
        case UnrouteFW.TYPE_ID:
            handleUnrouteCommand(buffer, index, length);
            break;
        case BindFW.TYPE_ID:
            handleBindCommand(buffer, index, length);
            break;
        case UnbindFW.TYPE_ID:
            handleUnbindCommand(buffer, index, length);
            break;
        case PrepareFW.TYPE_ID:
            handlePrepareCommand(buffer, index, length);
            break;
        case UnprepareFW.TYPE_ID:
            handleUnprepareCommand(buffer, index, length);
            break;
        default:
            // ignore unrecognized commands (forwards compatible)
            break;
        }
    }

    private void handleCaptureCommand(DirectBuffer buffer, int index, int length)
    {
        captureRO.wrap(buffer, index, index + length);

        long correlationId = captureRO.correlationId();
        String source = captureRO.source().asString();

        reader.doCapture(correlationId, source);
    }

    private void handleUncaptureCommand(DirectBuffer buffer, int index, int length)
    {
        uncaptureRO.wrap(buffer, index, index + length);

        long correlationId = uncaptureRO.correlationId();
        String source = uncaptureRO.source().asString();

        reader.doUncapture(correlationId, source);
    }

    private void handleRouteCommand(DirectBuffer buffer, int index, int length)
    {
        routeRO.wrap(buffer, index, index + length);

        long correlationId = routeRO.correlationId();
        String destination = routeRO.destination().asString();

        reader.doRoute(correlationId, destination);
    }

    private void handleUnrouteCommand(DirectBuffer buffer, int index, int length)
    {
        unrouteRO.wrap(buffer, index, index + length);

        long correlationId = unrouteRO.correlationId();
        String destination = unrouteRO.destination().asString();

        reader.doUnroute(correlationId, destination);
    }

    private void handleBindCommand(DirectBuffer buffer, int index, int length)
    {
        bindRO.wrap(buffer, index, index + length);

        long correlationId = bindRO.correlationId();
        String destination = bindRO.destination().asString();
        long destinationRef = bindRO.destinationRef();
        String source = bindRO.source().asString();
        String protocol = bindRO.protocol().asString();

        reader.doBind(correlationId, destination, destinationRef, source, protocol);
    }

    private void handleUnbindCommand(DirectBuffer buffer, int index, int length)
    {
        unbindRO.wrap(buffer, index, index + length);

        long correlationId = unbindRO.correlationId();
        long referenceId = unbindRO.referenceId();

        reader.doUnbind(correlationId, referenceId);
    }

    private void handlePrepareCommand(DirectBuffer buffer, int index, int length)
    {
        prepareRO.wrap(buffer, index, index + length);

        long correlationId = prepareRO.correlationId();
        String source = prepareRO.source().asString();
        String destination = prepareRO.destination().asString();
        long destinationRef = prepareRO.destinationRef();
        String protocol = prepareRO.protocol().asString();

        reader.doPrepare(correlationId, destination, destinationRef, source, protocol);
    }

    private void handleUnprepareCommand(DirectBuffer buffer, int index, int length)
    {
        unprepareRO.wrap(buffer, index, index + length);

        long correlationId = unprepareRO.correlationId();
        long referenceId = unprepareRO.referenceId();

        reader.doUnprepare(correlationId, referenceId);
    }
}
