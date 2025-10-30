package frc.robot;

import badgerlog.annotations.Entry;
import badgerlog.annotations.EntryType;

public class StaticTest {
    @Entry(EntryType.PUBLISHER)
    public static double staticDouble = 4;
}
