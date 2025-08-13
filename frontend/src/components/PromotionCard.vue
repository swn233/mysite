<template>
  <div class="promotion-card">
    <div class="promotion-content">
      <div class="promotion-logo">
        <img src="../assets/logo.svg" alt="Logo" class="logo" />
      </div>
      <div class="promotion-text">
        <h3>解锁全部功能</h3>
        <h4>摄影+会员专享特权</h4>
        <p>随时取消</p>
      </div>
    </div>
    <div class="promotion-image">
      <div class="color-blocks">
        <div class="color-block pink"></div>
        <div class="color-block blue"></div>
        <div class="color-block green"></div>
        <div class="color-block orange"></div>
      </div>
    </div>
    <div class="promotion-button">
      <el-button type="primary" class="upgrade-button">立即升级</el-button>
    </div>
  </div>
</template>

<script setup>
// 组件逻辑
</script>

<style scoped>
.promotion-card {
  display: flex;
  flex-direction: column;
  border-radius: 8px;
  overflow: hidden;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  position: relative;
  height: 250px;
}

.promotion-content {
  display: flex;
  padding: 20px;
  z-index: 2;
}

.promotion-logo {
  margin-right: 10px;
}

.logo {
  width: 40px;
  height: 40px;
}

.promotion-text {
  display: flex;
  flex-direction: column;
}

.promotion-text h3 {
  margin: 0 0 5px 0;
  font-size: 18px;
  font-weight: bold;
}

.promotion-text h4 {
  margin: 0 0 5px 0;
  font-size: 16px;
  font-weight: normal;
}

.promotion-text p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.promotion-image {
  position: absolute;
  top: 0;
  right: 0;
  width: 50%;
  height: 100%;
  overflow: hidden;
}

.color-blocks {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr 1fr;
  width: 100%;
  height: 100%;
}

.color-block {
  position: relative;
}

.color-block.pink {
  background-color: #ffb6c1;
  border-radius: 50%;
  transform: scale(1.2) translate(-10%, -10%);
}

.color-block.blue {
  background-color: #6495ed;
  border-radius: 50%;
  transform: scale(1.2) translate(10%, -10%);
}

.color-block.green {
  background-color: #98fb98;
  border-radius: 50%;
  transform: scale(1.2) translate(-10%, 10%);
}

.color-block.orange {
  background-color: #ffa500;
  border-radius: 50%;
  transform: scale(1.2) translate(10%, 10%);
}

.promotion-button {
  position: absolute;
  bottom: 20px;
  left: 20px;
  z-index: 2;
}

.upgrade-button {
  border-radius: 4px;
  font-weight: 500;
}
</style>