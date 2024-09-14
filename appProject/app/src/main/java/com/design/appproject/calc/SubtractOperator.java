package com.design.appproject.calc;

/**
 * @author shishuheng
 * @date 2020/1/21 2:30 下午
 */
public class SubtractOperator extends Operator {
    SubtractOperator(Integer id, String value, Integer factor) {
        super(id, value, factor);
    }

    @Override
    public double operate(double a, double b) {
        return a - b;
    }
}