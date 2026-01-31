package com.toryu.iims.ai.tools;

import com.toryu.iims.ai.tools.calculator.ScientificCalculator;
import com.toryu.iims.ai.tools.factory.AITool;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class CalculatorTool implements AITool {

    @Override
    public Object getToolInstance() {
        return this; // 返回当前实例
    }

    @Override
    public String getToolName() {
        return "calculator-tools";
    }

    @Tool(description = "科学计算器工具，支持基本运算、三角函数、对数、阶乘、排列组合等科学计算功能。表达式格式如：'2+3', 'sin(pi/2)', 'log10(100)', '5!', 'P(5,2)', 'C(10,3)'等")
    public String calculator(@ToolParam(description = "数学表达式，支持数字、基本运算符(+,-,*,/,%,^)、函数(sin,cos,tan,asin,acos,atan,sinh,cosh,tanh,lg/log10,ln,exp,sqrt,abs,ceil,floor,round)、常数(pi,e)、阶乘(!)、排列(P(n,k))、组合(C(n,k))等") String expression) {
        try {
            double result = ScientificCalculator.calculate(expression);
            return String.format("%.10g", result); // 使用通用格式，保留有效数字
        } catch (Exception e) {
            return "计算错误: " + e.getMessage();
        }
    }
}