package fschmidt.feature.selection;

import com.google.common.collect.ComparisonChain;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.IntConsumer;


// ----------------------------------------------
//  Utilities.
// ----------------------------------------------
// A few little helpers that allow us to write more
// structured warning free code.
public enum Utils {
    ;

    // Rethrow an 'Throwable' preserving the stack trace but making it unchecked.
    public static <T extends Throwable> void rethrow(final Throwable th) throws T {
        throw TCast.<T> of(Throwable.class).cast(th);
    }

    public static boolean isBoxed(Class<?> clazz) {
        if (clazz == null) return false;
        return clazz == Boolean.class
                || clazz == Character.class || clazz == Byte.class
                || clazz == Short.class || clazz == Integer.class
                || clazz == Long.class || clazz == Float.class
                || clazz == Double.class;
    }

    public static <T> T newObject(Class<? extends T> clazz, Object... args) {
        args = (args == null) ? new Object[0] : args;
        Constructor<?>[] cs = clazz.getDeclaredConstructors();
        for (int i = 0; i < cs.length; ++i) {
            Constructor<?> ctor = cs[i];
            if (ctor.getParameterCount() != args.length) {
                continue;
            }
            boolean match = true;
            Class<?>[] pt = ctor.getParameterTypes();
            for (int j = 0; j < pt.length; ++j) {
                if (args[i] == null) {
                    continue;
                }
                Class<?> at = args[i].getClass();
                if (!pt[i].isAssignableFrom(at) && !(pt[i].isPrimitive() && isBoxed(at))) {
                    match = false;
                    break;
                }
            }
            if (match) {
                try {
                    return TCast.cast(ctor.newInstance(args));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                    rethrow(ex);
                    throw new IllegalStateException();
                }
            }
        }
        throw new RuntimeException("No constructor found. " + "{ new <" + clazz.getName() + ">" + Arrays.toString(args) + " }");
    }

    // ------------------------------------------------------------
    //                        SELF MIX-IN
    // ------------------------------------------------------------
    // Support mix-in composition for self bounded types.
    public interface Self<T extends Self<T>> {

        default T self() { return TCast.cast(this); }
    }

    // ----------------------------------------------
    //  TCAST.
    // ----------------------------------------------
    // Is able to cast your mother without a warning
    public enum TCast {
        ;
        @SuppressWarnings("unchecked") public static <T> Class<T> of(Class<?> c) {
            return (Class<T>) c;
        }
        // type system loophole as 'language-extension'
        public static <T> T cast(Object o) {
            return TCast.<T> of(Object.class).cast(o);
        }
    }
    // ----------------------------------------------
    //  TUPLES.
    // ----------------------------------------------
    // Good tuples (better than the pair in java)
    public enum Tuples {
        ;

        public static <T1, T2> Tuple2<T1, T2> of(T1 _1, T2 _2) { return new Tuple2<>(_1, _2); }

        // ---------------------------------------------------
        // TUPLE.
        // ---------------------------------------------------
        public static abstract class Tuple implements Serializable, Iterable<Object>, Comparable<Tuple> {
            // ---------------------------------------------------
            public abstract <T> T field(int pos);
            public abstract <T> void field(int pos, T value);
            public abstract int length();
            public abstract Iterator<Object> iterator();
            public abstract int compareTo(Tuple o);

        }

        // ---------------------------------------------------
        // TUPLE 2.
        // ---------------------------------------------------
        @SuppressWarnings("unchecked")
        public static final class Tuple2<T1, T2> extends Tuple {
            public T1 _1;
            public T2 _2;
            // ---------------------------------------------------
            Tuple2() { this(null, null); }
            Tuple2(Tuple2<T1, T2> t) { this(t._1, t._2); }
            public Tuple2(T1 _1, T2 _2) {
                this._1 = _1;
                this._2 = _2;
            }
            // ---------------------------------------------------
            public int length() { return 2; }
            public Iterator<Object> iterator() { return Arrays.asList(new Object[]{_1, _2}).iterator(); }
            public <T> T field(int pos) {
                switch(pos) {
                    case 0: return (T) this._1;
                    case 1: return (T) this._2;
                    default: throw new IndexOutOfBoundsException(String.valueOf(pos));
                }
            }
            public <T> void field(int pos, T value) {
                switch(pos) {
                    case 0: this._1 = (T1) value; break;
                    case 1: this._2 = (T2) value; break;
                    default: throw new IndexOutOfBoundsException(String.valueOf(pos));
                }
            }
            // ---------------------------------------------------
            public int compareTo(Tuple t) {
                if (this == t) return 0;
                Tuple2<T1, T2> o = (Tuples.Tuple2<T1, T2>)t;
                ComparisonChain cc = ComparisonChain.start();
                if (_1 instanceof Comparable) cc.compare((Comparable<?>) _1, (Comparable<?>) o._1);
                if (_2 instanceof Comparable) cc.compare((Comparable<?>) _2, (Comparable<?>) o._2);
                return cc.result();
            }
            public int hashCode() {
                int prime = 31;
                int result = 1;
                result = prime * result + ((_1 == null) ? 0 : _1.hashCode());
                result = prime * result + ((_2 == null) ? 0 : _2.hashCode());
                return result;
            }
            public boolean equals(Object obj) {
                if (this == obj) return true;
                if (obj == null) return false;
                if (getClass() != obj.getClass()) return false;
                Tuple2<T1, T2> other = (Tuple2<T1, T2>) obj;
                if (_1 == null) { if (other._1 != null)  return false; }
                else if (!_1.equals(other._1)) return false;
                if (_2 == null) { if (other._2 != null) return false; }
                else if (!_2.equals(other._2)) return false;
                return true;
            }
            public String toString() { return "(" + _1 + "," + _2 + ")"; }
        }
    }


    // ------------------------------------------------------------
    //                     TEXT / STRING UTILS
    // ------------------------------------------------------------
    // Allows colored output on the console.
    public static final class Text {

        public enum Color {
            BLACK        ("[0;30m"),
            RED          ("[0;31m"),
            GREEN        ("[0;32m"),
            YELLOW       ("[0;33m"),
            BLUE         ("[0;34m"),
            PURPLE       ("[0;35m"),
            CYAN         ("[0;36m"),
            WHITE        ("[0;37m"),
            GREY         ("[37;m"),

            BOLD_BLACK   ("[1;30m"),
            BOLD_RED     ("[1;31m"),
            BOLD_GREEN   ("[1;32m"),
            BOLD_YELLOW  ("[1;33m"),
            BOLD_BLUE    ("[1;34m"),
            BOLD_PURPLE  ("[1;35m"),
            BOLD_CYAN    ("[1;36m"),
            BOLD_WHITE   ("[1;37m"),

            LIGHT_BLACK  ("[0;90m"),
            LIGHT_RED    ("[0;91m"),
            LIGHT_GREEN  ("[0;92m"),
            LIGHT_YELLOW ("[0;93m"),
            LIGHT_BLUE   ("[0;94m"),
            LIGHT_PURPLE ("[0;95m"),
            LIGHT_CYAN   ("[0;96m"),
            LIGHT_WHITE  ("[0;97m"),
            ;
            public final String code;
            Color(String val) {
                code = val;
            }
        }

        public static String black(Object o)   { return toString(Objects.toString(o), Color.BLACK);  }
        public static String red(Object o)     { return toString(Objects.toString(o), Color.RED);    }
        public static String green(Object o)   { return toString(Objects.toString(o), Color.GREEN);  }
        public static String yellow(Object o)  { return toString(Objects.toString(o), Color.YELLOW); }
        public static String blue(Object o)    { return toString(Objects.toString(o), Color.BLUE);   }
        public static String purple(Object o)  { return toString(Objects.toString(o), Color.PURPLE); }
        public static String cyan(Object o)    { return toString(Objects.toString(o), Color.CYAN);   }
        public static String white(Object o)   { return toString(Objects.toString(o), Color.WHITE);  }
        public static String grey(Object o)    { return toString(Objects.toString(o), Color.GREY);   }

        public static String boldBlack(Object o)   { return toString(Objects.toString(o), Color.BOLD_BLACK);  }
        public static String boldRed(Object o)     { return toString(Objects.toString(o), Color.BOLD_RED);    }
        public static String boldGreen(Object o)   { return toString(Objects.toString(o), Color.BOLD_GREEN);  }
        public static String boldYellow(Object o)  { return toString(Objects.toString(o), Color.BOLD_YELLOW); }
        public static String boldBlue(Object o)    { return toString(Objects.toString(o), Color.BOLD_BLUE);   }
        public static String boldPurple(Object o)  { return toString(Objects.toString(o), Color.BOLD_PURPLE); }
        public static String boldCyan(Object o)    { return toString(Objects.toString(o), Color.BOLD_CYAN);   }
        public static String boldWhite(Object o)   { return toString(Objects.toString(o), Color.BOLD_WHITE);  }


        public static String lightBlack(Object o)   { return toString(Objects.toString(o), Color.LIGHT_BLACK);  }
        public static String lightRed(Object o)     { return toString(Objects.toString(o), Color.LIGHT_RED);    }
        public static String lightGreen(Object o)   { return toString(Objects.toString(o), Color.LIGHT_GREEN);  }
        public static String lightYellow(Object o)  { return toString(Objects.toString(o), Color.LIGHT_YELLOW); }
        public static String lightBlue(Object o)    { return toString(Objects.toString(o), Color.LIGHT_BLUE);   }
        public static String lightPurple(Object o)  { return toString(Objects.toString(o), Color.LIGHT_PURPLE); }
        public static String lightCyan(Object o)    { return toString(Objects.toString(o), Color.LIGHT_CYAN);   }
        public static String lightWhite(Object o)   { return toString(Objects.toString(o), Color.LIGHT_WHITE);  }

        public static String toString(final String val, final Color color)  {
            if (useColor) {
                return "\033" + color.code + val + "\033[0m";
            }
            else {
                return val;
            }
        }

        private static boolean useColor = true;

        public static void colorOff() {
            useColor = false;
        }

        public static void colorOn() {
            useColor = true;
        }

        public static Text mkText() { return new Text(); }
        public static Text mkText(String str) { return new Text(str); }

        // ----------------------------------------------

        private StringBuilder sb;
        private String format;
        private int    indent;

        public Text() { this(""); }
        public Text(String s) {
            sb = new StringBuilder();
            sb.append(s);
        }

        // ----------------------------------------------

        public int length() { return sb.length(); }

        public Text length(int len) { sb.setLength(len); return this; }

        public StringBuilder builder() { return sb; }

        public Text clear() { sb.setLength(0); return this; }

        // ----------------------------------------------

        public Text incTab()      { indent++; return this; }

        public Text decTab()      { --indent; return this; }

        public Text incTab(int v) { indent += v; return this; }

        public Text decTab(int v) { indent -= v; return this; }

        public Text newLine() {
            sb.append('\n');
            for (int i = 0; i < indent; ++i)
                sb.append('\t');
            return this;
        }

        // ----------------------------------------------

        public Text delLast()    { return del(sb.length() - 1); }

        public Text del(int pos) { sb.deleteCharAt(pos); return this; }

        // ----------------------------------------------

        public Text add(Collection<?> cs, String sep) {
            for (Object o : cs) {
                add(o.toString()).add(sep);
            }
            final int len = sb.length();
            if (len > sep.length()) {
                sb.delete(len - sep.length(), len);
            }
            return this;
        }

        public Text prepend(String s) {
            return prependStr(s);
        }

        public Text add(String s) {
            return appendStr(s);
        }

        public Text add(String s, Object o) {
            return add(s).add("(").add(o).add(")");
        }

        public Text add(Object o) {
            return appendStr(o != null ? o.toString() : "nil");
        }


        // ----------------------------------------------

        public Text add(byte v) {
            return appendStr(Byte.toString(v));
        }

        public Text add(short v) {
            return appendStr(Short.toString(v));
        }

        public Text add(char v) {
            return appendStr(Character.toString(v));
        }

        public Text add(int v) {
            return appendStr(Integer.toString(v));
        }

        public Text add(long v) {
            return appendStr(Long.toString(v));
        }

        public Text add(boolean v) {
            return appendStr(Boolean.toString(v));
        }

        // ----------------------------------------------

        public Text add(float v) {
            if (Float.isInfinite(v)) sb.append("Float.").append(v > 0 ? "POS_INFINITY" : "NEG_INFINITY");
            else if (Double.isNaN(v)) sb.append("Float.NaN");
            else sb.append(v);
            return this;
        }

        public Text add(double v) {
            if (Double.isInfinite(v)) sb.append("Double.").append(v > 0 ? "POS_INFINITY" : "NEG_INFINITY");
            else if (Double.isNaN(v)) sb.append("Double.NaN");
            else appendStr(Double.toString(v));
            return this;
        }

        // ----------------------------------------------

        public Text asList(Object... vs) {
            return mk(vs.length, "[", "]", ", ", i -> add(vs[i]));
        }

        public Text asList(byte... vs) {
            return mk(vs.length, "[", "]", ", ", i -> add(vs[i]));
        }

        public Text asList(short... vs) {
            return mk(vs.length, "[", "]", ", ", i -> add(vs[i]));
        }

        public Text asList(char... vs) {
            return mk(vs.length, "[", "]", ", ", i -> add(vs[i]));
        }

        public Text asList(int... vs) {
            return mk(vs.length, "[", "]", ", ", i -> add(vs[i]));
        }

        public Text asList(long... vs) {
            return mk(vs.length, "[", "]", ", ", i -> add(vs[i]));
        }

        public Text asList(float... vs) {
            return mk(vs.length, "[", "]", ", ", i -> add(vs[i]));
        }

        public Text asList(double... vs) {
            return mk(vs.length, "[", "]", ", ", i -> add(vs[i]));
        }

        // ----------------------------------------------

        public Text asTuple(Object... vs) {
            return mk(vs.length, "( ", " )", ", ", i -> add(vs[i]));
        }

        public Text asTuple(byte... vs) {
            return mk(vs.length, "( ", " )", ", ", i -> add(vs[i]));
        }

        public Text asTuple(short... vs) {
            return mk(vs.length, "( ", " )", ", ", i -> add(vs[i]));
        }

        public Text asTuple(char... vs) {
            return mk(vs.length, "( ", " )", ", ", i -> add(vs[i]));
        }

        public Text asTuple(int... vs) {
            return mk(vs.length, "( ", " )", ", ", i -> add(vs[i]));
        }

        public Text asTuple(long... vs) {
            return mk(vs.length, "( ", " )", ", ", i -> add(vs[i]));
        }

        public Text asTuple(float... vs) {
            return mk(vs.length, "( ", " )", ", ", i -> add(vs[i]));
        }

        public Text asTuple(double... vs) {
            return mk(vs.length, "( ", " )", ", ", i -> add(vs[i]));
        }


        // ----------------------------------------------

        public Text done() {
            return appendStr("\033[0m");
        }

        public Text black() {
            return appendStr("\033[0;30m");
        }

        public Text red() {
            return appendStr("\033[0;31m");
        }

        public Text green() {
            return appendStr("\033[0;32m");
        }

        public Text yellow() {
            return appendStr("\033[0;33m");
        }

        public Text blue() {
            return appendStr("\033[0;34m");
        }

        public Text purple() {
            return appendStr("\033[0;35m");
        }

        public Text cyan() {
            return appendStr("\033[0;36m");
        }

        public Text white() {
            return appendStr("\033[0;37m");
        }

        public Text grey() {
            return appendStr("\u001b[37;m");
        }


        // ----------------------------------------------

        public String toString() {
            return sb.toString();
        }

        // ----------------------------------------------

        private interface F {
            void f();
        }

        private Text appendStr(String s) {
            sb.append(s);
            return this;
        }

        private Text appendStr(Object s) {
            sb.append(s.toString());
            return this;
        }

        private Text prependStr(String s) {
            sb.insert(0, s);
            return this;
        }

        private Text mk(int len, String sep, String beg, String end, IntConsumer cn) {
            appendStr(beg);
            for (int i = 0; i < len; ++i) {
                cn.accept(i);
                if (i < len - 1) {
                    appendStr(sep);
                }
            }
            return appendStr(end);
        }
    }
}
