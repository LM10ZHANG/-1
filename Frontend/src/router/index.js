import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/customers' },

    {
      path: '/customers',
      name: 'customer-list',
      component: () => import('@/views/customer/CustomerListView.vue'),
      meta: { title: '客户列表' },
    },
    {
      path: '/customers/new',
      name: 'customer-new',
      component: () => import('@/views/customer/CustomerEditView.vue'),
      meta: { title: '新增客户' },
    },
    {
      path: '/customers/:id',
      name: 'customer-detail',
      component: () => import('@/views/customer/CustomerDetailView.vue'),
      meta: { title: '客户详情' },
    },
    {
      path: '/customers/:id/edit',
      name: 'customer-edit',
      component: () => import('@/views/customer/CustomerEditView.vue'),
      meta: { title: '编辑客户' },
    },

    {
      path: '/products/spu',
      name: 'product-spu-list',
      component: () => import('@/views/product/SpuListView.vue'),
      meta: { title: 'SPU 列表' },
    },
    {
      path: '/products/sku',
      name: 'product-sku-list',
      component: () => import('@/views/product/SkuListView.vue'),
      meta: { title: 'SKU 列表' },
    },
    {
      path: '/products/categories',
      name: 'product-category-list',
      component: () => import('@/views/product/CategoryListView.vue'),
      meta: { title: '商品分类' },
    },
    {
      path: '/products/spu/new',
      name: 'product-spu-new',
      component: () => import('@/views/product/SpuEditView.vue'),
      meta: { title: '新增 SPU' },
    },
    {
      path: '/products/spu/:id/edit',
      name: 'product-spu-edit',
      component: () => import('@/views/product/SpuEditView.vue'),
      meta: { title: '编辑 SPU' },
    },
    {
      path: '/products/sku/new',
      name: 'product-sku-new',
      component: () => import('@/views/product/SkuEditView.vue'),
      meta: { title: '新增 SKU' },
    },
    {
      path: '/products/sku/:id/edit',
      name: 'product-sku-edit',
      component: () => import('@/views/product/SkuEditView.vue'),
      meta: { title: '编辑 SKU' },
    },

    {
      path: '/quotes',
      name: 'quote-list',
      component: () => import('@/views/quote/QuoteListView.vue'),
      meta: { title: '报价列表' },
    },
    {
      path: '/quotes/new',
      name: 'quote-new',
      component: () => import('@/views/quote/QuoteEditView.vue'),
      meta: { title: '新建报价' },
    },
    {
      path: '/quotes/:id',
      name: 'quote-detail',
      component: () => import('@/views/quote/QuoteDetailView.vue'),
      meta: { title: '报价详情' },
    },
    {
      path: '/quotes/:id/edit',
      name: 'quote-edit',
      component: () => import('@/views/quote/QuoteEditView.vue'),
      meta: { title: '编辑报价' },
    },
    {
      path: '/quotes/:id/approve',
      name: 'quote-approve',
      component: () => import('@/views/quote/QuoteApproveView.vue'),
      meta: { title: '报价审批' },
    },

    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFoundView.vue'),
    },
  ],
})

router.afterEach((to) => {
  const title = to.meta?.title
  document.title = title ? `${title} · 销售管理` : '销售管理'
})

export default router
