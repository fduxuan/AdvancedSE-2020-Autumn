<template>
  <a-spin v-if="loading" class="loading" tip="Loading..." :spinning="loading" />
  <div v-else>
    <iframe :src="pdfSrc" class="ifc" type="application/pdf"></iframe>
  </div>
</template>

<script>

import DraftMetaService from "@/model/DraftMetaService"

export default {
  name: 'PDFPreview',
  computed: {
    fid() {
      return this.$route.params.fid
    }
  },
  data() {
    return {
      loading: false,
      pdfSrc: ''
    }
  },
  methods: {
    async init() {
      this.loading = true
      const res = await DraftMetaService.download(this.fid)
      const blobURL = window.URL.createObjectURL(new Blob([ res ], { type: 'application/pdf' }))
      this.pdfSrc = blobURL
      this.loading = false
    }
  },
  mounted() {
    this.init()
  }
}
</script>

<style scoped>
.loading {
  position: absolute;
  transform: translate(-50%, -50%);
  top: 50%;
}
.ifc {
  width: 100%;
  height: calc(100% - 70px);
  position: absolute;
  left: 0;
  border: 0;
}
</style>