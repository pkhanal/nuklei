/*
 * Copyright 2015, Kaazing Corporation. All rights reserved.
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

package org.kaazing.nuklei.tcp.internal.acceptor;

import org.kaazing.nuklei.tcp.internal.conductor.Conductor;


public final class BoundResponse implements AcceptorResponse
{
    private final long correlationId;
    private final long bindingRef;

    public BoundResponse(
        long correlationId,
        long bindingRef)
    {
        this.correlationId = correlationId;
        this.bindingRef = bindingRef;
    }

    public void execute(Conductor conductor)
    {
        conductor.onBoundResponse(correlationId, bindingRef);
    }
}
