package com.acme.jenkins.config

/**
 * Factory responsible for building a validated {@link PipelineConfig}
 * from raw user-provided input (Jenkins params / Jenkinsfile map).
 */
class PipelineConfigFactory implements Serializable {

  /**
   * Creates a normalized {@link PipelineConfig} applying defaults and
   * validating required values.
   *
   * @param raw untyped configuration map
   * @return normalized and validated pipeline configuration
   * @throws IllegalArgumentException when required values are missing
   */
  PipelineConfig from(Map raw) {
    Map safeRaw = raw
    if (safeRaw == null) {
      safeRaw = new HashMap()
    }

    String appName = (safeRaw.appName ?: '').toString().trim()
    if (!appName) {
      throw new IllegalArgumentException('appName is required')
    }

    new PipelineConfig(
      appName,
      (safeRaw.agentLabel ?: 'linux').toString(),
      safeRaw.checkoutFromScm == null ? true : toBooleanValue(safeRaw.checkoutFromScm),
      (safeRaw.repoUrl ?: '').toString().trim(),
      (safeRaw.repoBranch ?: 'main').toString(),
      (safeRaw.buildCommand ?: 'echo build').toString(),
      (safeRaw.testCommand ?: 'echo test').toString(),
      (safeRaw.securityCommand ?: 'echo dependencyCheckAnalyze').toString(),
      toBooleanValue(safeRaw.deploy),
      (safeRaw.deployCommand ?: 'echo deployment').toString(),
      (safeRaw.environment ?: 'dev').toString()
    )
  }

  /**
   * Converts common Jenkins representations to boolean.
   *
   * Accepted truthy values:
   * - Boolean {@code true}
   * - String {@code "true"} (case-insensitive)
   *
   * @param value raw value
   * @return normalized boolean
   */
  private boolean toBooleanValue(Object value) {
    if (value == null) {
      return false
    }
    return 'true'.equalsIgnoreCase(value.toString())
  }
}
