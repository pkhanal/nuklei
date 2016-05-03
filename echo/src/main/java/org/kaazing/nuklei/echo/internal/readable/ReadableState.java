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
package org.kaazing.nuklei.echo.internal.readable;

import uk.co.real_logic.agrona.concurrent.ringbuffer.RingBuffer;

public class ReadableState
{
    private final long sourceRef;
    private final long destinationRef;
    private final RingBuffer routeBuffer;

    public ReadableState(
        long sourceRef,
        long destinationRef,
        RingBuffer routeBuffer)
    {
        this.sourceRef = sourceRef;
        this.destinationRef = destinationRef;
        this.routeBuffer = routeBuffer;
    }

    public long sourceRef()
    {
        return this.sourceRef;
    }

    public long destinationRef()
    {
        return this.destinationRef;
    }

    public RingBuffer routeBuffer()
    {
        return routeBuffer;
    }

    @Override
    public String toString()
    {
        return String.format("[sourceRef=%d]", sourceRef());
    }

}
