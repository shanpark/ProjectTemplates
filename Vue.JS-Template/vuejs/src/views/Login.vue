<template>
  <v-container fill-height>
    <v-row align="center" justify="center">
      <v-col cols="auto">
        <v-card class="mx-auto" max-width="344">
          <v-card-title>Login</v-card-title>
          <v-card-text>
            <v-text-field label="Email" size="40" maxlength="320" single-line full-width></v-text-field>
            <v-text-field label="Password" type="password" single-line></v-text-field>
            <v-row justify="center" >
              <v-btn color="primary">Login</v-btn>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {};
</script>