package com.acme.jenkins.utils

import org.junit.Test

import static org.junit.Assert.assertEquals

class VersioningTest {

  @Test
  void stripsLeadingVFromTag() {
    assertEquals('1.2.3', Versioning.normalizeTag('v1.2.3'))
  }

  @Test
  void returnsFallbackForNull() {
    assertEquals('0.0.0-unknown', Versioning.normalizeTag(null))
  }
}
