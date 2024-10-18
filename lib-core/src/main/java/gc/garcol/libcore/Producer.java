package gc.garcol.libcore;

/**
 * @author thaivc
 * @since 2024
 */
public interface Producer
{
    boolean publish(int messageTypeId, byte[] message);
}
