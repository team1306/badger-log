package frc.robot.testing.performance;

import badgerlog.annotations.Entry;
import badgerlog.annotations.EntryType;

public class SingleAnnotation {
    @Entry(EntryType.PUBLISHER)
    public Integer integer = 0;
}
