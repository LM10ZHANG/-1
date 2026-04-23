<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <header class="app-header">
    <nav class="nav">
      <RouterLink to="/">Home</RouterLink>
      <RouterLink to="/about">About</RouterLink>
    </nav>
  </header>

  <RouterView />
</template>

<style scoped>
.app-header {
  padding: 12px 24px;
  border-bottom: 1px solid #eee;
}
.nav a {
  margin-right: 16px;
  text-decoration: none;
  color: #1677ff;
}
.nav a.router-link-active {
  font-weight: bold;
}
</style>
