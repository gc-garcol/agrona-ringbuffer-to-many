package gc.garcol.libcore;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.ControlledMessageHandler;
import org.agrona.concurrent.ringbuffer.ManyToOneRingBuffer;

/**
 * @author thaivc
 * @since 2024
 */
public class RingBufferManyToMany implements Agent
{
    private ManyToOneRingBuffer inboundBuffer;
    private RingBufferOneToMany oneToManyRingBuffer;

    boolean publish(int messageTypeId, byte[] message)
    {
        final int claimIndex = inboundBuffer.tryClaim(messageTypeId, message.length);
        if (claimIndex <= 0)
        {
            return false;
        }
        inboundBuffer.buffer().putBytes(claimIndex, message);
        inboundBuffer.commit(claimIndex);
        return true;
    }

    public int doWork() throws Exception
    {
        inboundBuffer.controlledRead(this::onInboundMessage);
        return 0;
    }

    private ControlledMessageHandler.Action onInboundMessage(int msgTypeId, MutableDirectBuffer buffer, int index, int length)
    {
        byte[] message = new byte[length];
        buffer.getBytes(index, message);
        boolean enqueueSuccess = oneToManyRingBuffer.producer().publish(msgTypeId, message);
        return enqueueSuccess ? ControlledMessageHandler.Action.COMMIT : ControlledMessageHandler.Action.ABORT;
    }

    public String roleName()
    {
        return "ManyToManyRingBuffer";
    }
}
