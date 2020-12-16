import annotations.Inject;
import annotations.Value;

import java.io.*;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Properties;

public class BeanFactoryImpl implements BeanFactory {
    private final HashMap<String, Class<?>> classMap;
    private final HashMap<String, String> valMap;

    public BeanFactoryImpl() {
        classMap = new HashMap<>();
        valMap = new HashMap<>();
    }

    @Override
    public void loadInjectProperties(File file) {
        try {
            Properties prop = new Properties();
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            prop.load(in);
            prop.forEach((k, v) -> {
                try {
                    classMap.put((String) k, Class.forName((String) v));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadValueProperties(File file) {
        try {
            Properties propVal = new Properties();
            InputStream inVal = new BufferedInputStream(new FileInputStream(file));
            propVal.load(inVal);
            propVal.forEach((k, v) -> {
                valMap.put((String) k, (String) v);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public <T> T createInstance(Class<T> clazz) {
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            return (T) createInstance(classMap.get(clazz.getName()));
        }
        T res = null;
        try {
            //createInstance;
            Constructor constructor = null;
            for (Constructor c : clazz.getDeclaredConstructors()) {
                if (c.getAnnotation(Inject.class) != null) {
                    constructor = c;
                    break;
                }
            }

            if (constructor == null) {
                constructor = clazz.getConstructor();
            }

            Parameter[] parameters = constructor.getParameters();
            Object parameterObject = null;
            Object[] parameterObjects = new Object[parameters.length];
            int j = 0;
            for (Parameter p : parameters) {
                Class<?> type = p.getType();
                if (p.getAnnotation(Value.class) != null) {
                    System.out.println("The name of parameter:" + p.getName());
                    System.out.println("The type of parameter:" + p.getType().getName());
                    Value valueAnnotation = p.getAnnotation(Value.class);
                    System.out.println("value = " + valueAnnotation.value());
                    System.out.println("delimiter = " + valueAnnotation.delimiter());

                    parameterObject = parseVal(type, valueAnnotation);
                } else {
                    parameterObject = createInstance(type);
                }
                parameterObjects[j++] = parameterObject;
            }
            res = (T) constructor.newInstance(parameterObjects);

            //set fields;
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                Class<?> type = field.getType();
                field.setAccessible(true);
                if (field.getAnnotation(Value.class) != null) {
                    Value valueAnnotation = field.getAnnotation(Value.class);
                    field.set(res, parseVal(type, valueAnnotation));
                } else if (field.getAnnotation(Inject.class) != null) {
                    field.set(res, createInstance(type));
                }
                field.setAccessible(false);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return res;
    }

    private Object parseVal(Class<?> type, Value valueAnnotation) {
        String val = valueAnnotation.value();
        if (valMap.containsKey(val)) {
            val = valMap.get(val);
        }

        if (type == int.class || type == Integer.class) {
            //1
            return Integer.parseInt(val);
        } else if (type == boolean.class || type == Boolean.class) {
            //2
            return Boolean.parseBoolean(val);
        } else if (type == double.class || type == Double.class) {
            //3
            return Double.parseDouble(val);
        } else if (type == float.class || type == Float.class) {
            //4
            return Float.parseFloat(val);
        } else if (type == short.class || type == Short.class) {
            //5
            return Short.parseShort(val);
        } else if (type == byte.class || type == Byte.class) {
            //6
            return Byte.parseByte(val);
        } else if (type == char.class || type == Character.class) {
            //7
            return val.charAt(0);
        } else if (type == long.class || type == Long.class) {
            //8
            return Long.parseLong(val);
        } else if (type == String.class) {
            //9
            return val;
        } else {

            String[] strings = val.split(valueAnnotation.delimiter());
            if (type == int[].class) {
                //1
                int[] integers = new int[strings.length];
                for (int i = 0; i < strings.length; i++) {
                    integers[i] = Integer.parseInt(strings[i]);
                }
                return integers;
            } else if (type == Integer[].class) {
                //1
                Integer[] integers = new Integer[strings.length];
                for (int i = 0; i < strings.length; i++) {
                    integers[i] = Integer.parseInt(strings[i]);
                }
                return integers;
            } else if (type == boolean[].class) {
                //2
                boolean[] booleans = new boolean[strings.length];
                for (int i = 0; i < strings.length; i++) {
                    booleans[i] = Boolean.parseBoolean(strings[i]);
                }
                return booleans;
            } else if (type == Boolean[].class) {
                //2
                Boolean[] booleans = new Boolean[strings.length];
                for (int i = 0; i < strings.length; i++) {
                    booleans[i] = Boolean.parseBoolean(strings[i]);
                }
                return booleans;
            } else if (type == double[].class) {
                //3
                double[] vals = new double[strings.length];
                for (int i = 0; i < strings.length; i++) {
                    vals[i] = Double.parseDouble(strings[i]);
                }
                return vals;
            } else if (type == Double[].class) {
                //3
                Double[] vals = new Double[strings.length];
                for (int i = 0; i < strings.length; i++) {
                    vals[i] = Double.parseDouble(strings[i]);
                }
                return vals;
            } else if (type == float[].class) {
                //4
                float[] vals = new float[strings.length];
                for (int i = 0; i < strings.length; i++) {
                    vals[i] = Float.parseFloat(strings[i]);
                }
                return vals;
            } else if (type == Float[].class) {
                //4
                Float[] vals = new Float[strings.length];
                for (int i = 0; i < strings.length; i++) {
                    vals[i] = Float.parseFloat(strings[i]);
                }
                return vals;
            } else if (type == short[].class) {
                //5
                short[] vals = new short[strings.length];
                for (int i = 0; i < strings.length; i++) {
                    vals[i] = Short.parseShort(strings[i]);
                }
                return vals;
            } else if (type == Short[].class) {
                //5
                Short[] vals = new Short[strings.length];
                for (int i = 0; i < strings.length; i++) {
                    vals[i] = Short.parseShort(strings[i]);
                }
                return vals;
            } else if (type == byte[].class) {
                //6
                byte[] vals = new byte[strings.length];
                for (int i = 0; i < strings.length; i++) {
                    vals[i] = Byte.parseByte(strings[i]);
                }
                return vals;
            } else if (type == Byte[].class) {
                //6
                Byte[] vals = new Byte[strings.length];
                for (int i = 0; i < strings.length; i++) {
                    vals[i] = Byte.parseByte(strings[i]);
                }
                return vals;
            } else if (type == char[].class) {
                //7
                char[] vals = new char[strings.length];
                for (int i = 0; i < strings.length; i++) {
                    vals[i] = strings[i].charAt(0);
                }
                return vals;
            } else if (type == Character[].class) {
                //7
                Character[] vals = new Character[strings.length];
                for (int i = 0; i < strings.length; i++) {
                    vals[i] = strings[i].charAt(0);
                }
                return vals;
            } else if (type == long[].class) {
                //8
                long[] vals = new long[strings.length];
                for (int i = 0; i < strings.length; i++) {
                    vals[i] = Long.parseLong(strings[i]);
                }
                return vals;
            } else if (type == Long[].class) {
                //8
                Long[] vals = new Long[strings.length];
                for (int i = 0; i < strings.length; i++) {
                    vals[i] = Long.parseLong(strings[i]);
                }
                return vals;
            } else if (type == String[].class) {
                //9
                return strings;
            }
        }
        return null;
    }

}
