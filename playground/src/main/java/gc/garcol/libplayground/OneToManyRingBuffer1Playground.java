package gc.garcol.libplayground;

import gc.garcol.libcore.ConsumerTemplate;
import gc.garcol.libcore.ProducerSingle;
import gc.garcol.libcore.RingBufferOneToMany;
import org.agrona.MutableDirectBuffer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.agrona.concurrent.broadcast.RecordDescriptor.HEADER_LENGTH;

/**
 * @author thaivc
 * @since 2024
 */
public class OneToManyRingBuffer1Playground
{
    static AtomicInteger counter = new AtomicInteger(0);
    public static void main(String[] args)
    {

        List<ConsumerTemplate> consumerTemplates = List.of(
            new ConsumerTemplate()
            {
                public void consume(final int msgTypeId, final MutableDirectBuffer buffer, final int index, final int length)
                {
                    consumeMessage("Consumer 0", buffer, index, length);
                }
            },
            new ConsumerTemplate()
            {
                public void consume(final int msgTypeId, final MutableDirectBuffer buffer, final int index, final int length)
                {
                    consumeMessage("Consumer 1", buffer, index, length);
                }
            },
            new ConsumerTemplate()
            {
                public void consume(final int msgTypeId, final MutableDirectBuffer buffer, final int index, final int length)
                {
                    consumeMessage("Consumer 2", buffer, index, length);
                }
            }
        );

        RingBufferOneToMany ringBufferOneToMany = new RingBufferOneToMany(8, consumerTemplates);
        System.out.println("capacity " + ringBufferOneToMany.ringBuffer()
            .capacity() + " | maxMsgLength: " + ringBufferOneToMany.ringBuffer().maxMsgLength());

        for (int i = 0; i < 100; i++)
        {
            System.out.println("start at loop " + i);
            inspectProducer(ringBufferOneToMany.producer());
            inspectConsumer(consumerTemplates);
            publishMessage(ringBufferOneToMany);

            inspectProducer(ringBufferOneToMany.producer());
            inspectConsumer(consumerTemplates);
            consumerTemplates.get(0).poll();
            consumerTemplates.get(1).poll();
            consumerTemplates.get(2).poll();
            inspectProducer(ringBufferOneToMany.producer());
            inspectConsumer(consumerTemplates);
        }
    }

    public static void consumeMessage(String consumerName, MutableDirectBuffer buffer, int index, int length)
    {
        byte[] message = new byte[length];
        buffer.getBytes(index, message);
        System.out.printf("[%s] at [%s] message: %s%n", consumerName, index - HEADER_LENGTH, new String(message));
    }

    static void publishMessage(RingBufferOneToMany ringBufferOneToMany)
    {
        System.out.println("----- publish message -----");
        for (int i = 0; i < 10; i++)
        {
            boolean success = ringBufferOneToMany.producer().publish(1, ("hello world!!!!!!! " + (counter.get() + 1)).getBytes());
            if (!success)
            {
                System.out.println("STOP publish at loop: " + i + " | producer flip: " + ringBufferOneToMany.producer()
                    .currentBarrier().flip() + " | producer index: " + ringBufferOneToMany.producer().currentBarrier()
                    .index());
                break;
            }
            counter.getAndIncrement();
            System.out.println("publish at loop: " + i + " | producer flip: " + ringBufferOneToMany.producer()
                .currentBarrier().flip() + " | producer index: " + ringBufferOneToMany.producer().currentBarrier()
                .index());
        }
    }

    static void assetTrue(boolean condition, String message)
    {
        if (!condition)
        {
            throw new RuntimeException(message);
        }
    }

    static void inspectProducer(ProducerSingle producerSingle)
    {
        System.out.println("_____");
        System.out.println("Producer | flip: " + producerSingle.currentBarrier()
            .flip() + " | index: " + producerSingle.currentBarrier().index());
    }

    static void inspectConsumer(List<ConsumerTemplate> consumerTemplates)
    {
        for (int i = 0; i < consumerTemplates.size(); i++)
        {
            System.out.println("Consumer " + i + " | flip: " + consumerTemplates.get(i).currentBarrier()
                .flip() + " | index: " + consumerTemplates.get(i).currentBarrier().index());
        }
        System.out.println("_____====_____");
    }
}
