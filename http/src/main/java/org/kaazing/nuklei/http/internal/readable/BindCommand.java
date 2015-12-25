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
package org.kaazing.nuklei.http.internal.readable;

import static java.lang.String.format;

import java.util.Map;

import uk.co.real_logic.agrona.concurrent.ringbuffer.RingBuffer;

public final class BindCommand implements ReadableCommand
{
    private final long correlationId;
    private final long destinationRef;
    private final ReadableProxy destination;
    private final Map<String, String> headers;
    private final RingBuffer sourceRoute;
    private final RingBuffer destinationRoute;

    public BindCommand(
        long correlationId,
        long destinationRef,
        Map<String, String> headers,
        ReadableProxy destination,
        RingBuffer sourceRoute,
        RingBuffer destinationRoute)
    {
        this.correlationId = correlationId;
        this.destinationRef = destinationRef;
        this.headers = headers;
        this.destination = destination;
        this.sourceRoute = sourceRoute;
        this.destinationRoute = destinationRoute;
    }

    @Override
    public void execute(Readable source)
    {
        source.doBind(correlationId, destinationRef, headers, destination, sourceRoute, destinationRoute);
    }

    @Override
    public String toString()
    {
        return format("BIND [correlationId=%d, destinationRef=%d, headers=%s, destination=\"%s\"]",
                correlationId, destinationRef, headers, destination);
    }
}