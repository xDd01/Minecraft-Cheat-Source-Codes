package oshi.software.os.mac;

import oshi.hardware.*;
import oshi.util.*;
import java.util.*;
import oshi.software.os.mac.local.*;

public class MacHardwareAbstractionLayer implements HardwareAbstractionLayer
{
    private Processor[] _processors;
    private Memory _memory;
    
    public Processor[] getProcessors() {
        if (this._processors == null) {
            final List<Processor> processors = new ArrayList<Processor>();
            for (int nbCPU = new Integer(ExecutingCommand.getFirstAnswer("sysctl -n hw.logicalcpu")), i = 0; i < nbCPU; ++i) {
                processors.add(new CentralProcessor());
            }
            this._processors = processors.toArray(new Processor[0]);
        }
        return this._processors;
    }
    
    public Memory getMemory() {
        if (this._memory == null) {
            this._memory = new GlobalMemory();
        }
        return this._memory;
    }
}
