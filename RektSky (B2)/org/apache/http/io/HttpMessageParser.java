package org.apache.http.io;

import java.io.*;
import org.apache.http.*;

public interface HttpMessageParser<T extends HttpMessage>
{
    T parse() throws IOException, HttpException;
}
