package com.acme.jenkins.config

import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

class PipelineConfigTest {

  @Test
  void appliesDefaults() {
    PipelineConfigFactory factory = new PipelineConfigFactory()
    PipelineConfig config = factory.from([appName: 'billing'])

    assertEquals('linux', config.agentLabel)
    assertTrue(config.checkoutFromScm)
    assertEquals('main', config.repoBranch)
    assertEquals('echo build', config.buildCommand)
    assertEquals('echo test', config.testCommand)
    assertFalse(config.deploy)
  }
}
