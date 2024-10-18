package gc.garcol.libbenchmark;

import gc.garcol.libcore.ConsumerTemplate;
import gc.garcol.libcore.RingBufferOneToMany;
import org.agrona.MutableDirectBuffer;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author thaivc
 * @since 2024
 */
@State(Scope.Benchmark)
public class OneToManyRingBufferPlan
{

    RingBufferOneToMany ringBuffer;
    int data = ThreadLocalRandom.current().nextInt();
    byte[] message = new byte[] { (byte)(data >> 24), (byte)(data >> 16), (byte)(data >> 8), (byte)data };

    @Setup(Level.Trial)
    public void setUp()
    {
        List<ConsumerTemplate> consumerTemplates = List.of(
            new ConsumerTemplate()
            {
                public void consume(final int msgTypeId, final MutableDirectBuffer buffer, final int index, final int length)
                {
                    buffer.getInt(index);
                }
            },
            new ConsumerTemplate()
            {
                public void consume(final int msgTypeId, final MutableDirectBuffer buffer, final int index, final int length)
                {
                    buffer.getInt(index);
                }
            }
        );
        ringBuffer = new RingBufferOneToMany(22, consumerTemplates);
    }
}
