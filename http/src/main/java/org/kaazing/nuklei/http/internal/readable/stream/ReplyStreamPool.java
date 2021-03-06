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
package org.kaazing.nuklei.http.internal.readable.stream;

import static java.lang.Character.toUpperCase;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.kaazing.nuklei.http.internal.types.stream.DataFW;
import org.kaazing.nuklei.http.internal.types.stream.HttpBeginFW;
import org.kaazing.nuklei.http.internal.types.stream.HttpDataFW;
import org.kaazing.nuklei.http.internal.types.stream.HttpEndFW;

import uk.co.real_logic.agrona.MutableDirectBuffer;
import uk.co.real_logic.agrona.concurrent.AtomicBuffer;
import uk.co.real_logic.agrona.concurrent.MessageHandler;
import uk.co.real_logic.agrona.concurrent.ringbuffer.RingBuffer;

public final class ReplyStreamPool
{
    private final HttpBeginFW httpBeginRO = new HttpBeginFW();
    private final HttpDataFW httpDataRO = new HttpDataFW();
    private final HttpEndFW httpEndRO = new HttpEndFW();

    private final DataFW.Builder dataRW = new DataFW.Builder();

    private final AtomicBuffer atomicBuffer;

    public ReplyStreamPool(
        int capacity,
        AtomicBuffer atomicBuffer)
    {
        this.atomicBuffer = atomicBuffer;
    }

    public MessageHandler acquire(
        long sourceReplyStreamId,
        RingBuffer sourceRoute,
        Consumer<MessageHandler> released)
    {
        return new ReplyStream(released, sourceReplyStreamId, sourceRoute);
    }

    private final class ReplyStream implements MessageHandler
    {
        private final Consumer<MessageHandler> cleanup;
        private final long sourceReplyStreamId;
        private final RingBuffer sourceRoute;

        private ReplyStream(
            Consumer<MessageHandler> cleanup,
            long sourceReplyStreamId,
            RingBuffer sourceRoute)
        {
            this.cleanup = cleanup;
            this.sourceReplyStreamId = sourceReplyStreamId;
            this.sourceRoute = sourceRoute;
        }

        @Override
        public void onMessage(
            int msgTypeId,
            MutableDirectBuffer buffer,
            int index,
            int length)
        {
            switch (msgTypeId)
            {
            case HttpBeginFW.TYPE_ID:
                onBegin(buffer, index, length);
                break;
            case HttpDataFW.TYPE_ID:
                onData(buffer, index, length);
                break;
            case HttpEndFW.TYPE_ID:
                onEnd(buffer, index, length);
                break;
            }
        }

        private void onBegin(
            MutableDirectBuffer buffer,
            int index,
            int length)
        {
            httpBeginRO.wrap(buffer, index, index + length);

            // default status (and reason)
            String[] status = new String[] { "200", "OK" };

            StringBuilder headers = new StringBuilder();
            httpBeginRO.headers().forEach(header ->
            {
                String name = header.name().asString();
                String value = header.value().asString();

                if (":status".equals(name))
                {
                    status[0] = value;
                    if ("101".equals(status[0]))
                    {
                        status[1] = "Switching Protocols";
                    }
                }
                else
                {
                    headers.append(toUpperCase(name.charAt(0))).append(name.substring(1))
                           .append(": ").append(value).append("\r\n");
                }
            });

            String payloadChars =
                    new StringBuilder().append("HTTP/1.1 ").append(status[0]).append(" ").append(status[1]).append("\r\n")
                                       .append(headers).append("\r\n").toString();

            final DataFW data = dataRW.wrap(atomicBuffer, 0, atomicBuffer.capacity())
                                      .streamId(sourceReplyStreamId)
                                      .payload(payloadChars.getBytes(StandardCharsets.US_ASCII))
                                      .build();

            if (!sourceRoute.write(data.typeId(), data.buffer(), data.offset(), data.length()))
            {
                 throw new IllegalStateException("could not write to ring buffer");
            }
        }

        private void onData(
            MutableDirectBuffer buffer,
            int index,
            int length)
        {
            httpDataRO.wrap(buffer, index, index + length);

            // TODO: unwrap chunk syntax (if necessary - not after 101 upgrade)

            final DataFW data = dataRW.wrap(atomicBuffer, 0, atomicBuffer.capacity())
                                      .streamId(sourceReplyStreamId)
                                      .payload(httpDataRO.payload())
                                      .build();

            if (!sourceRoute.write(data.typeId(), data.buffer(), data.offset(), data.length()))
            {
                 throw new IllegalStateException("could not write to ring buffer");
            }
        }

        private void onEnd(
            MutableDirectBuffer buffer,
            int index,
            int length)
        {
            httpEndRO.wrap(buffer, index, index + length);

            // TODO

            // release
            cleanup.accept(this);
        }
    }

}
