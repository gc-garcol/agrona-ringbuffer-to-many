# Agrona OneToManyRingBuffer

`Agrona` collections currently only provide `OneToOneRingBuffer` and `ManyToOneRingBuffer`. This project offers a simple implementation of `OneToManyRingBuffer` and `ManyToManyRingBuffer` using Agrona's concurrent collections.

## OneToManyRingBuffer

- Customize the `OneToOneRingBufferReader` to support shared reading of the `AtomicBuffer`.
- The `ConsumerTemplate` includes a `currentBarrier` to signal the `next-consumer` in the `pipeline` (or the producer if the `consumer` is the last one).
- The `ProducerSingle` monitors the `lastConsumerBarrier` to apply backpressure when necessary.

## ManyToManyRingBuffer

`ManyToManyRingBuffer` = `ManyToOneRingBuffer` + `OneToManyRingBuffer`

## Examples
The <b>examples</b> can be found at `lib-playground`

- Create `consumers`
```java
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
```

- Create the `RingBufferOneToMany`, where the `producer` will be initialized as part of the `RingBufferOneToMany`.
```java
RingBufferOneToMany ringBufferOneToMany = new RingBufferOneToMany(8, consumerTemplates);
```

- Publish messages:
```java
for (int i = 0; i < 10; i++)
{
    boolean success = ringBufferOneToMany.producer()
        .publish(1, ("hello world!!!!!!! " + i).getBytes());
}
```

- Simulate polling for messages:
```java
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
```

## Note in agrona

`OneToOneRingBuffer`:
- `tail` for write
- `head` for read

## Benchmark Result

### OneToManyRingBuffer with 2 consumers

```shell
Benchmark                                                                Mode     Cnt       Score        Error   Units
OneToManyRingBufferBechmark.OneToManyRingBuffer                         thrpt       3  417842.147 ± 282989.811  ops/ms
OneToManyRingBufferBechmark.OneToManyRingBuffer:firstConsume            thrpt       3  152852.276 ± 109237.339  ops/ms
OneToManyRingBufferBechmark.OneToManyRingBuffer:publish                 thrpt       3   96956.800 ±   9018.261  ops/ms
OneToManyRingBufferBechmark.OneToManyRingBuffer:secondConsume           thrpt       3  168033.071 ± 164862.794  ops/ms
OneToManyRingBufferBechmark.OneToManyRingBuffer                          avgt       3      ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:firstConsume             avgt       3      ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:publish                  avgt       3      ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:secondConsume            avgt       3      ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer                        sample  778430      ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:firstConsume           sample  287743      ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:firstConsume:p0.00     sample                 ≈ 0                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:firstConsume:p0.50     sample              ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:firstConsume:p0.90     sample              ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:firstConsume:p0.95     sample              ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:firstConsume:p0.99     sample              ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:firstConsume:p0.999    sample              ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:firstConsume:p0.9999   sample               0.023                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:firstConsume:p1.00     sample               1.503                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:p0.00                  sample                 ≈ 0                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:p0.50                  sample              ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:p0.90                  sample              ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:p0.95                  sample              ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:p0.99                  sample              ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:p0.999                 sample              ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:p0.9999                sample               0.025                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:p1.00                  sample               1.589                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:publish                sample  196737      ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:publish:p0.00          sample                 ≈ 0                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:publish:p0.50          sample              ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:publish:p0.90          sample              ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:publish:p0.95          sample              ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:publish:p0.99          sample              ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:publish:p0.999         sample              ≈ 10⁻³                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:publish:p0.9999        sample               0.068                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:publish:p1.00          sample               1.399                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:secondConsume          sample  293950      ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:secondConsume:p0.00    sample                 ≈ 0                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:secondConsume:p0.50    sample              ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:secondConsume:p0.90    sample              ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:secondConsume:p0.95    sample              ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:secondConsume:p0.99    sample              ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:secondConsume:p0.999   sample              ≈ 10⁻⁴                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:secondConsume:p0.9999  sample               0.024                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:secondConsume:p1.00    sample               1.589                ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer                            ss       3       0.016 ±      0.135   ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:firstConsume               ss       3       0.012 ±      0.040   ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:publish                    ss       3       0.022 ±      0.130   ms/op
OneToManyRingBufferBechmark.OneToManyRingBuffer:secondConsume              ss       3       0.014 ±      0.238   ms/op
```
