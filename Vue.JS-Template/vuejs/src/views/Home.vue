<template>
  <div>
    <img alt="Vue logo" src="../assets/logo.png" />
    <p>This is Home !!</p>
    <p>{{message}}</p>
  </div>
</template>

<script>
export default {
  data() {
    return {
      message: ''
    }
  },
  components: {},
  created() {
    this.$axios
      .post("/home", { message: "Hi! Vue!" })
      .then((res) => {
        this.message = res.data.message;
      });
  }
};
</script>
