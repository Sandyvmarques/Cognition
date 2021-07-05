package com.example.pfinal.server

import androidx.annotation.Keep

/**
 * Modelo de uma ocorrencia
 */
@Keep
data class Ocorrencia
    (
    var dispositivo: String? = null,
    var dataOcorrencia: String? = null,
    var latitude: String? = null,
    var longitude: String? = null,
    var time: String? = null
   // var azimute: String? = null,

)
