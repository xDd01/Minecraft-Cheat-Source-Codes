package org.yaml.snakeyaml.parser;

import org.yaml.snakeyaml.events.*;

interface Production
{
    Event produce();
}
