package gc.garcol.libbenchmark;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

/**
 * @author thaivc
 * @since 2024
 */
public class OneToManyRingBufferRunner
{

    public static void main(String[] args) throws RunnerException
    {
        Options options = new OptionsBuilder()
            .include(OneToManyRingBufferBechmark.class.getSimpleName())
            .resultFormat(ResultFormatType.JSON)
//            .measurementTime(TimeValue.seconds(10))
            .result("benchmark-result/one-to-many.json")
            .build();
        new Runner(options).run();
    }

}
