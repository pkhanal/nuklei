/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
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
package org.kaazing.nuklei.http.internal.conductor;

import java.util.Map;

import org.kaazing.nuklei.http.internal.Context;

import uk.co.real_logic.agrona.concurrent.ManyToOneConcurrentArrayQueue;

public final class ConductorProxy
{
    private final ManyToOneConcurrentArrayQueue<ConductorResponse> responseQueue;

    public ConductorProxy(Context context)
    {
        this.responseQueue = context.conductorResponseQueue();
    }

    public void onErrorResponse(long correlationId)
    {
        ErrorResponse response = new ErrorResponse(correlationId);
        if (!responseQueue.offer(response))
        {
            throw new IllegalStateException("unable to offer response");
        }
    }

    public void onCapturedResponse(long correlationId)
    {
        CapturedResponse response = new CapturedResponse(correlationId);
        if (!responseQueue.offer(response))
        {
            throw new IllegalStateException("unable to offer response");
        }
    }

    public void onUncapturedResponse(long correlationId)
    {
        UncapturedResponse response = new UncapturedResponse(correlationId);
        if (!responseQueue.offer(response))
        {
            throw new IllegalStateException("unable to offer response");
        }
    }

    public void onRoutedResponse(long correlationId)
    {
        RoutedResponse response = new RoutedResponse(correlationId);
        if (!responseQueue.offer(response))
        {
            throw new IllegalStateException("unable to offer response");
        }
    }

    public void onUnroutedResponse(long correlationId)
    {
        UnroutedResponse response = new UnroutedResponse(correlationId);
        if (!responseQueue.offer(response))
        {
            throw new IllegalStateException("unable to offer response");
        }
    }

    public void onBoundResponse(
        long correlationId,
        long referenceId)
    {
        BoundResponse response = new BoundResponse(correlationId, referenceId);
        if (!responseQueue.offer(response))
        {
            throw new IllegalStateException("unable to offer response");
        }
    }

    public void onUnboundResponse(
        long correlationId,
        String destination,
        long destinationRef,
        String source,
        Map<String, String> headers)
    {
        UnboundResponse response = new UnboundResponse(correlationId, destination, destinationRef, source, headers);
        if (!responseQueue.offer(response))
        {
            throw new IllegalStateException("unable to offer response");
        }
    }

    public void onPreparedResponse(
        long correlationId,
        long referenceId)
    {
        PreparedResponse response = new PreparedResponse(correlationId, referenceId);
        if (!responseQueue.offer(response))
        {
            throw new IllegalStateException("unable to offer response");
        }
    }

    public void onUnpreparedResponse(
        long correlationId,
        String destination,
        long destinationRef,
        String source,
        Map<String, String> headers)
    {
        UnpreparedResponse response = new UnpreparedResponse(correlationId, destination, destinationRef, source, headers);
        if (!responseQueue.offer(response))
        {
            throw new IllegalStateException("unable to offer response");
        }
    }
}