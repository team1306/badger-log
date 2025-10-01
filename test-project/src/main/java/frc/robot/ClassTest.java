package frc.robot;

import badgerlog.annotations.Entry;
import badgerlog.annotations.EntryType;
import badgerlog.annotations.NoEntry;

@Entry(EntryType.INTELLIGENT)
public class ClassTest implements Testing{

    public int accessibleInteger = 2;
    
    @NoEntry
    public String notAccessibleString = "not accessible";
    
    @Entry(EntryType.INTELLIGENT)
    public boolean customEntryBool = true;
    public final double finalDouble = 5.3;
    private boolean privateBoolean = true;
    
    
    @Override
    public void initialize() {}

    @Override
    public void update() {
        accessibleInteger++;
        privateBoolean = !privateBoolean;
    }
}
