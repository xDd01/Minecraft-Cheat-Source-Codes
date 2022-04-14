package org.newdawn.slick;

import org.newdawn.slick.util.*;
import java.io.*;
import java.util.*;
import org.newdawn.slick.opengl.renderer.*;

public class AngelCodeFont implements Font
{
    private static SGL GL;
    private static final int DISPLAY_LIST_CACHE_SIZE = 200;
    private static final int MAX_CHAR = 255;
    private boolean displayListCaching;
    private Image fontImage;
    private Glyph[] chars;
    private int lineHeight;
    private int baseDisplayListID;
    private int eldestDisplayListID;
    private DisplayList eldestDisplayList;
    private boolean singleCase;
    private short ascent;
    private short descent;
    private short leading;
    private final LinkedHashMap displayLists;
    
    public AngelCodeFont(final String fntFile, final Image image) throws SlickException {
        this.displayListCaching = true;
        this.baseDisplayListID = -1;
        this.singleCase = false;
        this.displayLists = new LinkedHashMap(200, 1.0f, true) {
            @Override
            protected boolean removeEldestEntry(final Map.Entry eldest) {
                AngelCodeFont.this.eldestDisplayList = eldest.getValue();
                AngelCodeFont.this.eldestDisplayListID = AngelCodeFont.this.eldestDisplayList.id;
                return false;
            }
        };
        this.fontImage = image;
        this.parseFnt(ResourceLoader.getResourceAsStream(fntFile));
    }
    
    public AngelCodeFont(final String fntFile, final String imgFile) throws SlickException {
        this.displayListCaching = true;
        this.baseDisplayListID = -1;
        this.singleCase = false;
        this.displayLists = new LinkedHashMap(200, 1.0f, true) {
            @Override
            protected boolean removeEldestEntry(final Map.Entry eldest) {
                AngelCodeFont.this.eldestDisplayList = eldest.getValue();
                AngelCodeFont.this.eldestDisplayListID = AngelCodeFont.this.eldestDisplayList.id;
                return false;
            }
        };
        this.fontImage = new Image(imgFile);
        this.parseFnt(ResourceLoader.getResourceAsStream(fntFile));
    }
    
    public AngelCodeFont(final String fntFile, final Image image, final boolean caching) throws SlickException {
        this.displayListCaching = true;
        this.baseDisplayListID = -1;
        this.singleCase = false;
        this.displayLists = new LinkedHashMap(200, 1.0f, true) {
            @Override
            protected boolean removeEldestEntry(final Map.Entry eldest) {
                AngelCodeFont.this.eldestDisplayList = eldest.getValue();
                AngelCodeFont.this.eldestDisplayListID = AngelCodeFont.this.eldestDisplayList.id;
                return false;
            }
        };
        this.fontImage = image;
        this.displayListCaching = caching;
        this.parseFnt(ResourceLoader.getResourceAsStream(fntFile));
    }
    
    public AngelCodeFont(final String fntFile, final String imgFile, final boolean caching) throws SlickException {
        this.displayListCaching = true;
        this.baseDisplayListID = -1;
        this.singleCase = false;
        this.displayLists = new LinkedHashMap(200, 1.0f, true) {
            @Override
            protected boolean removeEldestEntry(final Map.Entry eldest) {
                AngelCodeFont.this.eldestDisplayList = eldest.getValue();
                AngelCodeFont.this.eldestDisplayListID = AngelCodeFont.this.eldestDisplayList.id;
                return false;
            }
        };
        this.fontImage = new Image(imgFile);
        this.displayListCaching = caching;
        this.parseFnt(ResourceLoader.getResourceAsStream(fntFile));
    }
    
    public AngelCodeFont(final String name, final InputStream fntFile, final InputStream imgFile) throws SlickException {
        this.displayListCaching = true;
        this.baseDisplayListID = -1;
        this.singleCase = false;
        this.displayLists = new LinkedHashMap(200, 1.0f, true) {
            @Override
            protected boolean removeEldestEntry(final Map.Entry eldest) {
                AngelCodeFont.this.eldestDisplayList = eldest.getValue();
                AngelCodeFont.this.eldestDisplayListID = AngelCodeFont.this.eldestDisplayList.id;
                return false;
            }
        };
        this.fontImage = new Image(imgFile, name, false);
        this.parseFnt(fntFile);
    }
    
    public AngelCodeFont(final String name, final InputStream fntFile, final InputStream imgFile, final boolean caching) throws SlickException {
        this.displayListCaching = true;
        this.baseDisplayListID = -1;
        this.singleCase = false;
        this.displayLists = new LinkedHashMap(200, 1.0f, true) {
            @Override
            protected boolean removeEldestEntry(final Map.Entry eldest) {
                AngelCodeFont.this.eldestDisplayList = eldest.getValue();
                AngelCodeFont.this.eldestDisplayListID = AngelCodeFont.this.eldestDisplayList.id;
                return false;
            }
        };
        this.fontImage = new Image(imgFile, name, false);
        this.displayListCaching = caching;
        this.parseFnt(fntFile);
    }
    
    private void parseFnt(final InputStream fntFile) throws SlickException {
        if (this.displayListCaching) {
            this.baseDisplayListID = AngelCodeFont.GL.glGenLists(200);
            if (this.baseDisplayListID == 0) {
                this.displayListCaching = false;
            }
        }
        try {
            final BufferedReader in = new BufferedReader(new InputStreamReader(fntFile));
            final String info = in.readLine();
            final String common = in.readLine();
            this.ascent = this.parseMetric(common, "base=");
            this.descent = this.parseMetric(common, "descent=");
            this.leading = this.parseMetric(common, "leading=");
            final String page = in.readLine();
            final Map<Short, List<Short>> kerning = new HashMap<Short, List<Short>>(64);
            final List<Glyph> charDefs = new ArrayList<Glyph>(255);
            int maxChar = 0;
            boolean done = false;
            while (!done) {
                final String line = in.readLine();
                if (line == null) {
                    done = true;
                }
                else {
                    if (!line.startsWith("chars c")) {
                        if (line.startsWith("char")) {
                            final Glyph def = this.parseChar(line);
                            if (def != null) {
                                maxChar = Math.max(maxChar, def.id);
                                charDefs.add(def);
                            }
                        }
                    }
                    if (line.startsWith("kernings c")) {
                        continue;
                    }
                    if (!line.startsWith("kerning")) {
                        continue;
                    }
                    final StringTokenizer tokens = new StringTokenizer(line, " =");
                    tokens.nextToken();
                    tokens.nextToken();
                    final short first = Short.parseShort(tokens.nextToken());
                    tokens.nextToken();
                    final int second = Integer.parseInt(tokens.nextToken());
                    tokens.nextToken();
                    final int offset = Integer.parseInt(tokens.nextToken());
                    List<Short> values = kerning.get(first);
                    if (values == null) {
                        values = new ArrayList<Short>();
                        kerning.put(first, values);
                    }
                    values.add((short)(offset << 8 | second));
                }
            }
            this.chars = new Glyph[maxChar + 1];
            Iterator i$ = charDefs.iterator();
            while (i$.hasNext()) {
                final Glyph def = i$.next();
                this.chars[def.id] = def;
            }
            i$ = kerning.entrySet().iterator();
            while (i$.hasNext()) {
                final Map.Entry<Short, List<Short>> entry = i$.next();
                final short first = entry.getKey();
                final List<Short> valueList = entry.getValue();
                final short[] valueArray = new short[valueList.size()];
                for (int i = 0; i < valueList.size(); ++i) {
                    valueArray[i] = valueList.get(i);
                }
                this.chars[first].kerning = valueArray;
            }
        }
        catch (IOException e) {
            Log.error(e);
            throw new SlickException("Failed to parse font file: " + fntFile);
        }
    }
    
    public Image getImage() {
        return this.fontImage;
    }
    
    public void setSingleCase(final boolean enabled) {
        this.singleCase = enabled;
    }
    
    public boolean isSingleCase() {
        return this.singleCase;
    }
    
    private short parseMetric(final String str, final String sub) {
        int ind = str.indexOf(sub);
        if (ind != -1) {
            final String subStr = str.substring(ind + sub.length());
            ind = subStr.indexOf(32);
            return Short.parseShort(subStr.substring(0, (ind != -1) ? ind : subStr.length()));
        }
        return -1;
    }
    
    private Glyph parseChar(final String line) throws SlickException {
        final StringTokenizer tokens = new StringTokenizer(line, " =");
        tokens.nextToken();
        tokens.nextToken();
        final short id = Short.parseShort(tokens.nextToken());
        if (id < 0) {
            return null;
        }
        if (id > 255) {
            throw new SlickException("Invalid character '" + id + "': SpriteFont does not support characters above " + 255);
        }
        tokens.nextToken();
        final short x = Short.parseShort(tokens.nextToken());
        tokens.nextToken();
        final short y = Short.parseShort(tokens.nextToken());
        tokens.nextToken();
        final short width = Short.parseShort(tokens.nextToken());
        tokens.nextToken();
        final short height = Short.parseShort(tokens.nextToken());
        tokens.nextToken();
        final short xoffset = Short.parseShort(tokens.nextToken());
        tokens.nextToken();
        final short yoffset = Short.parseShort(tokens.nextToken());
        tokens.nextToken();
        final short xadvance = Short.parseShort(tokens.nextToken());
        if (id != 32) {
            this.lineHeight = Math.max(height + yoffset, this.lineHeight);
        }
        final Image img = this.fontImage.getSubImage(x, y, width, height);
        return new Glyph(id, x, y, width, height, xoffset, yoffset, xadvance, img);
    }
    
    @Override
    public void drawString(final float x, final float y, final CharSequence text) {
        this.drawString(x, y, text, Color.white);
    }
    
    @Override
    public void drawString(final float x, final float y, final CharSequence text, final Color col) {
        this.drawString(x, y, text, col, 0, text.length() - 1);
    }
    
    @Override
    public void drawString(final float x, final float y, final CharSequence text, final Color col, final int startIndex, final int endIndex) {
        this.fontImage.bind();
        col.bind();
        AngelCodeFont.GL.glTranslatef(x, y, 0.0f);
        if (this.displayListCaching && startIndex == 0 && endIndex == text.length() - 1) {
            DisplayList displayList = this.displayLists.get(text);
            if (displayList != null) {
                AngelCodeFont.GL.glCallList(displayList.id);
            }
            else {
                displayList = new DisplayList();
                displayList.text = text;
                final int displayListCount = this.displayLists.size();
                if (displayListCount < 200) {
                    displayList.id = this.baseDisplayListID + displayListCount;
                }
                else {
                    displayList.id = this.eldestDisplayListID;
                    this.displayLists.remove(this.eldestDisplayList.text);
                }
                this.displayLists.put(text, displayList);
                AngelCodeFont.GL.glNewList(displayList.id, 4865);
                this.render(text, startIndex, endIndex);
                AngelCodeFont.GL.glEndList();
            }
        }
        else {
            this.render(text, startIndex, endIndex);
        }
        AngelCodeFont.GL.glTranslatef(-x, -y, 0.0f);
    }
    
    private void render(final CharSequence text, final int start, final int end) {
        AngelCodeFont.GL.glBegin(7);
        int x = 0;
        int y = 0;
        Glyph lastCharDef = null;
        for (int i = 0; i < text.length(); ++i) {
            final char id = text.charAt(i);
            if (id == '\n') {
                x = 0;
                y += this.getLineHeight();
            }
            else {
                final Glyph charDef = this.getGlyph(id);
                if (charDef != null) {
                    if (lastCharDef != null) {
                        x += lastCharDef.getKerning(id);
                    }
                    else {
                        x -= charDef.xoffset;
                    }
                    lastCharDef = charDef;
                    if (i >= start && i <= end) {
                        charDef.image.drawEmbedded((float)(x + charDef.xoffset), (float)(y + charDef.yoffset), charDef.width, charDef.height);
                    }
                    x += charDef.xadvance;
                }
            }
        }
        AngelCodeFont.GL.glEnd();
    }
    
    public int getYOffset(final String text) {
        DisplayList displayList = null;
        if (this.displayListCaching) {
            displayList = this.displayLists.get(text);
            if (displayList != null && displayList.yOffset != null) {
                return displayList.yOffset;
            }
        }
        int stopIndex = text.indexOf(10);
        if (stopIndex == -1) {
            stopIndex = text.length();
        }
        int minYOffset = 10000;
        for (int i = 0; i < stopIndex; ++i) {
            final Glyph charDef = this.getGlyph(text.charAt(i));
            if (charDef != null) {
                minYOffset = Math.min(charDef.yoffset, minYOffset);
            }
        }
        if (displayList != null) {
            displayList.yOffset = new Short((short)minYOffset);
        }
        return minYOffset;
    }
    
    @Override
    public int getHeight(final CharSequence text) {
        DisplayList displayList = null;
        if (this.displayListCaching) {
            displayList = this.displayLists.get(text);
            if (displayList != null && displayList.height != null) {
                return displayList.height;
            }
        }
        int lines = 0;
        int maxHeight = 0;
        for (int i = 0; i < text.length(); ++i) {
            final char id = text.charAt(i);
            if (id == '\n') {
                ++lines;
                maxHeight = 0;
            }
            else if (id != ' ') {
                final Glyph charDef = this.getGlyph(id);
                if (charDef != null) {
                    maxHeight = Math.max(charDef.height + charDef.yoffset, maxHeight);
                }
            }
        }
        maxHeight += lines * this.getLineHeight();
        if (displayList != null) {
            displayList.height = new Short((short)maxHeight);
        }
        return maxHeight;
    }
    
    @Override
    public int getWidth(final CharSequence text) {
        DisplayList displayList = null;
        if (this.displayListCaching) {
            displayList = this.displayLists.get(text);
            if (displayList != null && displayList.width != null) {
                return displayList.width;
            }
        }
        int maxWidth = 0;
        int width = 0;
        Glyph lastCharDef = null;
        for (int i = 0, n = text.length(); i < n; ++i) {
            final char id = text.charAt(i);
            if (id == '\n') {
                width = 0;
            }
            else {
                final Glyph charDef = this.getGlyph(id);
                if (charDef != null) {
                    if (lastCharDef != null) {
                        width += lastCharDef.getKerning(id);
                    }
                    lastCharDef = charDef;
                    if (i < n - 1 || charDef.width == 0) {
                        width += charDef.xadvance;
                    }
                    else {
                        width += charDef.width + charDef.xoffset;
                    }
                    maxWidth = Math.max(maxWidth, width);
                }
            }
        }
        if (displayList != null) {
            displayList.width = new Short((short)maxWidth);
        }
        return maxWidth;
    }
    
    @Override
    public int getLineHeight() {
        return this.lineHeight;
    }
    
    public int getDescent() {
        return this.descent;
    }
    
    public int getAscent() {
        return this.ascent;
    }
    
    public Glyph getGlyph(char c) {
        final Glyph g = (c < '\0' || c >= this.chars.length) ? null : this.chars[c];
        if (g != null) {
            return g;
        }
        if (g == null && this.singleCase) {
            if (c >= 'A' && c <= 'Z') {
                c += ' ';
            }
            else if (c >= 'a' && c <= 'z') {
                c -= ' ';
            }
        }
        return (c < '\0' || c >= this.chars.length) ? null : this.chars[c];
    }
    
    static {
        AngelCodeFont.GL = Renderer.get();
    }
    
    public static class Glyph
    {
        public final short id;
        public final short x;
        public final short y;
        public final short width;
        public final short height;
        public final short xoffset;
        public final short yoffset;
        public final short xadvance;
        public final Image image;
        protected short dlIndex;
        protected short[] kerning;
        
        protected Glyph(final short id, final short x, final short y, final short width, final short height, final short xoffset, final short yoffset, final short xadvance, final Image image) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.xoffset = xoffset;
            this.yoffset = yoffset;
            this.xadvance = xadvance;
            this.image = image;
        }
        
        @Override
        public String toString() {
            return "[CharDef id=" + this.id + " x=" + this.x + " y=" + this.y + "]";
        }
        
        public int getKerning(final int otherCodePoint) {
            if (this.kerning == null) {
                return 0;
            }
            int low = 0;
            int high = this.kerning.length - 1;
            while (low <= high) {
                final int midIndex = low + high >>> 1;
                final int value = this.kerning[midIndex];
                final int foundCodePoint = value & 0xFF;
                if (foundCodePoint < otherCodePoint) {
                    low = midIndex + 1;
                }
                else {
                    if (foundCodePoint <= otherCodePoint) {
                        return value >> 8;
                    }
                    high = midIndex - 1;
                }
            }
            return 0;
        }
    }
    
    private static class DisplayList
    {
        int id;
        Short yOffset;
        Short width;
        Short height;
        CharSequence text;
    }
}
