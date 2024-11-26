<template>
  <div class="flex flex-wrap justify-content-center">
    <div class="border-round">
      <Card style="width: 25rem" class="mt-8 bg-gray-100 w-25rem h-25rem">
        <template #title><p class="text-center">Sign In</p></template>
        <template #content>
          <div class="flex justify-content-center">
            <div class="mt-8">
              <action-button
                text="Sign in"
                severity="success"
                size="p-button-lg"
                outlined="false"
                @click="signIn"
              ></action-button>
            </div>
          </div>
        </template>
      </Card>
    </div>
  </div>
</template>

<script setup lang="js">
import Card from 'primevue/card'
import ActionButton from '@/components/Shared/ActionButton.vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const signIn = () => {
  router.push({name: 'depositList', params: {id: -1 }})
}
</script>

<style lang="css" scoped></style>
