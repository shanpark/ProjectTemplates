<template>
  <v-container fill-height>
    <v-row align="center" justify="center">
      <v-col cols="auto">
        <v-card class="mx-auto" max-width="344">
          <v-card-title>Login</v-card-title>
          <v-card-text>
            <div>
              email: shanpark@naver.com, password: lalala
            </div>
            <v-text-field label="Email" size="40" maxlength="320" single-line full-width v-model="email"></v-text-field>
            <v-text-field label="Password" type="password" single-line v-model="password"></v-text-field>
            <v-row justify="center">
              <v-btn color="primary" @click="auth">Login</v-btn>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  data() {
    return {
      email: null,
      password: null
    }
  },
  methods: {
    auth() {
      this.$axios
        .post("/auth", { email: this.email, password: this.password })
        .then((res) => {
          if (res.data.code == 0) { // success
            this.$store.state.authInfo = res.data; // ReauthResDto 객체가 내려올 것이다.
            this.$router.push({name: 'home'});
          }
        });
    },
  },
};
</script>