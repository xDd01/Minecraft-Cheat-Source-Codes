package org.apache.commons.compress.compressors.deflate64;

enum HuffmanState
{
    INITIAL, 
    STORED, 
    DYNAMIC_CODES, 
    FIXED_CODES;
}
