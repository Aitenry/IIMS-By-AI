import { createApp } from 'vue';
import App from '../App.vue';
import SvgIcon from '@/components/v-svg-icon/Index.vue';

const app = createApp(App);

// 注册全局组件
app.component('svg-icon', SvgIcon);

// 动态加载 SVG 文件
const requireAll = (requireContext) => requireContext.keys().map(requireContext);
const req = import.meta.glob('@/assets/svg/*.svg', { query: '?raw' });

Object.keys(req).forEach((key) => {
  req[key]().then((content) => {
    const svgElement = document.createElement('div');
    svgElement.innerHTML = content;
    document.body.appendChild(svgElement);
  });
});