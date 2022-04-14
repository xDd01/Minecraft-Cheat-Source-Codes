package org.newdawn.slick.opengl.shader;

import java.util.*;
import java.nio.*;
import java.io.*;
import org.newdawn.slick.util.*;
import org.lwjgl.opengl.*;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;
import org.lwjgl.*;

public class ShaderProgram
{
    public static final int VERTEX_SHADER = 35633;
    public static final int FRAGMENT_SHADER = 35632;
    private static boolean strict;
    protected int program;
    protected String log;
    protected HashMap<String, Integer> uniforms;
    protected HashMap<String, Integer> attributes;
    protected String vertShaderSource;
    protected String fragShaderSource;
    protected int vert;
    protected int frag;
    private FloatBuffer buf4;
    private IntBuffer ibuf4;
    
    public static boolean isSupported() {
        final ContextCapabilities c = GLContext.getCapabilities();
        return c.GL_ARB_shader_objects && c.GL_ARB_vertex_shader && c.GL_ARB_fragment_shader;
    }
    
    public static void setStrictMode(final boolean enabled) {
        ShaderProgram.strict = enabled;
    }
    
    public static boolean isStrictMode() {
        return ShaderProgram.strict;
    }
    
    public static void unbindAll() {
        ARBShaderObjects.glUseProgramObjectARB(0);
    }
    
    public static ShaderProgram loadProgram(final String vertFile, final String fragFile) throws SlickException {
        return new ShaderProgram(readFile(vertFile), readFile(fragFile));
    }
    
    public static String readFile(final String ref) throws SlickException {
        final InputStream in = ResourceLoader.getResourceAsStream(ref);
        try {
            return readFile(in);
        }
        catch (SlickException e) {
            throw new SlickException("could not load source file: " + ref);
        }
    }
    
    public static String readFile(final InputStream in) throws SlickException {
        try {
            final StringBuffer sBuffer = new StringBuffer();
            final BufferedReader br = new BufferedReader(new InputStreamReader(in));
            final char[] buffer = new char[1024];
            int cnt;
            while ((cnt = br.read(buffer, 0, buffer.length)) > -1) {
                sBuffer.append(buffer, 0, cnt);
            }
            br.close();
            return sBuffer.toString();
        }
        catch (IOException e) {
            throw new SlickException("could not load source file");
        }
    }
    
    public ShaderProgram(final String vertexShaderSource, final String fragShaderSource) throws SlickException {
        this.log = "";
        this.uniforms = new HashMap<String, Integer>();
        this.attributes = new HashMap<String, Integer>();
        if (vertexShaderSource == null || fragShaderSource == null) {
            throw new IllegalArgumentException("shader source must be non-null");
        }
        if (!isSupported()) {
            throw new SlickException("no shader support found; driver does not support extension GL_ARB_shader_objects");
        }
        this.vertShaderSource = vertexShaderSource;
        this.fragShaderSource = fragShaderSource;
        this.vert = this.compileShader(35633, vertexShaderSource);
        this.frag = this.compileShader(35632, fragShaderSource);
        this.program = this.createProgram();
        try {
            this.linkProgram();
        }
        catch (SlickException e) {
            this.release();
            throw e;
        }
        if (this.log != null && this.log.length() != 0) {
            Log.warn("GLSL Info: " + this.log);
        }
    }
    
    protected ShaderProgram() {
        this.log = "";
        this.uniforms = new HashMap<String, Integer>();
        this.attributes = new HashMap<String, Integer>();
    }
    
    protected int createProgram() throws SlickException {
        if (!isSupported()) {
            throw new SlickException("no shader support found; driver does not support extension GL_ARB_shader_objects");
        }
        final int program = ARBShaderObjects.glCreateProgramObjectARB();
        if (program == 0) {
            throw new SlickException("could not create program; check ShaderProgram.isSupported()");
        }
        return program;
    }
    
    private String shaderTypeString(final int type) {
        if (type == 35632) {
            return "FRAGMENT_SHADER";
        }
        if (type == 35633) {
            return "VERTEX_SHADER";
        }
        if (type == 36313) {
            return "GEOMETRY_SHADER";
        }
        return "shader";
    }
    
    protected int compileShader(final int type, final String source) throws SlickException {
        final int shader = ARBShaderObjects.glCreateShaderObjectARB(type);
        if (shader == 0) {
            throw new SlickException("could not create shader object; check ShaderProgram.isSupported()");
        }
        ARBShaderObjects.glShaderSourceARB(shader, source);
        ARBShaderObjects.glCompileShaderARB(shader);
        final int comp = ARBShaderObjects.glGetObjectParameteriARB(shader, 35713);
        final int len = ARBShaderObjects.glGetObjectParameteriARB(shader, 35716);
        final String t = this.shaderTypeString(type);
        final String err = ARBShaderObjects.glGetInfoLogARB(shader, len);
        if (err != null && err.length() != 0) {
            this.log = this.log + t + " compile log:\n" + err + "\n";
        }
        if (comp == 0) {
            throw new SlickException(this.log);
        }
        return shader;
    }
    
    protected void attachShaders() {
        ARBShaderObjects.glAttachObjectARB(this.getID(), this.vert);
        ARBShaderObjects.glAttachObjectARB(this.getID(), this.frag);
    }
    
    protected void linkProgram() throws SlickException {
        if (!this.valid()) {
            throw new SlickException("trying to link an invalid (i.e. released) program");
        }
        this.uniforms.clear();
        this.attributes.clear();
        this.attachShaders();
        ARBShaderObjects.glLinkProgramARB(this.program);
        final int comp = ARBShaderObjects.glGetObjectParameteriARB(this.program, 35714);
        final int len = ARBShaderObjects.glGetObjectParameteriARB(this.program, 35716);
        final String err = ARBShaderObjects.glGetInfoLogARB(this.program, len);
        if (err != null && err.length() != 0) {
            this.log = err + "\n" + this.log;
        }
        if (this.log != null) {
            this.log = this.log.trim();
        }
        if (comp == 0) {
            throw new SlickException(this.log);
        }
        this.fetchUniforms();
        this.fetchAttributes();
    }
    
    public String getLog() {
        return this.log;
    }
    
    public void bind() {
        if (!this.valid()) {
            throw new IllegalStateException("trying to enable a program that is not valid");
        }
        ARBShaderObjects.glUseProgramObjectARB(this.program);
    }
    
    public void unbind() {
        unbindAll();
    }
    
    public void releaseShaders() {
        this.unbind();
        if (this.vert != 0) {
            ARBShaderObjects.glDetachObjectARB(this.getID(), this.vert);
            ARBShaderObjects.glDeleteObjectARB(this.vert);
            this.vert = 0;
        }
        if (this.frag != 0) {
            ARBShaderObjects.glDetachObjectARB(this.getID(), this.frag);
            ARBShaderObjects.glDeleteObjectARB(this.frag);
            this.frag = 0;
        }
    }
    
    public void release() {
        if (this.program != 0) {
            this.unbind();
            this.releaseShaders();
            ARBShaderObjects.glDeleteObjectARB(this.program);
            this.program = 0;
        }
    }
    
    public int getVertexShaderID() {
        return this.vert;
    }
    
    public int getFragmentShaderID() {
        return this.frag;
    }
    
    public String getVertexShaderSource() {
        return this.vertShaderSource;
    }
    
    public String getFragmentShaderSource() {
        return this.fragShaderSource;
    }
    
    public int getID() {
        return this.program;
    }
    
    public boolean valid() {
        return this.program != 0;
    }
    
    private void fetchUniforms() {
        final int len = ARBShaderObjects.glGetObjectParameteriARB(this.program, 35718);
        final int strLen = ARBShaderObjects.glGetObjectParameteriARB(this.program, 35719);
        for (int i = 0; i < len; ++i) {
            final String name = ARBShaderObjects.glGetActiveUniformARB(this.program, i, strLen);
            final int id = ARBShaderObjects.glGetUniformLocationARB(this.program, name);
            this.uniforms.put(name, id);
        }
    }
    
    private void fetchAttributes() {
        final int len = ARBShaderObjects.glGetObjectParameteriARB(this.program, 35721);
        final int strLen = ARBShaderObjects.glGetObjectParameteriARB(this.program, 35722);
        for (int i = 0; i < len; ++i) {
            final String name = ARBVertexShader.glGetActiveAttribARB(this.program, i, strLen);
            final int id = ARBVertexShader.glGetAttribLocationARB(this.program, name);
            this.attributes.put(name, id);
        }
    }
    
    public int getUniformID(final String name) {
        final Integer locI = this.uniforms.get(name);
        int location = (locI == null) ? -1 : locI;
        if (location != -1) {
            return location;
        }
        location = ARBShaderObjects.glGetUniformLocationARB(this.program, name);
        if (location == -1 && ShaderProgram.strict) {
            throw new IllegalArgumentException("no active uniform by name '" + name + "' (disable strict compiling to suppress warnings)");
        }
        this.uniforms.put(name, location);
        return location;
    }
    
    public int getAttributeID(final String name) {
        int location = this.attributes.get(name);
        if (location != -1) {
            return location;
        }
        location = ARBVertexShader.glGetAttribLocationARB(this.program, name);
        if (location == -1 && ShaderProgram.strict) {
            throw new IllegalArgumentException("no active attribute by name '" + name + "'");
        }
        this.attributes.put(name, location);
        return location;
    }
    
    public String[] getAttributes() {
        return this.attributes.keySet().toArray(new String[this.attributes.size()]);
    }
    
    public String[] getUniformNames() {
        return this.uniforms.keySet().toArray(new String[this.uniforms.size()]);
    }
    
    public boolean enableVertexAttribute(final String name) {
        final int id = this.getAttributeID(name);
        if (id == -1) {
            return false;
        }
        ARBVertexShader.glEnableVertexAttribArrayARB(id);
        return true;
    }
    
    public boolean disableVertexAttribute(final String name) {
        final int id = this.getAttributeID(name);
        if (id == -1) {
            return false;
        }
        ARBVertexShader.glDisableVertexAttribArrayARB(id);
        return true;
    }
    
    public void setUniform4f(final String name, final Color color) {
        this.setUniform4f(name, color.r, color.g, color.b, color.a);
    }
    
    public void setUniform2f(final String name, final Vector2f vec) {
        this.setUniform2f(name, vec.x, vec.y);
    }
    
    private FloatBuffer uniformf(final String name) {
        if (this.buf4 == null) {
            this.buf4 = BufferUtils.createFloatBuffer(4);
        }
        this.buf4.clear();
        this.getUniform(name, this.buf4);
        return this.buf4;
    }
    
    private IntBuffer uniformi(final String name) {
        if (this.ibuf4 == null) {
            this.ibuf4 = BufferUtils.createIntBuffer(4);
        }
        this.ibuf4.clear();
        this.getUniform(name, this.ibuf4);
        return this.ibuf4;
    }
    
    public int getUniform1i(final String name) {
        return this.uniformi(name).get(0);
    }
    
    public int[] getUniform2i(final String name) {
        final IntBuffer buf = this.uniformi(name);
        return new int[] { buf.get(0), buf.get(1) };
    }
    
    public int[] getUniform3i(final String name) {
        final IntBuffer buf = this.uniformi(name);
        return new int[] { buf.get(0), buf.get(1), buf.get(2) };
    }
    
    public int[] getUniform4i(final String name) {
        final IntBuffer buf = this.uniformi(name);
        return new int[] { buf.get(0), buf.get(1), buf.get(2), buf.get(3) };
    }
    
    public float getUniform1f(final String name) {
        return this.uniformf(name).get(0);
    }
    
    public float[] getUniform2f(final String name) {
        final FloatBuffer buf = this.uniformf(name);
        return new float[] { buf.get(0), buf.get(1) };
    }
    
    public float[] getUniform3f(final String name) {
        final FloatBuffer buf = this.uniformf(name);
        return new float[] { buf.get(0), buf.get(1), buf.get(2) };
    }
    
    public float[] getUniform4f(final String name) {
        final FloatBuffer buf = this.uniformf(name);
        return new float[] { buf.get(0), buf.get(1), buf.get(2), buf.get(3) };
    }
    
    public boolean getUniform(final String name, final FloatBuffer buf) {
        final int id = this.getUniformID(name);
        if (id == -1) {
            return false;
        }
        ARBShaderObjects.glGetUniformARB(this.program, id, buf);
        return true;
    }
    
    public boolean getUniform(final String name, final IntBuffer buf) {
        final int id = this.getUniformID(name);
        if (id == -1) {
            return false;
        }
        ARBShaderObjects.glGetUniformARB(this.program, id, buf);
        return true;
    }
    
    public boolean hasUniform(final String name) {
        return this.uniforms.containsKey(name);
    }
    
    public boolean hasAttribute(final String name) {
        return this.attributes.containsKey(name);
    }
    
    public void setUniform1f(final String name, final float f) {
        final int id = this.getUniformID(name);
        if (id == -1) {
            return;
        }
        ARBShaderObjects.glUniform1fARB(id, f);
    }
    
    public void setUniform1i(final String name, final int i) {
        final int id = this.getUniformID(name);
        if (id == -1) {
            return;
        }
        ARBShaderObjects.glUniform1iARB(id, i);
    }
    
    public void setUniform2f(final String name, final float a, final float b) {
        final int id = this.getUniformID(name);
        if (id == -1) {
            return;
        }
        ARBShaderObjects.glUniform2fARB(id, a, b);
    }
    
    public void setUniform3f(final String name, final float a, final float b, final float c) {
        final int id = this.getUniformID(name);
        if (id == -1) {
            return;
        }
        ARBShaderObjects.glUniform3fARB(id, a, b, c);
    }
    
    public void setUniform4f(final String name, final float a, final float b, final float c, final float d) {
        final int id = this.getUniformID(name);
        if (id == -1) {
            return;
        }
        ARBShaderObjects.glUniform4fARB(id, a, b, c, d);
    }
    
    public void setUniform2i(final String name, final int a, final int b) {
        final int id = this.getUniformID(name);
        if (id == -1) {
            return;
        }
        ARBShaderObjects.glUniform2iARB(id, a, b);
    }
    
    public void setUniform3i(final String name, final int a, final int b, final int c) {
        final int id = this.getUniformID(name);
        if (id == -1) {
            return;
        }
        ARBShaderObjects.glUniform3iARB(id, a, b, c);
    }
    
    public void setUniform4i(final String name, final int a, final int b, final int c, final int d) {
        final int id = this.getUniformID(name);
        if (id == -1) {
            return;
        }
        ARBShaderObjects.glUniform4iARB(id, a, b, c, d);
    }
    
    public void setMatrix2(final String name, final boolean transpose, final FloatBuffer buf) {
        final int id = this.getUniformID(name);
        if (id == -1) {
            return;
        }
        ARBShaderObjects.glUniformMatrix2ARB(id, transpose, buf);
    }
    
    public void setMatrix3(final String name, final boolean transpose, final FloatBuffer buf) {
        final int id = this.getUniformID(name);
        if (id == -1) {
            return;
        }
        ARBShaderObjects.glUniformMatrix3ARB(id, transpose, buf);
    }
    
    public void setMatrix4(final String name, final boolean transpose, final FloatBuffer buf) {
        final int id = this.getUniformID(name);
        if (id == -1) {
            return;
        }
        ARBShaderObjects.glUniformMatrix4ARB(id, transpose, buf);
    }
    
    static {
        ShaderProgram.strict = true;
    }
}
