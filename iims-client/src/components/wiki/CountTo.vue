<template>
    <!-- 显示 d.num 属性的四舍五入取整后的值（若不加四舍五入，滚动时会显示小数点后面的数字） -->
    <div :class="customClass">{{ d.num.toFixed(0) }}</div>
</template>

<script setup>
import { reactive, watch } from 'vue'
import gsap from 'gsap'

// 总数值
const d = reactive({
    num: 0
})

// 对外暴露的属性值
const props = defineProps({
    value: { // 属性值名称
        type: Number, // 类型为数值
        default: 0 // 默认为 0
    },
    customClass: { // 自定义样式
        type: String, // 字符串类型
        default: '' // 默认为空
    }
})

function animateToValue() {
    // 从数值 0 滚动到 value 属性指定的值，动画时间为 0.5s 
    gsap.to(d, {
        duration: 0.5,
        num: props.value
    })
}
animateToValue()

// 侦听属性, 监听 props.value 的变化，一旦 props.value 发生变化，就调用 animateToValue 函数执行动画
watch(() => props.value, () => animateToValue())
</script>