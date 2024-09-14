package com.design.appproject.calc;

/**
 * @author shishuheng
 * @date 2020/1/21 2:34 下午
 */
public class RightParenthesesOperator extends Operator {
    RightParenthesesOperator(Integer id, String value, Integer factor) {
        super(id, value, factor);
    }

    @Override
    public double operate(double a, double b) {
        throw new RuntimeException("this operator cannot do operate");
    }
}