package gc.garcol.libplayground;

import gc.garcol.libcore.ConsumerTemplate;
import gc.garcol.libcore.ProducerSingle;
import gc.garcol.libcore.RingBufferOneToMany;
import org.agrona.MutableDirectBuffer;

import java.util.List;

import static org.agrona.ExpandableRingBuffer.HEADER_LENGTH;

/**
 * @author thaivc
 * @since 2024
 */
public class OneToManyRingBufferPlayground
{
    public static void main(String[] args) throws InterruptedException
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

        publishMessage(ringBufferOneToMany);

        System.out.println("consumer 0,1 poll all | consumer 2 polls 3");
        new Thread(() -> {
            consumerTemplates.get(0).poll();
        }).start();
        new Thread(() -> {
            try
            {
                Thread.sleep(1_000);
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
            consumerTemplates.get(1).poll();
        }).start();
        new Thread(() -> {
            try
            {
                Thread.sleep(2_000);
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
            consumerTemplates.get(2).poll();
        }).start();
        Thread.sleep(3_000);
        inspectProducer(ringBufferOneToMany.producer());
        inspectConsumer(consumerTemplates);
//
//        publishMessage(ringBufferOneToMany);
//
//        new Thread(() -> {
//            consumerTemplates.get(0).poll();
//        }).start();
//        new Thread(() -> {
//            consumerTemplates.get(1).poll();
//        }).start();
//        new Thread(() -> {
//            consumerTemplates.get(2).poll();
//        }).start();
//        inspectProducer(ringBufferOneToMany.producer());
//        inspectConsumer(consumerTemplates);
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
            boolean success = ringBufferOneToMany.producer()
                .publish(1, ("hello world!!!!!!! " + i).getBytes());
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
