package com.example.util

import kotlin.math.floor
import kotlin.math.min

/**
 * Open Location Code (Plus Code) generator implemented in pure Kotlin.
 * Converts latitude and longitude into standard Google Plus Codes.
 */
object PlusCodeHelper {
    private const val CODE_ALPHABET = "23456789CFGHJMPQRVWX"
    private const val ENCODING_BASE = 20
    private const val LATITUDE_MAX = 90.0
    private const val LONGITUDE_MAX = 180.0
    private const val PAIR_CODE_LENGTH = 10
    private const val SEPARATOR = '+'
    private const val SEPARATOR_POSITION = 8

    fun encode(latitude: Double, longitude: Double, codeLength: Int = 10, locality: String = ""): String {
        var lat = latitude.coerceIn(-LATITUDE_MAX, LATITUDE_MAX)
        var lng = longitude.coerceIn(-LONGITUDE_MAX, LONGITUDE_MAX)

        if (lat == LATITUDE_MAX) {
            lat -= 0.0000001
        }
        if (lng == LONGITUDE_MAX) {
            lng -= 0.0000001
        }

        // Normalize to positive
        lat += LATITUDE_MAX
        lng += LONGITUDE_MAX

        var latVal = lat
        var lngVal = lng

        val codeBuilder = StringBuilder()

        // Compute resolution per pair
        var latRes = 20.0
        var lngRes = 20.0

        for (i in 0 until min(codeLength, PAIR_CODE_LENGTH) step 2) {
            val latDigit = floor(latVal / latRes).toInt().coerceIn(0, 19)
            val lngDigit = floor(lngVal / lngRes).toInt().coerceIn(0, 19)

            codeBuilder.append(CODE_ALPHABET[latDigit])
            codeBuilder.append(CODE_ALPHABET[lngDigit])

            latVal -= latDigit * latRes
            lngVal -= lngDigit * lngRes

            latRes /= ENCODING_BASE
            lngRes /= ENCODING_BASE
        }

        // Insert '+' at position 8
        if (codeBuilder.length >= SEPARATOR_POSITION) {
            codeBuilder.insert(SEPARATOR_POSITION, SEPARATOR)
        }

        val fullCode = codeBuilder.toString()
        // Format shortened plus code with locality if provided (e.g. "8F6Q+4X Coimbatore")
        return if (locality.isNotBlank()) {
            val shortPart = if (fullCode.length >= 8) {
                fullCode.substring(4) // e.g. "8F6Q+4X"
            } else fullCode
            "$shortPart $locality"
        } else {
            fullCode
        }
    }
}
