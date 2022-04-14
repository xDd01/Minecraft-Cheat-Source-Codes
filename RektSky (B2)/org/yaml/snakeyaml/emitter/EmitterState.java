package org.yaml.snakeyaml.emitter;

import java.io.*;

interface EmitterState
{
    void expect() throws IOException;
}
