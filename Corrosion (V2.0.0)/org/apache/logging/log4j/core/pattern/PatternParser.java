/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.pattern;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.PluginManager;
import org.apache.logging.log4j.core.config.plugins.PluginType;
import org.apache.logging.log4j.core.helpers.Strings;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.ExtendedThrowablePatternConverter;
import org.apache.logging.log4j.core.pattern.FormattingInfo;
import org.apache.logging.log4j.core.pattern.LiteralPatternConverter;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.status.StatusLogger;

public final class PatternParser {
    private static final char ESCAPE_CHAR = '%';
    private static final Logger LOGGER = StatusLogger.getLogger();
    private static final int BUF_SIZE = 32;
    private static final int DECIMAL = 10;
    private final Configuration config;
    private final Map<String, Class<PatternConverter>> converterRules;

    public PatternParser(String converterKey) {
        this(null, converterKey, null, null);
    }

    public PatternParser(Configuration config, String converterKey, Class<?> expected) {
        this(config, converterKey, expected, null);
    }

    public PatternParser(Configuration config, String converterKey, Class<?> expectedClass, Class<?> filterClass) {
        this.config = config;
        PluginManager manager = new PluginManager(converterKey, expectedClass);
        manager.collectPlugins();
        Map<String, PluginType<?>> plugins = manager.getPlugins();
        HashMap<String, Class<PatternConverter>> converters = new HashMap<String, Class<PatternConverter>>();
        for (PluginType<?> type : plugins.values()) {
            try {
                ConverterKeys keys;
                Class<?> clazz = type.getPluginClass();
                if (filterClass != null && !filterClass.isAssignableFrom(clazz) || (keys = clazz.getAnnotation(ConverterKeys.class)) == null) continue;
                for (String key : keys.value()) {
                    converters.put(key, clazz);
                }
            }
            catch (Exception ex2) {
                LOGGER.error("Error processing plugin " + type.getElementName(), (Throwable)ex2);
            }
        }
        this.converterRules = converters;
    }

    public List<PatternFormatter> parse(String pattern) {
        return this.parse(pattern, false);
    }

    public List<PatternFormatter> parse(String pattern, boolean alwaysWriteExceptions) {
        ArrayList<PatternFormatter> list = new ArrayList<PatternFormatter>();
        ArrayList<PatternConverter> converters = new ArrayList<PatternConverter>();
        ArrayList<FormattingInfo> fields = new ArrayList<FormattingInfo>();
        this.parse(pattern, converters, fields);
        Iterator fieldIter = fields.iterator();
        boolean handlesThrowable = false;
        for (PatternConverter converter : converters) {
            LogEventPatternConverter pc2;
            if (converter instanceof LogEventPatternConverter) {
                pc2 = (LogEventPatternConverter)converter;
                handlesThrowable |= pc2.handlesThrowable();
            } else {
                pc2 = new LiteralPatternConverter(this.config, "");
            }
            FormattingInfo field = fieldIter.hasNext() ? (FormattingInfo)fieldIter.next() : FormattingInfo.getDefault();
            list.add(new PatternFormatter(pc2, field));
        }
        if (alwaysWriteExceptions && !handlesThrowable) {
            ExtendedThrowablePatternConverter pc3 = ExtendedThrowablePatternConverter.newInstance(null);
            list.add(new PatternFormatter(pc3, FormattingInfo.getDefault()));
        }
        return list;
    }

    private static int extractConverter(char lastChar, String pattern, int i2, StringBuilder convBuf, StringBuilder currentLiteral) {
        convBuf.setLength(0);
        if (!Character.isUnicodeIdentifierStart(lastChar)) {
            return i2;
        }
        convBuf.append(lastChar);
        while (i2 < pattern.length() && Character.isUnicodeIdentifierPart(pattern.charAt(i2))) {
            convBuf.append(pattern.charAt(i2));
            currentLiteral.append(pattern.charAt(i2));
            ++i2;
        }
        return i2;
    }

    private static int extractOptions(String pattern, int i2, List<String> options) {
        while (i2 < pattern.length() && pattern.charAt(i2) == '{') {
            int end;
            int begin = i2++;
            int depth = 0;
            do {
                if ((end = pattern.indexOf(125, i2)) == -1) continue;
                int next = pattern.indexOf("{", i2);
                if (next != -1 && next < end) {
                    i2 = end + 1;
                    ++depth;
                    continue;
                }
                if (depth <= 0) continue;
                --depth;
            } while (depth > 0);
            if (end == -1) break;
            String r2 = pattern.substring(begin + 1, end);
            options.add(r2);
            i2 = end + 1;
        }
        return i2;
    }

    public void parse(String pattern, List<PatternConverter> patternConverters, List<FormattingInfo> formattingInfos) {
        if (pattern == null) {
            throw new NullPointerException("pattern");
        }
        StringBuilder currentLiteral = new StringBuilder(32);
        int patternLength = pattern.length();
        ParserState state = ParserState.LITERAL_STATE;
        int i2 = 0;
        FormattingInfo formattingInfo = FormattingInfo.getDefault();
        while (i2 < patternLength) {
            char c2 = pattern.charAt(i2++);
            block0 : switch (state) {
                case LITERAL_STATE: {
                    if (i2 == patternLength) {
                        currentLiteral.append(c2);
                        break;
                    }
                    if (c2 == '%') {
                        switch (pattern.charAt(i2)) {
                            case '%': {
                                currentLiteral.append(c2);
                                ++i2;
                                break block0;
                            }
                        }
                        if (currentLiteral.length() != 0) {
                            patternConverters.add(new LiteralPatternConverter(this.config, currentLiteral.toString()));
                            formattingInfos.add(FormattingInfo.getDefault());
                        }
                        currentLiteral.setLength(0);
                        currentLiteral.append(c2);
                        state = ParserState.CONVERTER_STATE;
                        formattingInfo = FormattingInfo.getDefault();
                        break;
                    }
                    currentLiteral.append(c2);
                    break;
                }
                case CONVERTER_STATE: {
                    currentLiteral.append(c2);
                    switch (c2) {
                        case '-': {
                            formattingInfo = new FormattingInfo(true, formattingInfo.getMinLength(), formattingInfo.getMaxLength());
                            break block0;
                        }
                        case '.': {
                            state = ParserState.DOT_STATE;
                            break block0;
                        }
                    }
                    if (c2 >= '0' && c2 <= '9') {
                        formattingInfo = new FormattingInfo(formattingInfo.isLeftAligned(), c2 - 48, formattingInfo.getMaxLength());
                        state = ParserState.MIN_STATE;
                        break;
                    }
                    i2 = this.finalizeConverter(c2, pattern, i2, currentLiteral, formattingInfo, this.converterRules, patternConverters, formattingInfos);
                    state = ParserState.LITERAL_STATE;
                    formattingInfo = FormattingInfo.getDefault();
                    currentLiteral.setLength(0);
                    break;
                }
                case MIN_STATE: {
                    currentLiteral.append(c2);
                    if (c2 >= '0' && c2 <= '9') {
                        formattingInfo = new FormattingInfo(formattingInfo.isLeftAligned(), formattingInfo.getMinLength() * 10 + c2 - 48, formattingInfo.getMaxLength());
                        break;
                    }
                    if (c2 == '.') {
                        state = ParserState.DOT_STATE;
                        break;
                    }
                    i2 = this.finalizeConverter(c2, pattern, i2, currentLiteral, formattingInfo, this.converterRules, patternConverters, formattingInfos);
                    state = ParserState.LITERAL_STATE;
                    formattingInfo = FormattingInfo.getDefault();
                    currentLiteral.setLength(0);
                    break;
                }
                case DOT_STATE: {
                    currentLiteral.append(c2);
                    if (c2 >= '0' && c2 <= '9') {
                        formattingInfo = new FormattingInfo(formattingInfo.isLeftAligned(), formattingInfo.getMinLength(), c2 - 48);
                        state = ParserState.MAX_STATE;
                        break;
                    }
                    LOGGER.error("Error occurred in position " + i2 + ".\n Was expecting digit, instead got char \"" + c2 + "\".");
                    state = ParserState.LITERAL_STATE;
                    break;
                }
                case MAX_STATE: {
                    currentLiteral.append(c2);
                    if (c2 >= '0' && c2 <= '9') {
                        formattingInfo = new FormattingInfo(formattingInfo.isLeftAligned(), formattingInfo.getMinLength(), formattingInfo.getMaxLength() * 10 + c2 - 48);
                        break;
                    }
                    i2 = this.finalizeConverter(c2, pattern, i2, currentLiteral, formattingInfo, this.converterRules, patternConverters, formattingInfos);
                    state = ParserState.LITERAL_STATE;
                    formattingInfo = FormattingInfo.getDefault();
                    currentLiteral.setLength(0);
                }
            }
        }
        if (currentLiteral.length() != 0) {
            patternConverters.add(new LiteralPatternConverter(this.config, currentLiteral.toString()));
            formattingInfos.add(FormattingInfo.getDefault());
        }
    }

    private PatternConverter createConverter(String converterId, StringBuilder currentLiteral, Map<String, Class<PatternConverter>> rules, List<String> options) {
        Object[] parms;
        String converterName = converterId;
        Class<PatternConverter> converterClass = null;
        for (int i2 = converterId.length(); i2 > 0 && converterClass == null; --i2) {
            converterName = converterName.substring(0, i2);
            if (converterClass != null || rules == null) continue;
            converterClass = rules.get(converterName);
        }
        if (converterClass == null) {
            LOGGER.error("Unrecognized format specifier [" + converterId + "]");
            return null;
        }
        Method[] methods = converterClass.getDeclaredMethods();
        Method newInstanceMethod = null;
        for (Method method : methods) {
            if (!Modifier.isStatic(method.getModifiers()) || !method.getDeclaringClass().equals(converterClass) || !method.getName().equals("newInstance")) continue;
            if (newInstanceMethod == null) {
                newInstanceMethod = method;
                continue;
            }
            if (!method.getReturnType().equals(newInstanceMethod.getReturnType())) continue;
            LOGGER.error("Class " + converterClass + " cannot contain multiple static newInstance methods");
            return null;
        }
        if (newInstanceMethod == null) {
            LOGGER.error("Class " + converterClass + " does not contain a static newInstance method");
            return null;
        }
        Class<?>[] parmTypes = newInstanceMethod.getParameterTypes();
        Object[] objectArray = parms = parmTypes.length > 0 ? new Object[parmTypes.length] : null;
        if (parms != null) {
            int i3 = 0;
            boolean errors = false;
            for (Class<Configuration> clazz : parmTypes) {
                if (clazz.isArray() && clazz.getName().equals("[Ljava.lang.String;")) {
                    String[] optionsArray;
                    parms[i3] = optionsArray = options.toArray(new String[options.size()]);
                } else if (clazz.isAssignableFrom(Configuration.class)) {
                    parms[i3] = this.config;
                } else {
                    LOGGER.error("Unknown parameter type " + clazz.getName() + " for static newInstance method of " + converterClass.getName());
                    errors = true;
                }
                ++i3;
            }
            if (errors) {
                return null;
            }
        }
        try {
            Object newObj = newInstanceMethod.invoke(null, parms);
            if (newObj instanceof PatternConverter) {
                currentLiteral.delete(0, currentLiteral.length() - (converterId.length() - converterName.length()));
                return (PatternConverter)newObj;
            }
            LOGGER.warn("Class " + converterClass.getName() + " does not extend PatternConverter.");
        }
        catch (Exception ex2) {
            LOGGER.error("Error creating converter for " + converterId, (Throwable)ex2);
        }
        return null;
    }

    private int finalizeConverter(char c2, String pattern, int i2, StringBuilder currentLiteral, FormattingInfo formattingInfo, Map<String, Class<PatternConverter>> rules, List<PatternConverter> patternConverters, List<FormattingInfo> formattingInfos) {
        StringBuilder convBuf = new StringBuilder();
        i2 = PatternParser.extractConverter(c2, pattern, i2, convBuf, currentLiteral);
        String converterId = convBuf.toString();
        ArrayList<String> options = new ArrayList<String>();
        i2 = PatternParser.extractOptions(pattern, i2, options);
        PatternConverter pc2 = this.createConverter(converterId, currentLiteral, rules, options);
        if (pc2 == null) {
            StringBuilder msg;
            if (Strings.isEmpty(converterId)) {
                msg = new StringBuilder("Empty conversion specifier starting at position ");
            } else {
                msg = new StringBuilder("Unrecognized conversion specifier [");
                msg.append(converterId);
                msg.append("] starting at position ");
            }
            msg.append(Integer.toString(i2));
            msg.append(" in conversion pattern.");
            LOGGER.error(msg.toString());
            patternConverters.add(new LiteralPatternConverter(this.config, currentLiteral.toString()));
            formattingInfos.add(FormattingInfo.getDefault());
        } else {
            patternConverters.add(pc2);
            formattingInfos.add(formattingInfo);
            if (currentLiteral.length() > 0) {
                patternConverters.add(new LiteralPatternConverter(this.config, currentLiteral.toString()));
                formattingInfos.add(FormattingInfo.getDefault());
            }
        }
        currentLiteral.setLength(0);
        return i2;
    }

    private static enum ParserState {
        LITERAL_STATE,
        CONVERTER_STATE,
        DOT_STATE,
        MIN_STATE,
        MAX_STATE;

    }
}

