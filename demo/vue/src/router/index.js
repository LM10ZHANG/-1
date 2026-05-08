//路由配置
//使用懒加载导入组件
//嵌套路由结构，主布局为Manager.vue
//根据用户角色控制路由访问权限
import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: () => import('@/views/Manager.vue'),
      redirect: '/home',
      children: [
        // 个人中心相关路由
        { path: 'person', component: () => import('@/views/manager/Person.vue')},
        { path: 'tPerson', component: () => import('@/views/manager/TPerson.vue')},
        { path: 'sPerson', component: () => import('@/views/manager/SPerson.vue')},
        { path: 'password', component: () => import('@/views/manager/Password.vue')},
        // 主页面
        { path: 'home', component: () => import('@/views/manager/Home.vue')},
        // 用户管理（管理员权限）
        { path: 'admin', component: () => import('@/views/manager/Admin.vue')},
        { path: 'teacher', component: () => import('@/views/manager/Teacher.vue')},
        { path: 'student', component: () => import('@/views/manager/Student.vue')},
        // 系统管理
        { path: 'notice', component: () => import('@/views/manager/Notice.vue')},
        { path: 'college', component: () => import('@/views/manager/College.vue')},
        { path: 'speciality', component: () => import('@/views/manager/Speciality.vue')},
        { path: 'course', component: () => import('@/views/manager/Course.vue')},
        { path: 'choice', component: () => import('@/views/manager/Choice.vue')},
        // 前端 B：客户中心 / 商品中心 / 报价中心（mock 静态演示，不影响既有页面）
        { path: 'customers', component: () => import('@/views/customer/CustomerListView.vue')},
        { path: 'customers/new', component: () => import('@/views/customer/CustomerEditView.vue')},
        { path: 'customers/:id', component: () => import('@/views/customer/CustomerDetailView.vue')},
        { path: 'customers/:id/edit', component: () => import('@/views/customer/CustomerEditView.vue')},
        { path: 'products/spu', component: () => import('@/views/product/SpuListView.vue')},
        { path: 'products/sku', component: () => import('@/views/product/SkuListView.vue')},
        { path: 'products/categories', component: () => import('@/views/product/CategoryListView.vue')},
        { path: 'products/spu/new', component: () => import('@/views/product/SpuEditView.vue')},
        { path: 'products/spu/:id/edit', component: () => import('@/views/product/SpuEditView.vue')},
        { path: 'products/sku/new', component: () => import('@/views/product/SkuEditView.vue')},
        { path: 'products/sku/:id/edit', component: () => import('@/views/product/SkuEditView.vue')},
        { path: 'quotes', component: () => import('@/views/quote/QuoteListView.vue')},
        { path: 'quotes/new', component: () => import('@/views/quote/QuoteEditView.vue')},
        { path: 'quotes/:id', component: () => import('@/views/quote/QuoteDetailView.vue')},
        { path: 'quotes/:id/edit', component: () => import('@/views/quote/QuoteEditView.vue')},
        { path: 'quotes/:id/approve', component: () => import('@/views/quote/QuoteApproveView.vue')},
      ]
    },
    // 认证相关路由
    { path: '/login', component: () => import('@/views/Login.vue')},
    { path: '/register', component: () => import('@/views/Register.vue')},
  ]
})

export default router
