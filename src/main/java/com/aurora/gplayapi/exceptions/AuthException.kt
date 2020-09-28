package com.aurora.gplayapi.exceptions

import java.io.IOException

class AuthException(message: String?) : IOException(message) {
    var code = 0
    var rawResponse: String = String()
}