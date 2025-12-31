package com.toryu.iims.ai.tools;

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

    @Tool(description = "计算两数之和")
    public String add(@ToolParam(description = "第1个加数") double a, @ToolParam(description = "第2个加数") double b) {
        try {
            return String.valueOf(a + b);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算两数之积")
    public String multiply(@ToolParam(description = "第1个因数") double a, @ToolParam(description = "第2个因数") double b) {
        try {
            return String.valueOf(a * b);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算两数之差")
    public String subtract(@ToolParam(description = "被减数") double a, @ToolParam(description = "减数") double b) {
        try {
            return String.valueOf(a - b);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算两数之商")
    public String divide(@ToolParam(description = "被除数") double a, @ToolParam(description = "除数") double b) {
        try {
            if (b == 0) {
                return "Error: 除数不能为零";
            }
            return String.valueOf(a / b);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算两数的幂次方")
    public String power(@ToolParam(description = "底数") double base, @ToolParam(description = "指数") double exponent) {
        try {
            double result = Math.pow(base, exponent);
            if (Double.isInfinite(result)) {
                return "Error: 计算结果为无穷大";
            }
            if (Double.isNaN(result)) {
                return "Error: 计算结果无效";
            }
            return String.valueOf(result);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算两数的最大公约数")
    public String gcd(@ToolParam(description = "第一个正整数") double a, @ToolParam(description = "第二个正整数") double b) {
        try {
            int intA = (int) a;
            int intB = (int) b;
            if (intA <= 0 || intB <= 0) {
                return "Error: 参数必须为正整数";
            }
            while (intB != 0) {
                int temp = intB;
                intB = intA % intB;
                intA = temp;
            }
            return String.valueOf(intA);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算两数的最小公倍数")
    public String lcm(@ToolParam(description = "第一个正整数") double a, @ToolParam(description = "第二个正整数") double b) {
        try {
            int intA = (int) a;
            int intB = (int) b;
            if (intA <= 0 || intB <= 0) {
                return "Error: 参数必须为正整数";
            }
            double gcdResult = Double.parseDouble(gcd(a, b));
            if (gcdResult == 0 || Double.isNaN(gcdResult)) {
                return "Error: GCD计算失败";
            }
            double result = (double) Math.abs(intA * intB) / (int) gcdResult;
            return String.valueOf((long) result);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算数字的阶乘")
    public String factorial(@ToolParam(description = "要计算阶乘的非负整数") double n) {
        try {
            int intN = (int) n;
            if (intN < 0) {
                return "Error: 参数必须为非负整数";
            }
            if (intN > 20) {
                return "Error: 输入值过大，可能导致溢出（建议小于等于20）";
            }
            long result = 1;
            for (int i = 2; i <= intN; i++) {
                result *= i;
            }
            return String.valueOf(result);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "判断一个数是否为质数")
    public String isPrime(@ToolParam(description = "要判断的正整数") double n) {
        try {
            int intN = (int) n;
            if (intN <= 1) {
                return "false";
            }
            if (intN <= 3) {
                return "true";
            }
            if (intN % 2 == 0 || intN % 3 == 0) {
                return "false";
            }
            for (int i = 5; i * i <= intN; i += 6) {
                if (intN % i == 0 || intN % (i + 2) == 0) {
                    return "false";
                }
            }
            return "true";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算两数取模运算")
    public String modulo(@ToolParam(description = "被除数") double a, @ToolParam(description = "除数") double b) {
        try {
            if (b == 0) {
                return "Error: 除数不能为零";
            }
            return String.valueOf(a % b);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算两数的平均值")
    public String average(@ToolParam(description = "第一个数值") double a, @ToolParam(description = "第二个数值") double b) {
        try {
            return String.valueOf((a + b) / 2.0);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算两数的最大值")
    public String max(@ToolParam(description = "第一个数值") double a, @ToolParam(description = "第二个数值") double b) {
        try {
            return String.valueOf(Math.max(a, b));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算两数的最小值")
    public String min(@ToolParam(description = "第一个数值") double a, @ToolParam(description = "第二个数值") double b) {
        try {
            return String.valueOf(Math.min(a, b));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算绝对值")
    public String absolute(@ToolParam(description = "原数值") double value) {
        try {
            return String.valueOf(Math.abs(value));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "向上取整")
    public String ceil(@ToolParam(description = "原数值") double value) {
        try {
            return String.valueOf(Math.ceil(value));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "向下取整")
    public String floor(@ToolParam(description = "原数值") double value) {
        try {
            return String.valueOf(Math.floor(value));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "四舍五入")
    public String round(@ToolParam(description = "原数值") double value) {
        try {
            return String.valueOf(Math.round(value));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算平方根")
    public String sqrt(@ToolParam(description = "被开方数") double value) {
        try {
            if (value < 0) {
                return "Error: 负数不能开平方根";
            }
            return String.valueOf(Math.sqrt(value));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算立方根")
    public String cbrt(@ToolParam(description = "被开方数") double value) {
        try {
            return String.valueOf(Math.cbrt(value));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算自然对数")
    public String log(@ToolParam(description = "真数") double value) {
        try {
            if (value <= 0) {
                return "Error: 真数必须大于0";
            }
            return String.valueOf(Math.log(value));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算以10为底的对数")
    public String log10(@ToolParam(description = "真数") double value) {
        try {
            if (value <= 0) {
                return "Error: 真数必须大于0";
            }
            return String.valueOf(Math.log10(value));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算以指定底数的对数")
    public String logBase(@ToolParam(description = "底数") double base, @ToolParam(description = "真数") double value) {
        try {
            if (base <= 0 || base == 1 || value <= 0) {
                return "Error: 底数必须大于0且不等于1，真数必须大于0";
            }
            return String.valueOf(Math.log(value) / Math.log(base));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算三角函数正弦值")
    public String sin(@ToolParam(description = "角度（弧度制）") double angle) {
        try {
            return String.valueOf(Math.sin(angle));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算三角函数余弦值")
    public String cos(@ToolParam(description = "角度（弧度制）") double angle) {
        try {
            return String.valueOf(Math.cos(angle));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算三角函数正切值")
    public String tan(@ToolParam(description = "角度（弧度制）") double angle) {
        try {
            return String.valueOf(Math.tan(angle));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "将角度从度转换为弧度")
    public String toRadians(@ToolParam(description = "角度（度）") double degrees) {
        try {
            return String.valueOf(Math.toRadians(degrees));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "将角度从弧度转换为度")
    public String toDegrees(@ToolParam(description = "角度（弧度制）") double radians) {
        try {
            return String.valueOf(Math.toDegrees(radians));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算两个整数之间的和（包含边界）")
    public String sumRange(@ToolParam(description = "起始值") double start, @ToolParam(description = "结束值") double end) {
        try {
            int intStart = (int) start;
            int intEnd = (int) end;
            if (intStart > intEnd) {
                int temp = intStart;
                intStart = intEnd;
                intEnd = temp;
            }
            int sum = 0;
            for (int i = intStart; i <= intEnd; i++) {
                sum += i;
            }
            return String.valueOf(sum);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算百分比")
    public String percentage(@ToolParam(description = "数值") double value, @ToolParam(description = "百分比") double percent) {
        try {
            return String.valueOf(value * (percent / 100.0));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算两点之间的欧几里得距离")
    public String distance(@ToolParam(description = "点A的x坐标") double x1,
                           @ToolParam(description = "点A的y坐标") double y1,
                           @ToolParam(description = "点B的x坐标") double x2,
                           @ToolParam(description = "点B的y坐标") double y2) {
        try {
            double result = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
            return String.valueOf(result);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算圆的面积")
    public String circleArea(@ToolParam(description = "半径") double radius) {
        try {
            if (radius < 0) {
                return "Error: 半径不能为负数";
            }
            return String.valueOf(Math.PI * Math.pow(radius, 2));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算圆的周长")
    public String circleCircumference(@ToolParam(description = "半径") double radius) {
        try {
            if (radius < 0) {
                return "Error: 半径不能为负数";
            }
            return String.valueOf(2 * Math.PI * radius);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算矩形面积")
    public String rectangleArea(@ToolParam(description = "长度") double length, @ToolParam(description = "宽度") double width) {
        try {
            if (length < 0 || width < 0) {
                return "Error: 长度和宽度不能为负数";
            }
            return String.valueOf(length * width);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "计算矩形周长")
    public String rectanglePerimeter(@ToolParam(description = "长度") double length, @ToolParam(description = "宽度") double width) {
        try {
            if (length < 0 || width < 0) {
                return "Error: 长度和宽度不能为负数";
            }
            return String.valueOf(2 * (length + width));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}