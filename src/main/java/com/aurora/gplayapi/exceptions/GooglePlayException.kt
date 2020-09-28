package com.aurora.gplayapi.exceptions

import java.io.IOException

class GooglePlayException(message: String?) : IOException(message) {
    var code = 0
    var rawResponse: String = String()
}