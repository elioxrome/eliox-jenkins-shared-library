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
    assertEquals('docker build -t billing:latest .', config.buildCommand)
    assertEquals("docker run --rm 'billing:latest' uv run pytest", config.testCommand)
    assertTrue(config.securityCommand.contains('/var/run/docker.sock:/var/run/docker.sock'))
    assertTrue(config.securityCommand.contains('billing:latest'))
    assertTrue(config.deployCommand.contains("https://github.com/elioxrome/eliox-platform-config"))
    assertTrue(config.deployCommand.contains("kind load docker-image 'billing:latest' --name 'local-cluster'"))
    assertTrue(config.deployCommand.contains("helm upgrade --install 'billing' '.deploy-config/charts/billing'"))
    assertTrue(config.deployCommand.contains("--namespace 'apps'"))
    assertEquals('kubeconfig-bootstrap-kind', config.kubeconfigCredentialsId)
    assertFalse(config.deploy)
  }
}
