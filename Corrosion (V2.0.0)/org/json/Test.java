/*
 * Decompiled with CFR 0.152.
 */
package org.json;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.json.CDL;
import org.json.Cookie;
import org.json.CookieList;
import org.json.HTTP;
import org.json.JSONArray;
import org.json.JSONML;
import org.json.JSONObject;
import org.json.JSONString;
import org.json.JSONStringer;
import org.json.JSONTokener;
import org.json.XML;

public class Test {
    public static void main(String[] stringArray) {
        class Obj
        implements JSONString {
            public String aString;
            public double aNumber;
            public boolean aBoolean;

            public Obj(String string, double d2, boolean bl2) {
                this.aString = string;
                this.aNumber = d2;
                this.aBoolean = bl2;
            }

            public double getNumber() {
                return this.aNumber;
            }

            public String getString() {
                return this.aString;
            }

            public boolean isBoolean() {
                return this.aBoolean;
            }

            public String getBENT() {
                return "All uppercase key";
            }

            public String getX() {
                return "x";
            }

            @Override
            public String toJSONString() {
                return "{" + JSONObject.quote(this.aString) + ":" + JSONObject.doubleToString(this.aNumber) + "}";
            }

            public String toString() {
                return this.getString() + " " + this.getNumber() + " " + this.isBoolean() + "." + this.getBENT() + " " + this.getX();
            }
        }
        Obj obj = new Obj("A beany object", 42.0, true);
        try {
            JSONObject jSONObject = XML.toJSONObject("<![CDATA[This is a collection of test patterns and examples for org.json.]]>  Ignore the stuff past the end.  ");
            System.out.println(jSONObject.toString());
            String string = "{     \"list of lists\" : [         [1, 2, 3],         [4, 5, 6],     ] }";
            jSONObject = new JSONObject(string);
            System.out.println(jSONObject.toString(4));
            System.out.println(XML.toString(jSONObject));
            string = "<recipe name=\"bread\" prep_time=\"5 mins\" cook_time=\"3 hours\"> <title>Basic bread</title> <ingredient amount=\"8\" unit=\"dL\">Flour</ingredient> <ingredient amount=\"10\" unit=\"grams\">Yeast</ingredient> <ingredient amount=\"4\" unit=\"dL\" state=\"warm\">Water</ingredient> <ingredient amount=\"1\" unit=\"teaspoon\">Salt</ingredient> <instructions> <step>Mix all ingredients together.</step> <step>Knead thoroughly.</step> <step>Cover with a cloth, and leave for one hour in warm room.</step> <step>Knead again.</step> <step>Place in a bread baking tin.</step> <step>Cover with a cloth, and leave for one hour in warm room.</step> <step>Bake in the oven at 180(degrees)C for 30 minutes.</step> </instructions> </recipe> ";
            jSONObject = XML.toJSONObject(string);
            System.out.println(jSONObject.toString(4));
            System.out.println();
            jSONObject = JSONML.toJSONObject(string);
            System.out.println(jSONObject.toString());
            System.out.println(JSONML.toString(jSONObject));
            System.out.println();
            JSONArray jSONArray = JSONML.toJSONArray(string);
            System.out.println(jSONArray.toString(4));
            System.out.println(JSONML.toString(jSONArray));
            System.out.println();
            string = "<div id=\"demo\" class=\"JSONML\"><p>JSONML is a transformation between <b>JSON</b> and <b>XML</b> that preserves ordering of document features.</p><p>JSONML can work with JSON arrays or JSON objects.</p><p>Three<br/>little<br/>words</p></div>";
            jSONObject = JSONML.toJSONObject(string);
            System.out.println(jSONObject.toString(4));
            System.out.println(JSONML.toString(jSONObject));
            System.out.println();
            jSONArray = JSONML.toJSONArray(string);
            System.out.println(jSONArray.toString(4));
            System.out.println(JSONML.toString(jSONArray));
            System.out.println();
            string = "<person created=\"2006-11-11T19:23\" modified=\"2006-12-31T23:59\">\n <firstName>Robert</firstName>\n <lastName>Smith</lastName>\n <address type=\"home\">\n <street>12345 Sixth Ave</street>\n <city>Anytown</city>\n <state>CA</state>\n <postalCode>98765-4321</postalCode>\n </address>\n </person>";
            jSONObject = XML.toJSONObject(string);
            System.out.println(jSONObject.toString(4));
            jSONObject = new JSONObject(obj);
            System.out.println(jSONObject.toString());
            string = "{ \"entity\": { \"imageURL\": \"\", \"name\": \"IXXXXXXXXXXXXX\", \"id\": 12336, \"ratingCount\": null, \"averageRating\": null } }";
            jSONObject = new JSONObject(string);
            System.out.println(jSONObject.toString(2));
            JSONStringer jSONStringer = new JSONStringer();
            string = jSONStringer.object().key("single").value("MARIE HAA'S").key("Johnny").value("MARIE HAA\\'S").key("foo").value("bar").key("baz").array().object().key("quux").value("Thanks, Josh!").endObject().endArray().key("obj keys").value(JSONObject.getNames(obj)).endObject().toString();
            System.out.println(string);
            System.out.println(new JSONStringer().object().key("a").array().array().array().value("b").endArray().endArray().endArray().endObject().toString());
            jSONStringer = new JSONStringer();
            jSONStringer.array();
            jSONStringer.value(1L);
            jSONStringer.array();
            jSONStringer.value(null);
            jSONStringer.array();
            jSONStringer.object();
            jSONStringer.key("empty-array").array().endArray();
            jSONStringer.key("answer").value(42L);
            jSONStringer.key("null").value(null);
            jSONStringer.key("false").value(false);
            jSONStringer.key("true").value(true);
            jSONStringer.key("big").value(1.23456789E96);
            jSONStringer.key("small").value(1.23456789E-80);
            jSONStringer.key("empty-object").object().endObject();
            jSONStringer.key("long");
            jSONStringer.value(Long.MAX_VALUE);
            jSONStringer.endObject();
            jSONStringer.value("two");
            jSONStringer.endArray();
            jSONStringer.value(true);
            jSONStringer.endArray();
            jSONStringer.value(98.6);
            jSONStringer.value(-100.0);
            jSONStringer.object();
            jSONStringer.endObject();
            jSONStringer.object();
            jSONStringer.key("one");
            jSONStringer.value(1.0);
            jSONStringer.endObject();
            jSONStringer.value(obj);
            jSONStringer.endArray();
            System.out.println(jSONStringer.toString());
            System.out.println(new JSONArray(jSONStringer.toString()).toString(4));
            int[] nArray = new int[]{1, 2, 3};
            JSONArray jSONArray2 = new JSONArray(nArray);
            System.out.println(jSONArray2.toString());
            String[] stringArray2 = new String[]{"aString", "aNumber", "aBoolean"};
            jSONObject = new JSONObject((Object)obj, stringArray2);
            jSONObject.put("Testing JSONString interface", obj);
            System.out.println(jSONObject.toString(4));
            jSONObject = new JSONObject("{slashes: '///', closetag: '</script>', backslash:'\\\\', ei: {quotes: '\"\\''},eo: {a: '\"quoted\"', b:\"don't\"}, quotes: [\"'\", '\"']}");
            System.out.println(jSONObject.toString(2));
            System.out.println(XML.toString(jSONObject));
            System.out.println("");
            jSONObject = new JSONObject("{foo: [true, false,9876543210,    0.0, 1.00000001,  1.000000000001, 1.00000000000000001, .00000000000000001, 2.00, 0.1, 2e100, -32,[],{}, \"string\"],   to   : null, op : 'Good',ten:10} postfix comment");
            jSONObject.put("String", "98.6");
            jSONObject.put("JSONObject", new JSONObject());
            jSONObject.put("JSONArray", new JSONArray());
            jSONObject.put("int", 57);
            jSONObject.put("double", 1.2345678901234568E29);
            jSONObject.put("true", true);
            jSONObject.put("false", false);
            jSONObject.put("null", JSONObject.NULL);
            jSONObject.put("bool", "true");
            jSONObject.put("zero", -0.0);
            jSONObject.put("\\u2028", "\u2028");
            jSONObject.put("\\u2029", "\u2029");
            jSONArray = jSONObject.getJSONArray("foo");
            jSONArray.put(666);
            jSONArray.put(2001.99);
            jSONArray.put("so \"fine\".");
            jSONArray.put("so <fine>.");
            jSONArray.put(true);
            jSONArray.put(false);
            jSONArray.put(new JSONArray());
            jSONArray.put(new JSONObject());
            jSONObject.put("keys", JSONObject.getNames(jSONObject));
            System.out.println(jSONObject.toString(4));
            System.out.println(XML.toString(jSONObject));
            System.out.println("String: " + jSONObject.getDouble("String"));
            System.out.println("  bool: " + jSONObject.getBoolean("bool"));
            System.out.println("    to: " + jSONObject.getString("to"));
            System.out.println("  true: " + jSONObject.getString("true"));
            System.out.println("   foo: " + jSONObject.getJSONArray("foo"));
            System.out.println("    op: " + jSONObject.getString("op"));
            System.out.println("   ten: " + jSONObject.getInt("ten"));
            System.out.println("  oops: " + jSONObject.optBoolean("oops"));
            string = "<xml one = 1 two=' \"2\" '><five></five>First \t&lt;content&gt;<five></five> This is \"content\". <three>  3  </three>JSON does not preserve the sequencing of elements and contents.<three>  III  </three>  <three>  T H R E E</three><four/>Content text is an implied structure in XML. <six content=\"6\"/>JSON does not have implied structure:<seven>7</seven>everything is explicit.<![CDATA[CDATA blocks<are><supported>!]]></xml>";
            jSONObject = XML.toJSONObject(string);
            System.out.println(jSONObject.toString(2));
            System.out.println(XML.toString(jSONObject));
            System.out.println("");
            jSONArray2 = JSONML.toJSONArray(string);
            System.out.println(jSONArray2.toString(4));
            System.out.println(JSONML.toString(jSONArray2));
            System.out.println("");
            string = "<xml do='0'>uno<a re='1' mi='2'>dos<b fa='3'/>tres<c>true</c>quatro</a>cinqo<d>seis<e/></d></xml>";
            jSONArray2 = JSONML.toJSONArray(string);
            System.out.println(jSONArray2.toString(4));
            System.out.println(JSONML.toString(jSONArray2));
            System.out.println("");
            string = "<mapping><empty/>   <class name = \"Customer\">      <field name = \"ID\" type = \"string\">         <bind-xml name=\"ID\" node=\"attribute\"/>      </field>      <field name = \"FirstName\" type = \"FirstName\"/>      <field name = \"MI\" type = \"MI\"/>      <field name = \"LastName\" type = \"LastName\"/>   </class>   <class name = \"FirstName\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class>   <class name = \"MI\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class>   <class name = \"LastName\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class></mapping>";
            jSONObject = XML.toJSONObject(string);
            System.out.println(jSONObject.toString(2));
            System.out.println(XML.toString(jSONObject));
            System.out.println("");
            jSONArray2 = JSONML.toJSONArray(string);
            System.out.println(jSONArray2.toString(4));
            System.out.println(JSONML.toString(jSONArray2));
            System.out.println("");
            jSONObject = XML.toJSONObject("<?xml version=\"1.0\" ?><Book Author=\"Anonymous\"><Title>Sample Book</Title><Chapter id=\"1\">This is chapter 1. It is not very long or interesting.</Chapter><Chapter id=\"2\">This is chapter 2. Although it is longer than chapter 1, it is not any more interesting.</Chapter></Book>");
            System.out.println(jSONObject.toString(2));
            System.out.println(XML.toString(jSONObject));
            System.out.println("");
            jSONObject = XML.toJSONObject("<!DOCTYPE bCard 'http://www.cs.caltech.edu/~adam/schemas/bCard'><bCard><?xml default bCard        firstname = ''        lastname  = '' company   = '' email = '' homepage  = ''?><bCard        firstname = 'Rohit'        lastname  = 'Khare'        company   = 'MCI'        email     = 'khare@mci.net'        homepage  = 'http://pest.w3.org/'/><bCard        firstname = 'Adam'        lastname  = 'Rifkin'        company   = 'Caltech Infospheres Project'        email     = 'adam@cs.caltech.edu'        homepage  = 'http://www.cs.caltech.edu/~adam/'/></bCard>");
            System.out.println(jSONObject.toString(2));
            System.out.println(XML.toString(jSONObject));
            System.out.println("");
            jSONObject = XML.toJSONObject("<?xml version=\"1.0\"?><customer>    <firstName>        <text>Fred</text>    </firstName>    <ID>fbs0001</ID>    <lastName> <text>Scerbo</text>    </lastName>    <MI>        <text>B</text>    </MI></customer>");
            System.out.println(jSONObject.toString(2));
            System.out.println(XML.toString(jSONObject));
            System.out.println("");
            jSONObject = XML.toJSONObject("<!ENTITY tp-address PUBLIC '-//ABC University::Special Collections Library//TEXT (titlepage: name and address)//EN' 'tpspcoll.sgm'><list type='simple'><head>Repository Address </head><item>Special Collections Library</item><item>ABC University</item><item>Main Library, 40 Circle Drive</item><item>Ourtown, Pennsylvania</item><item>17654 USA</item></list>");
            System.out.println(jSONObject.toString());
            System.out.println(XML.toString(jSONObject));
            System.out.println("");
            jSONObject = XML.toJSONObject("<test intertag status=ok><empty/>deluxe<blip sweet=true>&amp;&quot;toot&quot;&toot;&#x41;</blip><x>eks</x><w>bonus</w><w>bonus2</w></test>");
            System.out.println(jSONObject.toString(2));
            System.out.println(XML.toString(jSONObject));
            System.out.println("");
            jSONObject = HTTP.toJSONObject("GET / HTTP/1.0\nAccept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*\nAccept-Language: en-us\nUser-Agent: Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; T312461; Q312461)\nHost: www.nokko.com\nConnection: keep-alive\nAccept-encoding: gzip, deflate\n");
            System.out.println(jSONObject.toString(2));
            System.out.println(HTTP.toString(jSONObject));
            System.out.println("");
            jSONObject = HTTP.toJSONObject("HTTP/1.1 200 Oki Doki\nDate: Sun, 26 May 2002 17:38:52 GMT\nServer: Apache/1.3.23 (Unix) mod_perl/1.26\nKeep-Alive: timeout=15, max=100\nConnection: Keep-Alive\nTransfer-Encoding: chunked\nContent-Type: text/html\n");
            System.out.println(jSONObject.toString(2));
            System.out.println(HTTP.toString(jSONObject));
            System.out.println("");
            jSONObject = new JSONObject("{nix: null, nux: false, null: 'null', 'Request-URI': '/', Method: 'GET', 'HTTP-Version': 'HTTP/1.0'}");
            System.out.println(jSONObject.toString(2));
            System.out.println("isNull: " + jSONObject.isNull("nix"));
            System.out.println("   has: " + jSONObject.has("nix"));
            System.out.println(XML.toString(jSONObject));
            System.out.println(HTTP.toString(jSONObject));
            System.out.println("");
            jSONObject = XML.toJSONObject("<?xml version='1.0' encoding='UTF-8'?>\n\n<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\"><SOAP-ENV:Body><ns1:doGoogleSearch xmlns:ns1=\"urn:GoogleSearch\" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><key xsi:type=\"xsd:string\">GOOGLEKEY</key> <q xsi:type=\"xsd:string\">'+search+'</q> <start xsi:type=\"xsd:int\">0</start> <maxResults xsi:type=\"xsd:int\">10</maxResults> <filter xsi:type=\"xsd:boolean\">true</filter> <restrict xsi:type=\"xsd:string\"></restrict> <safeSearch xsi:type=\"xsd:boolean\">false</safeSearch> <lr xsi:type=\"xsd:string\"></lr> <ie xsi:type=\"xsd:string\">latin1</ie> <oe xsi:type=\"xsd:string\">latin1</oe></ns1:doGoogleSearch></SOAP-ENV:Body></SOAP-ENV:Envelope>");
            System.out.println(jSONObject.toString(2));
            System.out.println(XML.toString(jSONObject));
            System.out.println("");
            jSONObject = new JSONObject("{Envelope: {Body: {\"ns1:doGoogleSearch\": {oe: \"latin1\", filter: true, q: \"'+search+'\", key: \"GOOGLEKEY\", maxResults: 10, \"SOAP-ENV:encodingStyle\": \"http://schemas.xmlsoap.org/soap/encoding/\", start: 0, ie: \"latin1\", safeSearch:false, \"xmlns:ns1\": \"urn:GoogleSearch\"}}}}");
            System.out.println(jSONObject.toString(2));
            System.out.println(XML.toString(jSONObject));
            System.out.println("");
            jSONObject = CookieList.toJSONObject("  f%oo = b+l=ah  ; o;n%40e = t.wo ");
            System.out.println(jSONObject.toString(2));
            System.out.println(CookieList.toString(jSONObject));
            System.out.println("");
            jSONObject = Cookie.toJSONObject("f%oo=blah; secure ;expires = April 24, 2002");
            System.out.println(jSONObject.toString(2));
            System.out.println(Cookie.toString(jSONObject));
            System.out.println("");
            jSONObject = new JSONObject("{script: 'It is not allowed in HTML to send a close script tag in a string<script>because it confuses browsers</script>so we insert a backslash before the /'}");
            System.out.println(jSONObject.toString());
            System.out.println("");
            JSONTokener jSONTokener = new JSONTokener("{op:'test', to:'session', pre:1}{op:'test', to:'session', pre:2}");
            jSONObject = new JSONObject(jSONTokener);
            System.out.println(jSONObject.toString());
            System.out.println("pre: " + jSONObject.optInt("pre"));
            char c2 = jSONTokener.skipTo('{');
            System.out.println((int)c2);
            jSONObject = new JSONObject(jSONTokener);
            System.out.println(jSONObject.toString());
            System.out.println("");
            jSONArray = CDL.toJSONArray("No quotes, 'Single Quotes', \"Double Quotes\"\n1,'2',\"3\"\n,'It is \"good,\"', \"It works.\"\n\n");
            System.out.println(CDL.toString(jSONArray));
            System.out.println("");
            System.out.println(jSONArray.toString(4));
            System.out.println("");
            jSONArray = new JSONArray(" [\"<escape>\", next is an implied null , , ok,] ");
            System.out.println(jSONArray.toString());
            System.out.println("");
            System.out.println(XML.toString(jSONArray));
            System.out.println("");
            jSONObject = new JSONObject("{ fun => with non-standard forms ; forgiving => This package can be used to parse formats that are similar to but not stricting conforming to JSON; why=To make it easier to migrate existing data to JSON,one = [[1.00]]; uno=[[{1=>1}]];'+':+6e66 ;pluses=+++;empty = '' , 'double':0.666,true: TRUE, false: FALSE, null=NULL;[true] = [[!,@;*]]; string=>  o. k. ; \r oct=0666; hex=0x666; dec=666; o=0999; noh=0x0x}");
            System.out.println(jSONObject.toString(4));
            System.out.println("");
            if (jSONObject.getBoolean("true") && !jSONObject.getBoolean("false")) {
                System.out.println("It's all good");
            }
            System.out.println("");
            jSONObject = new JSONObject(jSONObject, new String[]{"dec", "oct", "hex", "missing"});
            System.out.println(jSONObject.toString(4));
            System.out.println("");
            System.out.println(new JSONStringer().array().value(jSONArray).value(jSONObject).endArray());
            jSONObject = new JSONObject("{string: \"98.6\", long: 2147483648, int: 2147483647, longer: 9223372036854775807, double: 9223372036854775808}");
            System.out.println(jSONObject.toString(4));
            System.out.println("\ngetInt");
            System.out.println("int    " + jSONObject.getInt("int"));
            System.out.println("long   " + jSONObject.getInt("long"));
            System.out.println("longer " + jSONObject.getInt("longer"));
            System.out.println("double " + jSONObject.getInt("double"));
            System.out.println("string " + jSONObject.getInt("string"));
            System.out.println("\ngetLong");
            System.out.println("int    " + jSONObject.getLong("int"));
            System.out.println("long   " + jSONObject.getLong("long"));
            System.out.println("longer " + jSONObject.getLong("longer"));
            System.out.println("double " + jSONObject.getLong("double"));
            System.out.println("string " + jSONObject.getLong("string"));
            System.out.println("\ngetDouble");
            System.out.println("int    " + jSONObject.getDouble("int"));
            System.out.println("long   " + jSONObject.getDouble("long"));
            System.out.println("longer " + jSONObject.getDouble("longer"));
            System.out.println("double " + jSONObject.getDouble("double"));
            System.out.println("string " + jSONObject.getDouble("string"));
            jSONObject.put("good sized", Long.MAX_VALUE);
            System.out.println(jSONObject.toString(4));
            jSONArray = new JSONArray("[2147483647, 2147483648, 9223372036854775807, 9223372036854775808]");
            System.out.println(jSONArray.toString(4));
            System.out.println("\nKeys: ");
            Iterator iterator = jSONObject.keys();
            while (iterator.hasNext()) {
                string = (String)iterator.next();
                System.out.println(string + ": " + jSONObject.getString(string));
            }
            System.out.println("\naccumulate: ");
            jSONObject = new JSONObject();
            jSONObject.accumulate("stooge", "Curly");
            jSONObject.accumulate("stooge", "Larry");
            jSONObject.accumulate("stooge", "Moe");
            jSONArray = jSONObject.getJSONArray("stooge");
            jSONArray.put(5, "Shemp");
            System.out.println(jSONObject.toString(4));
            System.out.println("\nwrite:");
            System.out.println(jSONObject.write(new StringWriter()));
            string = "<xml empty><a></a><a>1</a><a>22</a><a>333</a></xml>";
            jSONObject = XML.toJSONObject(string);
            System.out.println(jSONObject.toString(4));
            System.out.println(XML.toString(jSONObject));
            string = "<book><chapter>Content of the first chapter</chapter><chapter>Content of the second chapter      <chapter>Content of the first subchapter</chapter>      <chapter>Content of the second subchapter</chapter></chapter><chapter>Third Chapter</chapter></book>";
            jSONObject = XML.toJSONObject(string);
            System.out.println(jSONObject.toString(4));
            System.out.println(XML.toString(jSONObject));
            jSONArray = JSONML.toJSONArray(string);
            System.out.println(jSONArray.toString(4));
            System.out.println(JSONML.toString(jSONArray));
            Collection collection = null;
            Map map = null;
            jSONObject = new JSONObject(map);
            jSONArray = new JSONArray(collection);
            jSONObject.append("stooge", "Joe DeRita");
            jSONObject.append("stooge", "Shemp");
            jSONObject.accumulate("stooges", "Curly");
            jSONObject.accumulate("stooges", "Larry");
            jSONObject.accumulate("stooges", "Moe");
            jSONObject.accumulate("stoogearray", jSONObject.get("stooges"));
            jSONObject.put("map", map);
            jSONObject.put("collection", collection);
            jSONObject.put("array", jSONArray);
            jSONArray.put(map);
            jSONArray.put(collection);
            System.out.println(jSONObject.toString(4));
            string = "{plist=Apple; AnimalSmells = { pig = piggish; lamb = lambish; worm = wormy; }; AnimalSounds = { pig = oink; lamb = baa; worm = baa;  Lisa = \"Why is the worm talking like a lamb?\" } ; AnimalColors = { pig = pink; lamb = black; worm = pink; } } ";
            jSONObject = new JSONObject(string);
            System.out.println(jSONObject.toString(4));
            string = " (\"San Francisco\", \"New York\", \"Seoul\", \"London\", \"Seattle\", \"Shanghai\")";
            jSONArray = new JSONArray(string);
            System.out.println(jSONArray.toString());
            string = "<a ichi='1' ni='2'><b>The content of b</b> and <c san='3'>The content of c</c><d>do</d><e></e><d>re</d><f/><d>mi</d></a>";
            jSONObject = XML.toJSONObject(string);
            System.out.println(jSONObject.toString(2));
            System.out.println(XML.toString(jSONObject));
            System.out.println("");
            jSONArray2 = JSONML.toJSONArray(string);
            System.out.println(jSONArray2.toString(4));
            System.out.println(JSONML.toString(jSONArray2));
            System.out.println("");
            System.out.println("\nTesting Exceptions: ");
            System.out.print("Exception: ");
            try {
                jSONArray = new JSONArray();
                jSONArray.put(Double.NEGATIVE_INFINITY);
                jSONArray.put(Double.NaN);
                System.out.println(jSONArray.toString());
            }
            catch (Exception exception) {
                System.out.println(exception);
            }
            System.out.print("Exception: ");
            try {
                System.out.println(jSONObject.getDouble("stooge"));
            }
            catch (Exception exception) {
                System.out.println(exception);
            }
            System.out.print("Exception: ");
            try {
                System.out.println(jSONObject.getDouble("howard"));
            }
            catch (Exception exception) {
                System.out.println(exception);
            }
            System.out.print("Exception: ");
            try {
                System.out.println(jSONObject.put(null, "howard"));
            }
            catch (Exception exception) {
                System.out.println(exception);
            }
            System.out.print("Exception: ");
            try {
                System.out.println(jSONArray.getDouble(0));
            }
            catch (Exception exception) {
                System.out.println(exception);
            }
            System.out.print("Exception: ");
            try {
                System.out.println(jSONArray.get(-1));
            }
            catch (Exception exception) {
                System.out.println(exception);
            }
            System.out.print("Exception: ");
            try {
                System.out.println(jSONArray.put(Double.NaN));
            }
            catch (Exception exception) {
                System.out.println(exception);
            }
            System.out.print("Exception: ");
            try {
                jSONObject = XML.toJSONObject("<a><b>    ");
            }
            catch (Exception exception) {
                System.out.println(exception);
            }
            System.out.print("Exception: ");
            try {
                jSONObject = XML.toJSONObject("<a></b>    ");
            }
            catch (Exception exception) {
                System.out.println(exception);
            }
            System.out.print("Exception: ");
            try {
                jSONObject = XML.toJSONObject("<a></a    ");
            }
            catch (Exception exception) {
                System.out.println(exception);
            }
            System.out.print("Exception: ");
            try {
                jSONArray2 = new JSONArray(new Object());
                System.out.println(jSONArray2.toString());
            }
            catch (Exception exception) {
                System.out.println(exception);
            }
            System.out.print("Exception: ");
            try {
                string = "[)";
                jSONArray = new JSONArray(string);
                System.out.println(jSONArray.toString());
            }
            catch (Exception exception) {
                System.out.println(exception);
            }
            System.out.print("Exception: ");
            try {
                string = "<xml";
                jSONArray2 = JSONML.toJSONArray(string);
                System.out.println(jSONArray2.toString(4));
            }
            catch (Exception exception) {
                System.out.println(exception);
            }
            System.out.print("Exception: ");
            try {
                string = "<right></wrong>";
                jSONArray2 = JSONML.toJSONArray(string);
                System.out.println(jSONArray2.toString(4));
            }
            catch (Exception exception) {
                System.out.println(exception);
            }
            System.out.print("Exception: ");
            try {
                string = "{\"koda\": true, \"koda\": true}";
                jSONObject = new JSONObject(string);
                System.out.println(jSONObject.toString(4));
            }
            catch (Exception exception) {
                System.out.println(exception);
            }
            System.out.print("Exception: ");
            try {
                jSONStringer = new JSONStringer();
                string = jSONStringer.object().key("bosanda").value("MARIE HAA'S").key("bosanda").value("MARIE HAA\\'S").endObject().toString();
                System.out.println(jSONObject.toString(4));
            }
            catch (Exception exception) {
                System.out.println(exception);
            }
        }
        catch (Exception exception) {
            System.out.println(exception.toString());
        }
    }
}

