package shadersmod.common;

import java.io.*;
import java.util.logging.*;

class SMCLog$SMCLogger$1 extends StreamHandler {
    @Override
    public synchronized void publish(final LogRecord record) {
        super.publish(record);
        this.flush();
    }
}