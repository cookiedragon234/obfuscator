/*
 * Copyright (c) 2017-2019 superblaubeere27, Sam Sun, MarcoMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.superblaubeere27.jobf.utils;

import com.google.common.io.Files;
import me.superblaubeere27.jobf.JObfSettings;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class NameUtils {
    /**
     * By ItzSomebody
     */
    private final static char[] DICT_SPACES = new char[]{
            '\u2000', '\u2001', '\u2002', '\u2003', '\u2004', '\u2005', '\u2006', '\u2007', '\u2008', '\u2009', '\u200A', '\u200B', '\u200C', '\u200D', '\u200E', '\u200F'
    };
    private static HashMap<String, Integer> packageMap = new HashMap<>();
    private static Map<String, HashMap<String, Integer>> USED_METHODNAMES = new HashMap<>();
    private static Map<String, Integer> USED_FIELDNAMES = new HashMap<>();
    //    private static boolean iL = true;
    private static int localVars = Short.MAX_VALUE;
    private static Random random = new Random();
    private static int METHODS = 0;
    private static int FIELDS = 0;
    private static boolean usingCustomDictionary = false;
    private static List<String> defaultDictionary = Arrays.asList("1", "l");
    private static List<String> customDictionary = new ArrayList<>();

    @SuppressWarnings("SameParameterValue")
    private static int randInt(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public static void setup() {
        USED_METHODNAMES.clear();
        USED_FIELDNAMES.clear();
        packageMap.clear();
    }

    public static String generateSpaceString(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    public static String generateClassName() {
        return generateClassName("");
    }


    public static String generateClassName(String packageName) {
        if (!packageMap.containsKey(packageName))
            packageMap.put(packageName, 0);

        int id = packageMap.get(packageName);
        packageMap.put(packageName, id + 1);

        return getName(id);
//        return ClassNameGenerator.className(Utils.random(2, 5));
    }

    private static String getName(int id) {
        if (usingCustomDictionary)
        {
            return Utils.randomise(id, customDictionary);
        }

        return Utils.randomise(id, defaultDictionary);
    }

    /**
     * @param len Length of the string to generate.
     * @return a built {@link String} consisting of DICT_SPACES.
     * @author ItzSomebody
     * Generates a {@link String} consisting only of DICT_SPACES.
     * Stole this idea from NeonObf and Smoke.
     */
    public static String crazyString(int len) {
        char[] buildString = new char[len];
        for (int i = 0; i < len; i++) {
            buildString[i] = DICT_SPACES[random.nextInt(DICT_SPACES.length)];
        }
        return new String(buildString);
    }


    public static String generateMethodName(final String className, String desc) {
//        if (!USED_METHODNAMES.containsKey(className)) {
//            USED_METHODNAMES.put(className, new HashMap<>());
//        }
//
//        HashMap<String, Integer> descMap = USED_METHODNAMES.get(className);
//
//        if (!descMap.containsKey(desc)) {
//            descMap.put(desc, 0);
//        }
////        System.out.println("0 " + className + "/" + desc + ":" + descMap);
//
//        int i = descMap.get(desc);
//
//        descMap.put(desc, i + 1);
//
////        System.out.println(USED_METHODNAMES);
//
//
//        String name = getName(names, i);
//
//        return name;
        return getName(METHODS++);
    }

    public static String generateMethodName(final ClassNode classNode, String desc) {
        return generateMethodName(classNode.name, desc);
    }

    public static String generateFieldName(final String className) {
//        if (!USED_FIELDNAMES.containsKey(className)) {
//            USED_FIELDNAMES.put(className, 0);
//        }
//
//        int i = USED_FIELDNAMES.get(className);
//        USED_FIELDNAMES.put(className, i + 1);
//
//        return getName(names, i);
        return getName(FIELDS++);
    }

    public static String generateFieldName(final ClassNode classNode) {
        return generateFieldName(classNode.name);
    }

    public static String generateLocalVariableName(final String className, final String methodName) {
        return generateLocalVariableName();
    }

    public static String generateLocalVariableName() {
        return Utils.randomise(localVars--, usingCustomDictionary ? customDictionary : defaultDictionary);
    }

    private static int getLenght() {
        return new Random().nextInt(20) + 6;
    }

    public static String unicodeString(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append((char) randInt(128, 250));
        }
        return stringBuilder.toString();
    }

    public static void mapClass(String old, String newName) {
        if (USED_METHODNAMES.containsKey(old)) {
            USED_METHODNAMES.put(newName, USED_METHODNAMES.get(old));
        }
        if (USED_FIELDNAMES.containsKey(old)) {
            USED_FIELDNAMES.put(newName, USED_FIELDNAMES.get(old));
        }
    }

    public static String getPackage(String in) {
        int lin = in.lastIndexOf('/');

        if (lin == 0) throw new IllegalArgumentException("Illegal class name");

        return lin == -1 ? "" : in.substring(0, lin);
    }

    public static void applySettings(JObfSettings settings) {
        usingCustomDictionary = settings.getUseCustomDictionary().getObject();
        
        if (usingCustomDictionary) {
            // If the user is using a custom dictionary then we will split their input into a comma delimited list of strings to be added to the dictionary used for name generation
            String[] dic = settings.getNameDictionary().getObject().replace("\n", ",").replace(", ", ",").split(",");
            customDictionary = Arrays.asList(dic);
        }
    }

    public static void cleanUp() {
        //customDictionary.clear(); Removed, throws UnsupportedOperationException
        customDictionary = new ArrayList<>();
    }
}
