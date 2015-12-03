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
package org.kaazing.nuklei.http.internal.translator;

import java.util.function.Consumer;

import org.kaazing.nuklei.Nukleus;
import org.kaazing.nuklei.http.internal.Context;
import org.kaazing.nuklei.http.internal.conductor.ConductorProxy;

import uk.co.real_logic.agrona.concurrent.OneToOneConcurrentArrayQueue;

public final class Translator implements Nukleus, Consumer<TranslatorCommand>
{
    private final ConductorProxy conductorProxy;
    private final OneToOneConcurrentArrayQueue<TranslatorCommand> commandQueue;

    public Translator(Context context)
    {
        this.conductorProxy = new ConductorProxy(context);
        this.commandQueue = context.translatorCommandQueue();
    }

    @Override
    public int process() throws Exception
    {
        int weight = 0;

        weight += commandQueue.drain(this);

        // TODO: read streams

        return weight;
    }

    @Override
    public String name()
    {
        return "translator";
    }

    @Override
    public void accept(TranslatorCommand command)
    {
        command.execute(this);
    }

    public void doCapture(
        long correlationId,
        String handler)
    {
        // TODO
    }

    public void doUncapture(
        long correlationId,
        String handler)
    {
        // TODO
    }

    public void doRoute(
        long correlationId,
        String destination)
    {
        // TODO
    }

    public void doUnroute(
        long correlationId,
        String destination)
    {
        // TODO
    }

    public void doBind(
        long correlationId,
        String source,
        long sourceRef)
    {
        // TODO
    }

    public void doUnbind(
        long correlationId,
        long referenceId)
    {
        // TODO
    }

    public void doPrepare(
        long correlationId,
        String destination,
        long destinationRef)
    {
        // TODO
    }

    public void doUnprepare(
        long correlationId,
        long referenceId)
    {
        // TODO
    }
}
