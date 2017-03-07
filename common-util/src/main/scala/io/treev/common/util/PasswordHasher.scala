package io.treev.common.util

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64

object PasswordHasher {

  def hashPassword(password: String): String = {
    val digest = MessageDigest.getInstance("SHA-1")
    encoder.encodeToString(digest.digest(password.getBytes(StandardCharsets.UTF_8)))
  }

  private lazy val encoder = Base64.getUrlEncoder.withoutPadding()

}
