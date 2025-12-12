package badgerlog;

import frc.robot.testing.performance.NoAnnotation;
import frc.robot.testing.performance.SingleAnnotation;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 5)
@Fork(1)
public class EntryCreationBenchmark {
    
    @Benchmark
    public void normalIntegerCreation(){
        new NoAnnotation();
    }
    
    @Benchmark
    public void entryIntegerCreation(){
        new SingleAnnotation();
    }
}
