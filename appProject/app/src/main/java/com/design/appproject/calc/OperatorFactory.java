package com.design.appproject.calc;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shishuheng
 * @date 2020/1/20 9:43 上午
 */
public class OperatorFactory {
    public static final Integer ADD = 0;
    public static final Integer SUBTRACT = 1;
    public static final Integer MULTIPLY = 2;
    public static final Integer DIVIDE = 3;
    public static final Integer PARENTHESES_LEFT = 4;
    public static final Integer PARENTHESES_RIGHT = 5;

    private static final Operator addOperator = new AddOperator(0, "+", 1);
    private static final Operator subtractOperator = new SubtractOperator(1, "-", 1);
    private static final Operator multiplyOperator = new MultiplyOperator(2, "*", 2);
    private static final Operator divideOperator = new DivideOperator(3, "/", 2);
    private static final Operator parenthesesLeft = new LeftParenthesesOperator(4, "(", 0);
    private static final Operator parenthesesRight = new RightParenthesesOperator(5, ")", 0);

    private static final Map<String, Operator> map = new LinkedHashMap<>();
    static {
        map.put("+", addOperator);
        map.put("-", subtractOperator);
        map.put("*", multiplyOperator);
        map.put("/", divideOperator);
        map.put("(", parenthesesLeft);
        map.put(")", parenthesesRight);
    }


    public static Operator getInstance(Integer type) {
        switch (type) {
            case 0: return addOperator;
            case 1: return subtractOperator;
            case 2: return multiplyOperator;
            case 3: return divideOperator;
            case 4: return parenthesesLeft;
            case 5: return parenthesesRight;
            default: return null;
        }
    }

    public static Operator getInstance(String type) {
        return map.get(type);
    }
}