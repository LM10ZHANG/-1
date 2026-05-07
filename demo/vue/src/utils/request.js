//Axios封装
//统一API配置
//请求/响应拦截
//错误处理（特别是401跳转登录）
import { ElMessage } from 'element-plus'
import router from '../router'
import axios from "axios";
// 创建axios实例，配置基础URL和超时时间
const request = axios.create({
    baseURL: import.meta.env.VITE_BASE_URL,
    timeout: 30000  // 后台接口超时时间设置
})

// 请求拦截器
request.interceptors.request.use(config => {
    // 设置请求头
    config.headers['Content-Type'] = 'application/json;charset=utf-8';
    return config
}, error => {
    return Promise.reject(error)
});

// 响应拦截器
request.interceptors.response.use(
    response => {
        let res = response.data;
        // 处理blob响应（文件下载）
        if (response.config.responseType === 'blob') {
            return res
        }
        // 处理字符串响应
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }
        // 处理401未授权
        if (res.code === '401') {
            ElMessage.error(res.msg);
            router.push("/login")
        }
        return res;
    },
        error => {
        console.log('err' + error)
        return Promise.reject(error)
    }
)


export default request
