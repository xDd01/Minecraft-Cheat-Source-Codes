package net.minecraft.nbt;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.Stack;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonToNBT
{
    private static final Logger logger = LogManager.getLogger();
    private static final Pattern field_179273_b = Pattern.compile("\\[[-+\\d|,\\s]+\\]");

    public static NBTTagCompound getTagFromJson(String jsonString) throws NBTException
    {
        jsonString = jsonString.trim();

        if (!jsonString.startsWith("{"))
        {
            throw new NBTException("Invalid tag encountered, expected \'{\' as first char.");
        }
        else if (func_150310_b(jsonString) != 1)
        {
            throw new NBTException("Encountered multiple top tags, only one expected");
        }
        else
        {
            return (NBTTagCompound)func_150316_a("tag", jsonString).parse();
        }
    }

    static int func_150310_b(String p_150310_0_) throws NBTException
    {
        int var1 = 0;
        boolean var2 = false;
        Stack var3 = new Stack();

        for (int var4 = 0; var4 < p_150310_0_.length(); ++var4)
        {
            char var5 = p_150310_0_.charAt(var4);

            if (var5 == 34)
            {
                if (func_179271_b(p_150310_0_, var4))
                {
                    if (!var2)
                    {
                        throw new NBTException("Illegal use of \\\": " + p_150310_0_);
                    }
                }
                else
                {
                    var2 = !var2;
                }
            }
            else if (!var2)
            {
                if (var5 != 123 && var5 != 91)
                {
                    if (var5 == 125 && (var3.isEmpty() || ((Character)var3.pop()).charValue() != 123))
                    {
                        throw new NBTException("Unbalanced curly brackets {}: " + p_150310_0_);
                    }

                    if (var5 == 93 && (var3.isEmpty() || ((Character)var3.pop()).charValue() != 91))
                    {
                        throw new NBTException("Unbalanced square brackets []: " + p_150310_0_);
                    }
                }
                else
                {
                    if (var3.isEmpty())
                    {
                        ++var1;
                    }

                    var3.push(Character.valueOf(var5));
                }
            }
        }

        if (var2)
        {
            throw new NBTException("Unbalanced quotation: " + p_150310_0_);
        }
        else if (!var3.isEmpty())
        {
            throw new NBTException("Unbalanced brackets: " + p_150310_0_);
        }
        else
        {
            if (var1 == 0 && !p_150310_0_.isEmpty())
            {
                var1 = 1;
            }

            return var1;
        }
    }

    static JsonToNBT.Any func_179272_a(String ... p_179272_0_) throws NBTException
    {
        return func_150316_a(p_179272_0_[0], p_179272_0_[1]);
    }

    static JsonToNBT.Any func_150316_a(String p_150316_0_, String p_150316_1_) throws NBTException
    {
        p_150316_1_ = p_150316_1_.trim();
        String var3;
        boolean var4;
        char var6;

        if (p_150316_1_.startsWith("{"))
        {
            p_150316_1_ = p_150316_1_.substring(1, p_150316_1_.length() - 1);
            JsonToNBT.Compound var5;

            for (var5 = new JsonToNBT.Compound(p_150316_0_); p_150316_1_.length() > 0; p_150316_1_ = p_150316_1_.substring(var3.length() + 1))
            {
                var3 = func_150314_a(p_150316_1_, true);

                if (var3.length() > 0)
                {
                    var4 = false;
                    var5.field_150491_b.add(func_179270_a(var3, var4));
                }

                if (p_150316_1_.length() < var3.length() + 1)
                {
                    break;
                }

                var6 = p_150316_1_.charAt(var3.length());

                if (var6 != 44 && var6 != 123 && var6 != 125 && var6 != 91 && var6 != 93)
                {
                    throw new NBTException("Unexpected token \'" + var6 + "\' at: " + p_150316_1_.substring(var3.length()));
                }
            }

            return var5;
        }
        else if (p_150316_1_.startsWith("[") && !field_179273_b.matcher(p_150316_1_).matches())
        {
            p_150316_1_ = p_150316_1_.substring(1, p_150316_1_.length() - 1);
            JsonToNBT.List var2;

            for (var2 = new JsonToNBT.List(p_150316_0_); p_150316_1_.length() > 0; p_150316_1_ = p_150316_1_.substring(var3.length() + 1))
            {
                var3 = func_150314_a(p_150316_1_, false);

                if (var3.length() > 0)
                {
                    var4 = true;
                    var2.field_150492_b.add(func_179270_a(var3, var4));
                }

                if (p_150316_1_.length() < var3.length() + 1)
                {
                    break;
                }

                var6 = p_150316_1_.charAt(var3.length());

                if (var6 != 44 && var6 != 123 && var6 != 125 && var6 != 91 && var6 != 93)
                {
                    throw new NBTException("Unexpected token \'" + var6 + "\' at: " + p_150316_1_.substring(var3.length()));
                }
            }

            return var2;
        }
        else
        {
            return new JsonToNBT.Primitive(p_150316_0_, p_150316_1_);
        }
    }

    private static JsonToNBT.Any func_179270_a(String p_179270_0_, boolean p_179270_1_) throws NBTException
    {
        String var2 = func_150313_b(p_179270_0_, p_179270_1_);
        String var3 = func_150311_c(p_179270_0_, p_179270_1_);
        return func_179272_a(new String[] {var2, var3});
    }

    private static String func_150314_a(String p_150314_0_, boolean p_150314_1_) throws NBTException
    {
        int var2 = func_150312_a(p_150314_0_, ':');
        int var3 = func_150312_a(p_150314_0_, ',');

        if (p_150314_1_)
        {
            if (var2 == -1)
            {
                throw new NBTException("Unable to locate name/value separator for string: " + p_150314_0_);
            }

            if (var3 != -1 && var3 < var2)
            {
                throw new NBTException("Name error at: " + p_150314_0_);
            }
        }
        else if (var2 == -1 || var2 > var3)
        {
            var2 = -1;
        }

        return func_179269_a(p_150314_0_, var2);
    }

    private static String func_179269_a(String p_179269_0_, int p_179269_1_) throws NBTException
    {
        Stack var2 = new Stack();
        int var3 = p_179269_1_ + 1;
        boolean var4 = false;
        boolean var5 = false;
        boolean var6 = false;

        for (int var7 = 0; var3 < p_179269_0_.length(); ++var3)
        {
            char var8 = p_179269_0_.charAt(var3);

            if (var8 == 34)
            {
                if (func_179271_b(p_179269_0_, var3))
                {
                    if (!var4)
                    {
                        throw new NBTException("Illegal use of \\\": " + p_179269_0_);
                    }
                }
                else
                {
                    var4 = !var4;

                    if (var4 && !var6)
                    {
                        var5 = true;
                    }

                    if (!var4)
                    {
                        var7 = var3;
                    }
                }
            }
            else if (!var4)
            {
                if (var8 != 123 && var8 != 91)
                {
                    if (var8 == 125 && (var2.isEmpty() || ((Character)var2.pop()).charValue() != 123))
                    {
                        throw new NBTException("Unbalanced curly brackets {}: " + p_179269_0_);
                    }

                    if (var8 == 93 && (var2.isEmpty() || ((Character)var2.pop()).charValue() != 91))
                    {
                        throw new NBTException("Unbalanced square brackets []: " + p_179269_0_);
                    }

                    if (var8 == 44 && var2.isEmpty())
                    {
                        return p_179269_0_.substring(0, var3);
                    }
                }
                else
                {
                    var2.push(Character.valueOf(var8));
                }
            }

            if (!Character.isWhitespace(var8))
            {
                if (!var4 && var5 && var7 != var3)
                {
                    return p_179269_0_.substring(0, var7 + 1);
                }

                var6 = true;
            }
        }

        return p_179269_0_.substring(0, var3);
    }

    private static String func_150313_b(String p_150313_0_, boolean p_150313_1_) throws NBTException
    {
        if (p_150313_1_)
        {
            p_150313_0_ = p_150313_0_.trim();

            if (p_150313_0_.startsWith("{") || p_150313_0_.startsWith("["))
            {
                return "";
            }
        }

        int var2 = func_150312_a(p_150313_0_, ':');

        if (var2 == -1)
        {
            if (p_150313_1_)
            {
                return "";
            }
            else
            {
                throw new NBTException("Unable to locate name/value separator for string: " + p_150313_0_);
            }
        }
        else
        {
            return p_150313_0_.substring(0, var2).trim();
        }
    }

    private static String func_150311_c(String p_150311_0_, boolean p_150311_1_) throws NBTException
    {
        if (p_150311_1_)
        {
            p_150311_0_ = p_150311_0_.trim();

            if (p_150311_0_.startsWith("{") || p_150311_0_.startsWith("["))
            {
                return p_150311_0_;
            }
        }

        int var2 = func_150312_a(p_150311_0_, ':');

        if (var2 == -1)
        {
            if (p_150311_1_)
            {
                return p_150311_0_;
            }
            else
            {
                throw new NBTException("Unable to locate name/value separator for string: " + p_150311_0_);
            }
        }
        else
        {
            return p_150311_0_.substring(var2 + 1).trim();
        }
    }

    private static int func_150312_a(String p_150312_0_, char p_150312_1_)
    {
        int var2 = 0;

        for (boolean var3 = true; var2 < p_150312_0_.length(); ++var2)
        {
            char var4 = p_150312_0_.charAt(var2);

            if (var4 == 34)
            {
                if (!func_179271_b(p_150312_0_, var2))
                {
                    var3 = !var3;
                }
            }
            else if (var3)
            {
                if (var4 == p_150312_1_)
                {
                    return var2;
                }

                if (var4 == 123 || var4 == 91)
                {
                    return -1;
                }
            }
        }

        return -1;
    }

    private static boolean func_179271_b(String p_179271_0_, int p_179271_1_)
    {
        return p_179271_1_ > 0 && p_179271_0_.charAt(p_179271_1_ - 1) == 92 && !func_179271_b(p_179271_0_, p_179271_1_ - 1);
    }

    abstract static class Any
    {
        protected String json;

        public abstract NBTBase parse();
    }

    static class Compound extends JsonToNBT.Any
    {
        protected java.util.List field_150491_b = Lists.newArrayList();

        public Compound(String p_i45137_1_)
        {
            this.json = p_i45137_1_;
        }

        public NBTBase parse()
        {
            NBTTagCompound var1 = new NBTTagCompound();
            Iterator var2 = this.field_150491_b.iterator();

            while (var2.hasNext())
            {
                JsonToNBT.Any var3 = (JsonToNBT.Any)var2.next();
                var1.setTag(var3.json, var3.parse());
            }

            return var1;
        }
    }

    static class List extends JsonToNBT.Any
    {
        protected java.util.List field_150492_b = Lists.newArrayList();

        public List(String json)
        {
            this.json = json;
        }

        public NBTBase parse()
        {
            NBTTagList var1 = new NBTTagList();
            Iterator var2 = this.field_150492_b.iterator();

            while (var2.hasNext())
            {
                JsonToNBT.Any var3 = (JsonToNBT.Any)var2.next();
                var1.appendTag(var3.parse());
            }

            return var1;
        }
    }

    static class Primitive extends JsonToNBT.Any
    {
        private static final Pattern DOUBLE = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[d|D]");
        private static final Pattern FLOAT = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[f|F]");
        private static final Pattern BYTE = Pattern.compile("[-+]?[0-9]+[b|B]");
        private static final Pattern LONG = Pattern.compile("[-+]?[0-9]+[l|L]");
        private static final Pattern SHORT = Pattern.compile("[-+]?[0-9]+[s|S]");
        private static final Pattern INTEGER = Pattern.compile("[-+]?[0-9]+");
        private static final Pattern DOUBLE_UNTYPED = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
        private static final Splitter SPLITTER = Splitter.on(',').omitEmptyStrings();
        protected String jsonValue;

        public Primitive(String p_i45139_1_, String p_i45139_2_)
        {
            this.json = p_i45139_1_;
            this.jsonValue = p_i45139_2_;
        }

        public NBTBase parse()
        {
            try
            {
                if (DOUBLE.matcher(this.jsonValue).matches())
                {
                    return new NBTTagDouble(Double.parseDouble(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
                }

                if (FLOAT.matcher(this.jsonValue).matches())
                {
                    return new NBTTagFloat(Float.parseFloat(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
                }

                if (BYTE.matcher(this.jsonValue).matches())
                {
                    return new NBTTagByte(Byte.parseByte(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
                }

                if (LONG.matcher(this.jsonValue).matches())
                {
                    return new NBTTagLong(Long.parseLong(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
                }

                if (SHORT.matcher(this.jsonValue).matches())
                {
                    return new NBTTagShort(Short.parseShort(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
                }

                if (INTEGER.matcher(this.jsonValue).matches())
                {
                    return new NBTTagInt(Integer.parseInt(this.jsonValue));
                }

                if (DOUBLE_UNTYPED.matcher(this.jsonValue).matches())
                {
                    return new NBTTagDouble(Double.parseDouble(this.jsonValue));
                }

                if (this.jsonValue.equalsIgnoreCase("true") || this.jsonValue.equalsIgnoreCase("false"))
                {
                    return new NBTTagByte((byte)(Boolean.parseBoolean(this.jsonValue) ? 1 : 0));
                }
            }
            catch (NumberFormatException var6)
            {
                this.jsonValue = this.jsonValue.replaceAll("\\\\\"", "\"");
                return new NBTTagString(this.jsonValue);
            }

            if (this.jsonValue.startsWith("[") && this.jsonValue.endsWith("]"))
            {
                String var7 = this.jsonValue.substring(1, this.jsonValue.length() - 1);
                String[] var8 = (String[])Iterables.toArray(SPLITTER.split(var7), String.class);

                try
                {
                    int[] var3 = new int[var8.length];

                    for (int var4 = 0; var4 < var8.length; ++var4)
                    {
                        var3[var4] = Integer.parseInt(var8[var4].trim());
                    }

                    return new NBTTagIntArray(var3);
                }
                catch (NumberFormatException var5)
                {
                    return new NBTTagString(this.jsonValue);
                }
            }
            else
            {
                if (this.jsonValue.startsWith("\"") && this.jsonValue.endsWith("\""))
                {
                    this.jsonValue = this.jsonValue.substring(1, this.jsonValue.length() - 1);
                }

                this.jsonValue = this.jsonValue.replaceAll("\\\\\"", "\"");
                StringBuilder var1 = new StringBuilder();

                for (int var2 = 0; var2 < this.jsonValue.length(); ++var2)
                {
                    if (var2 < this.jsonValue.length() - 1 && this.jsonValue.charAt(var2) == 92 && this.jsonValue.charAt(var2 + 1) == 92)
                    {
                        var1.append('\\');
                        ++var2;
                    }
                    else
                    {
                        var1.append(this.jsonValue.charAt(var2));
                    }
                }

                return new NBTTagString(var1.toString());
            }
        }
    }
}
