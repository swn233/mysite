<script setup>
// 不需要直接导入组件，使用路由视图显示
</script>

<template>
  <div class="app-container">
    <!-- 路由视图 -->
    <router-view />
  </div>
</template>

<style>
body {
  margin: 0;
}
</style>

<style scoped>
.app-container {
  min-height: 100vh;
  width: 100%;
}
</style>
