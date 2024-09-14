package com.design.appproject.calc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Calculator {
    /**
     * 简单实现的一个栈
     * <p>
     * 用来温习数据结构随手写的 这个地方完全可以使用java自带的{@link Stack}
     *
     * @param <E>
     */
    private static class MyStack<E> {
        // 默认初始化的数据大小
        private static final int defaultSize = 10;
        // 元素类型的Class
        private Class<E> type;
        // 自增量 当list数组不够时 重新申请空间时的增加值
        private int increment = 10;
        // 用来存储栈元素的数组
        private E[] list = null;
        // 栈的游标
        private int index = -1;

        private MyStack(Class<E> type) {
            this.type = type;
            list = generateArray(type, defaultSize);
        }

        /**
         * 创建实例
         *
         * @param type
         * @return
         */
        public static MyStack getInstance(Class type) {
            return new MyStack<>(type);
        }

        /**
         * 创建数组
         *
         * @param type
         * @param size
         * @return
         */
        private E[] generateArray(Class<E> type, int size) {
            E[] array = (E[]) Array.newInstance(type, size);
            return array;
        }

        /**
         * 重新申请一个更大的数组
         * <p>
         * 在当前数组的基础上 重新申请一块比当前数组大 increment 大小的数组 并将当前数组的值复制到新数组
         */
        private void realloc() {
            if (null == list) {
                list = generateArray(type, defaultSize);
            }
            Class<E> eClass = (Class<E>) list[0].getClass();
            E[] newArray = generateArray(eClass, list.length + increment);
            System.arraycopy(list, 0, newArray, 0, list.length);
            list = newArray;
        }

        /**
         * 出栈
         *
         * @return
         */
        public E pop() {
            if (index > -1) {
                E e = list[index];
                index--;
                return e;
            } else {
                return null;
            }
        }

        /**
         * 压栈
         *
         * @param e
         */
        public void push(E e) {
            if (++index >= list.length) {
                realloc();
            }
            list[index] = e;
        }

        /**
         * 获取栈顶元素
         *
         * @return
         */
        public E top() {
            return list[index];
        }

        /**
         * 清除栈内元素
         */
        public void clear() {
            // discard array and let gc recycle the memory
            list = generateArray(type, defaultSize);
        }

        public boolean isEmpty() {
            if (null == list) {
                return true;
            }
            if (-1 == index) {
                return true;
            }
            return false;
        }
    }

    /**
     * 将表达式拆分成数组
     * <p>
     * 拆分成每个数字或符号都为单独的元素的数组
     *
     * @param exp
     * @return
     */
    private String[] splitStr(String exp) {
        // 删除多余的空格
        char[] chars = new char[exp.length()];
        int charLen = 0;
        for (int i = 0; i < exp.length(); i++) {
            if (' ' != exp.charAt(i)) {
                chars[charLen] = exp.charAt(i);
                charLen++;
            }
        }
        char[] charSeq = new char[charLen];
        System.arraycopy(chars, 0, charSeq, 0, charLen);
        // 修正后的字符
        String str = String.valueOf(charSeq);
        String[] arrays = null;
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (isOperator(c)) {
                count++;
            }
        }
        arrays = new String[count * 2 + 1];
        int index = 0;
        int start = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (isOperator(c)) {
                arrays[index] = str.substring(start, i);
                arrays[++index] = String.valueOf(c);
                start = i + 1;
                index++;
            }
        }
        if (start <= str.length() - 1) {
            arrays[index] = str.substring(start);
        }
        return arrays;
    }

    private boolean isOperator(char c) {
        return isOperator(String.valueOf(c));
    }

    private boolean isOperator(String c) {
        Operator operator = OperatorFactory.getInstance(c);
        if (null != operator) {
            return true;
        }
        return false;
    }

    private Operator getOperator(String c) {
        return OperatorFactory.getInstance(c);
    }

    public String dealStr(String exp) {
        String[] arrays = splitStr(exp);
        List<String> postfix = new ArrayList<>(arrays.length);
        MyStack<Operator> stack = (MyStack<Operator>) MyStack.getInstance(Operator.class);
        for (int i = 0; i < arrays.length; i++) {
            String s = arrays[i];
            if (s.equals("") || s.equals(" ")) {
                continue;
            }
            Operator operator = getOperator(s);
            if (null != operator) {
                Operator o = manualStack(operator, stack);
                while (null != o) {
                    postfix.add(o.getValue());
                    o = manualStack(operator, stack);
                }
            } else {
                postfix.add(s);
            }
        }
        while (!stack.isEmpty()) {
            Operator o = stack.pop();
            postfix.add(o.getValue());
        }
        String v = compute(postfix);
        return v;
    }

    private Operator manualStack(Operator operator, MyStack<Operator> stack) {
        if (operator.equals(OperatorFactory.getInstance(")"))) {
            if (stack.isEmpty() || null == stack.top()) {
                throw new RuntimeException("error formula");
            }
            if (stack.top().equals(OperatorFactory.getInstance("("))) {
                stack.pop();
            } else {
                return stack.pop();
            }
        } else if (operator.equals(OperatorFactory.getInstance("("))) {
            stack.push(operator);
        } else {
            if (stack.isEmpty()) {
                stack.push(operator);
            } else {
                if (operator.getFactor() <= stack.top().getFactor()) {
                    return stack.pop();
                } else {
                    stack.push(operator);
                }
            }
        }
        return null;
    }

    private String compute(List<String> postfixExpression) {
        List<String> newList = new ArrayList<>();
        if (null == postfixExpression || postfixExpression.isEmpty()) {
            return null;
        }
        MyStack<String> stack = MyStack.getInstance(String.class);
        for (int i = 0; i < postfixExpression.size(); i++) {
            String a = postfixExpression.get(i);
            if (isOperator(a)) {
                Operator o = getOperator(a);
                String two = stack.pop();
                String one = stack.pop();
                if (null == one || null == two) {
                    throw new RuntimeException("stack elements has error");
                }
                double od = Double.valueOf(one);
                double td = Double.valueOf(two);
                double val = o.operate(od, td);
                String v = String.valueOf(val);
                stack.push(v);
            } else {
                stack.push(a);
            }
        }
        String value = stack.pop();
        if (stack.isEmpty()) {
            return value;
        }
        return null;
    }
}