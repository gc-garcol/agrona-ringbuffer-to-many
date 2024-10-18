package gc.garcol.libbenchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author thaivc
 * @since 2024
 */
@State(Scope.Group)
@BenchmarkMode({ Mode.All })
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 3)
@Fork(1)
public class OneToManyRingBufferBechmark
{
    @Benchmark
    @GroupThreads(1)
    @Group("OneToManyRingBuffer")
    public boolean publish(OneToManyRingBufferPlan ringBufferPlan, Blackhole blackhole) throws IOException
    {
        var success = ringBufferPlan.ringBuffer.producer().publish(1, ringBufferPlan.message);
        blackhole.consume(success);
        return success;
    }

    @Benchmark
    @GroupThreads(1)
    @Group("OneToManyRingBuffer")
    public boolean firstConsume(OneToManyRingBufferPlan ringBufferPlan, Blackhole blackhole) throws IOException
    {
        var totalConsumed = ringBufferPlan.ringBuffer.consumers().getFirst().poll();
        blackhole.consume(totalConsumed);
        return totalConsumed > 0;
    }

    @Benchmark
    @GroupThreads(1)
    @Group("OneToManyRingBuffer")
    public boolean secondConsume(OneToManyRingBufferPlan ringBufferPlan, Blackhole blackhole) throws IOException
    {
        var totalConsumed = ringBufferPlan.ringBuffer.consumers().getLast().poll();
        blackhole.consume(totalConsumed);
        return totalConsumed > 0;
    }
}
