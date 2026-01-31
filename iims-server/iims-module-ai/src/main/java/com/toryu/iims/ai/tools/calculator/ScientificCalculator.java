package com.toryu.iims.ai.tools.calculator;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;

/**
 * @Author: Aitenry
 * @Date: 2026/1/26 23:17
 * @Version: v1.0.0
 * @Description: TODO
 **/
public class ScientificCalculator {

    // --- 常量定义 ---
    private static final Map<String, Double> CONSTANTS = new HashMap<>();

    static {
        CONSTANTS.put("pi", Math.PI);
        CONSTANTS.put("e", Math.E);
    }

    // --- 配置选项 ---
    private static boolean isDegreeMode = false; // 默认为弧度模式

    // --- 一元函数定义 ---
    // --- 一元函数定义 ---
    private static final Map<String, Function<Double, Double>> UNARY_FUNCTIONS = new HashMap<>();

    static {
        UNARY_FUNCTIONS.put("sin", d -> Math.sin(isDegreeMode ? Math.toRadians(d) : d));
        UNARY_FUNCTIONS.put("cos", d -> Math.cos(isDegreeMode ? Math.toRadians(d) : d));
        UNARY_FUNCTIONS.put("tan", d -> Math.tan(isDegreeMode ? Math.toRadians(d) : d));
        UNARY_FUNCTIONS.put("asin", Math::asin);
        UNARY_FUNCTIONS.put("acos", Math::acos);
        UNARY_FUNCTIONS.put("atan", Math::atan);
        UNARY_FUNCTIONS.put("sinh", Math::sinh);
        UNARY_FUNCTIONS.put("cosh", Math::cosh);
        UNARY_FUNCTIONS.put("tanh", Math::tanh);
        // 修复：将一元常用对数重命名为 "lg" 和 "log10"，避免与二元 "log" 冲突
        UNARY_FUNCTIONS.put("lg", Math::log10);    // 常用对数 (base 10)
        UNARY_FUNCTIONS.put("log10", Math::log10); // 兼容写法
        UNARY_FUNCTIONS.put("ln", Math::log);      // 自然对数
        UNARY_FUNCTIONS.put("exp", Math::exp);
        UNARY_FUNCTIONS.put("sqrt", Math::sqrt);
        UNARY_FUNCTIONS.put("abs", Math::abs);
        UNARY_FUNCTIONS.put("ceil", Math::ceil);
        UNARY_FUNCTIONS.put("floor", Math::floor);
        UNARY_FUNCTIONS.put("round", d -> Math.round(d * 100.0) / 100.0);
    }

    // --- 二元函数定义 ---
    private static final Map<String, BiFunction<Double, Double, Double>> BINARY_FUNCTIONS = new HashMap<>();

    static {
        BINARY_FUNCTIONS.put("log", (base, value) -> Math.log(value) / Math.log(base)); // log_base(value)
        BINARY_FUNCTIONS.put("max", Math::max);
        BINARY_FUNCTIONS.put("min", Math::min);
        BINARY_FUNCTIONS.put("p", (n, k) -> {
            try {
                return calculatePermutation(n, k);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }); // P(n, k)
        BINARY_FUNCTIONS.put("c", (n, k) -> {
            try {
                return calculateCombination(n, k);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }); // C(n, k)
    }

    // --- 二元运算符定义 ---
    private static final Map<String, Integer> BINARY_OPERATORS = new HashMap<>();

    static {
        BINARY_OPERATORS.put("+", 1);
        BINARY_OPERATORS.put("-", 1);
        BINARY_OPERATORS.put("*", 2);
        BINARY_OPERATORS.put("/", 2);
        BINARY_OPERATORS.put("%", 2);
        BINARY_OPERATORS.put("^", 3); // 指数 (右结合)
    }

    // --- 特殊标记 ---
    private static final String FACTORIAL_MARKER = "!"; // 阶乘作为一个特殊的后缀操作符


    public static double calculate(String expression) throws Exception {
        if (expression == null || expression.isEmpty()) {
            throw new IllegalArgumentException("Expression cannot be null or empty.");
        }
        ScientificCalculator.ExpressionEvaluator evaluator = new ScientificCalculator.ExpressionEvaluator();
        return evaluator.evaluate(expression);
    }

    public static void setDegreeMode(boolean degreeMode) {
        isDegreeMode = degreeMode;
    }

    public static boolean getDegreeMode() {
        return isDegreeMode;
    }

    private static class ExpressionEvaluator {
        private Queue<Object> tokens;

        public double evaluate(String expression) throws Exception {
            tokenize(expression);
            List<Object> rpn = infixToRPN();
            return evaluateRPN(rpn);
        }

        private void tokenize(String expression) throws Exception {
            this.tokens = new LinkedList<>();
            expression = expression.replaceAll("\\s+", ""); // 移除空格

            int tokenIndex = 0;

            while (tokenIndex < expression.length()) {
                char c = expression.charAt(tokenIndex);

                if (Character.isDigit(c) || (c == '-' && (tokenIndex == 0 || "+-*/(^,".indexOf(expression.charAt(tokenIndex - 1)) != -1))) {
                    // 处理负数
                    StringBuilder number = new StringBuilder();
                    if (c == '-') {
                        number.append(c);
                        tokenIndex++;
                        if (tokenIndex >= expression.length() || !Character.isDigit(expression.charAt(tokenIndex))) {
                            throw new Exception("Invalid number format at position " + tokenIndex);
                        }
                    }

                    while (tokenIndex < expression.length() && (Character.isDigit(expression.charAt(tokenIndex)) || expression.charAt(tokenIndex) == '.')) {
                        number.append(expression.charAt(tokenIndex));
                        tokenIndex++;
                    }
                    tokens.add(Double.parseDouble(number.toString()));
                } else if (Character.isLetter(c)) {
                    StringBuilder identifier = new StringBuilder();
                    while (tokenIndex < expression.length() && Character.isLetterOrDigit(expression.charAt(tokenIndex))) {
                        identifier.append(expression.charAt(tokenIndex));
                        tokenIndex++;
                    }
                    String idStr = identifier.toString().toLowerCase();
                    if (CONSTANTS.containsKey(idStr)) {
                        tokens.add(CONSTANTS.get(idStr));
                    } else if (UNARY_FUNCTIONS.containsKey(idStr) || BINARY_FUNCTIONS.containsKey(idStr)) {
                        tokens.add(new ScientificCalculator.FunctionToken(idStr));
                    } else {
                        throw new Exception("Unknown function or constant at position " + (tokenIndex - idStr.length()) + ": " + idStr);
                    }
                } else if (BINARY_OPERATORS.containsKey(String.valueOf(c))) {
                    tokens.add(String.valueOf(c));
                    tokenIndex++;
                } else if ("()+-*/,".indexOf(c) != -1) {
                    tokens.add(String.valueOf(c));
                    tokenIndex++;
                } else if (c == '!') {
                    tokens.add(FACTORIAL_MARKER);
                    tokenIndex++;
                } else {
                    throw new Exception("Invalid character at position " + tokenIndex + ": " + c);
                }
            }
        }

        private List<Object> infixToRPN() throws Exception {
            List<Object> output = new ArrayList<>();
            Stack<Object> operatorStack = new Stack<>();

            while (!tokens.isEmpty()) {
                Object token = tokens.poll();

                if (token instanceof Double) {
                    output.add(token);
                } else if (token instanceof ScientificCalculator.FunctionToken ft) {
                    operatorStack.push(ft);
                } else if (token instanceof String sToken) {
                    if (sToken.equals("(")) {
                        operatorStack.push(sToken);
                    } else if (sToken.equals(")")) {
                        // 弹出所有操作符直到遇到左括号
                        while (!operatorStack.isEmpty() && !(operatorStack.peek() instanceof String && "(".equals(((String) operatorStack.peek())))) {
                            output.add(operatorStack.pop());
                        }
                        if (operatorStack.isEmpty()) {
                            throw new Exception("Mismatched parentheses.");
                        }
                        // 弹出左括号
                        operatorStack.pop();

                        // 检查栈顶是否是函数，如果是则弹出到输出队列
                        if (!operatorStack.isEmpty() && operatorStack.peek() instanceof ScientificCalculator.FunctionToken) {
                            output.add(operatorStack.pop());
                        }
                    } else if (sToken.equals(",")) {
                        // 弹出所有操作符直到遇到左括号（处理函数参数）
                        while (!operatorStack.isEmpty() && !(operatorStack.peek() instanceof String && "(".equals(((String) operatorStack.peek())))) {
                            output.add(operatorStack.pop());
                        }
                    } else if (sToken.equals(FACTORIAL_MARKER)) {
                        output.add(sToken);
                    } else if (BINARY_OPERATORS.containsKey(sToken)) {
                        // 处理运算符优先级
                        while (!operatorStack.isEmpty() &&
                                operatorStack.peek() instanceof String &&
                                !operatorStack.peek().equals("(") &&
                                BINARY_OPERATORS.containsKey((String) operatorStack.peek()) &&
                                BINARY_OPERATORS.get((String) operatorStack.peek()) >= BINARY_OPERATORS.get(sToken)) {
                            output.add(operatorStack.pop());
                        }
                        operatorStack.push(sToken);
                    }
                }
            }

            // 弹出剩余的操作符
            while (!operatorStack.isEmpty()) {
                Object op = operatorStack.pop();
                if (("(".equals(op) || ")".equals(op))) {
                    throw new Exception("Mismatched parentheses.");
                }
                output.add(op);
            }

            return output;
        }

        private double evaluateRPN(List<Object> rpn) throws Exception {
            Stack<Double> stack = new Stack<>();

            for (Object token : rpn) {
                if (token instanceof Double) {
                    stack.push((Double) token);
                } else if (token instanceof String op) {
                    if (op.equals(FACTORIAL_MARKER)) {
                        if (stack.isEmpty()) throw new Exception("Invalid expression: No operand for factorial !");
                        double operand = stack.pop();
                        if (operand < 0 || operand != Math.floor(operand)) {
                            throw new Exception("Factorial (!) is only defined for non-negative integers.");
                        }
                        double result = calculateFactorial((int) operand);
                        stack.push(result);
                    } else if (BINARY_OPERATORS.containsKey(op)) {
                        if (stack.size() < 2)
                            throw new Exception("Invalid expression: Not enough operands for operator " + op);
                        double b = stack.pop();
                        double a = stack.pop();
                        double result = switch (op) {
                            case "+" -> a + b;
                            case "-" -> a - b;
                            case "*" -> a * b;
                            case "/" -> {
                                if (b == 0) throw new ArithmeticException("Division by zero.");
                                yield a / b;
                            }
                            case "%" -> a % b;
                            case "^" -> Math.pow(a, b);
                            default -> throw new Exception("Unknown operator during evaluation: " + op);
                        };
                        stack.push(result);
                    }
                } else if (token instanceof ScientificCalculator.FunctionToken ft) {
                    String functionName = ft.name;

                    if (UNARY_FUNCTIONS.containsKey(functionName)) {
                        if (stack.isEmpty())
                            throw new Exception("Invalid expression: Not enough operands for function " + functionName);
                        double operand = stack.pop();
                        double result = UNARY_FUNCTIONS.get(functionName).apply(operand);
                        stack.push(result);
                    } else if (BINARY_FUNCTIONS.containsKey(functionName)) {
                        if (stack.size() < 2)
                            throw new Exception("Invalid expression: Not enough operands for function " + functionName);
                        double arg2 = stack.pop(); // 第二个参数
                        double arg1 = stack.pop(); // 第一个参数
                        double result = BINARY_FUNCTIONS.get(functionName).apply(arg1, arg2);
                        stack.push(result);
                    } else {
                        throw new Exception("Unknown function: " + functionName);
                    }
                }
            }

            if (stack.size() != 1) {
                throw new Exception("Invalid expression: Final stack size is not 1. Current size: " + stack.size() + ". Expression might be malformed.");
            }
            return stack.pop();
        }
    }

    // 内部类，用于区分函数标记和其他字符串
    private static class FunctionToken {
        String name;

        FunctionToken(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "FUNCTION:" + name;
        }
    }

    // --- 辅助计算方法 ---
    private static double calculateFactorial(int n) {
        if (n <= 1) return 1;
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result.doubleValue(); // 注意：对于很大的n，double可能无法精确表示
    }

    private static double calculatePermutation(double nVal, double kVal) throws Exception {
        int n = (int) nVal;
        int k = (int) kVal;
        if (k < 0 || k > n) {
            throw new Exception("P(n,k) requires 0 <= k <= n.");
        }
        return calculateFactorial(n) / calculateFactorial(n - k);
    }

    private static double calculateCombination(double nVal, double kVal) throws Exception {
        int n = (int) nVal;
        int k = (int) kVal;
        if (k < 0 || k > n) {
            throw new Exception("C(n,k) requires 0 <= k <= n.");
        }
        // Use the more efficient formula C(n,k) = C(n, n-k) if k > n/2
        if (k > n / 2) k = n - k;
        double result = 1;
        for (int i = 1; i <= k; i++) {
            result = result * (n - i + 1) / i;
        }
        return result;
    }

}
