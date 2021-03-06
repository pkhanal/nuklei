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
package org.kaazing.nuklei.echo.internal.control;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;
import org.kaazing.nuklei.test.NukleusRule;

public class ControlIT
{
    private final K3poRule k3po = new K3poRule()
        .setScriptRoot("org/kaazing/specification/nuklei/echo/control");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    private final NukleusRule nukleus = new NukleusRule("echo")
        .directory("target/nukleus-itests")
        .commandBufferCapacity(1024)
        .responseBufferCapacity(1024)
        .counterValuesBufferCapacity(1024)
        .initialize("source", "echo")
        .initialize("destination", "echo");

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout).around(nukleus);

    @Test
    @Specification({
        "capture.source/controller"
    })
    public void shouldCaptureSource() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "capture.destination/controller"
    })
    public void shouldCaptureDestination() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "capture.source/controller",
        "route.source/controller"
    })
    public void shouldRouteSource() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "capture.destination/controller",
        "route.destination/controller"
    })
    public void shouldRouteDestination() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "capture.source/controller",
        "route.source/controller",
        "bind.source/controller"
    })
    public void shouldBindSource() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "capture.source/controller",
        "route.source/controller",
        "bind.source/controller",
        "unbind.source/controller"
    })
    public void shouldUnbindSource() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "capture.destination/controller",
        "route.destination/controller",
        "prepare.destination/controller"
    })
    public void shouldPrepareDestination() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "capture.destination/controller",
        "route.destination/controller",
        "prepare.destination/controller",
        "unprepare.destination/controller"
    })
    public void shouldUnprepareDestination() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "capture.destination/controller",
        "route.destination/controller",
        "prepare.destination/controller",
        "connect.destination/controller"
    })
    public void shouldConnectDestination() throws Exception
    {
        k3po.finish();
    }
}
