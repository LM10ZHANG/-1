<script setup>
import { useCounterStore } from '@/stores/counter'

const counter = useCounterStore()
</script>

<template>
  <main class="home">
    <h1>Home</h1>
    <p>Vue 3 + Vite + Pinia + Vue Router</p>

    <section class="card">
      <h2>Pinia Counter</h2>
      <p>count: {{ counter.count }}</p>
      <p>doubleCount: {{ counter.doubleCount }}</p>
      <button @click="counter.increment">+1</button>
      <button @click="counter.reset">Reset</button>
    </section>
  </main>
</template>

<style scoped>
.home {
  padding: 24px;
}
.card {
  margin-top: 16px;
  padding: 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
}
button {
  margin-right: 8px;
}
</style>
