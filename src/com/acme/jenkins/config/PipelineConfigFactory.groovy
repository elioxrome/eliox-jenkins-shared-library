package com.acme.jenkins.config

/**
 * Factory responsible for building a validated {@link PipelineConfig}
 * from raw user-provided input (Jenkins params / Jenkinsfile map).
 */
class PipelineConfigFactory implements Serializable {

  private static final String DEFAULT_BRANCH = 'main'
  private static final String DEFAULT_DEPLOY_REPO_URL = 'https://github.com/elioxrome/eliox-platform-config'

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
    String environment = (safeRaw.environment ?: 'dev')
    String deployNamespace = (safeRaw.deployNamespace ?: 'apps').toString().trim()
    String deployRepoUrl = (safeRaw.deployRepoUrl ?: DEFAULT_DEPLOY_REPO_URL).toString().trim()
    String deployRepoBranch = (safeRaw.deployRepoBranch ?: DEFAULT_BRANCH).toString().trim()
    String helmChartPath = (safeRaw.helmChartPath ?: "charts/${appName}").toString().trim()
    String kindClusterName = (safeRaw.kindClusterName ?: 'local-cluster').toString().trim()
    String deployImageTag = (safeRaw.deployImageTag ?: 'latest').toString().trim()
    String defaultBuildCommand = "docker build -t ${appName}:${deployImageTag} ."
    String defaultTestCommand = "docker run --rm '${appName}:${deployImageTag}' uv run pytest"
    String defaultSecurityCommand = """\
    docker run --rm \
      -v /var/run/docker.sock:/var/run/docker.sock \
      -v \$HOME/.cache/trivy:/root/.cache/ \
      aquasec/trivy:latest image --severity HIGH,CRITICAL --ignore-unfixed ${appName}:${deployImageTag}
    """.stripIndent().trim()
    String defaultDeployCommand = """\
    rm -rf .deploy-config
    kind load docker-image '${appName}:${deployImageTag}' --name '${kindClusterName}'
    git clone --depth 1 --branch '${deployRepoBranch}' '${deployRepoUrl}' .deploy-config
    helm upgrade --install '${appName}' '.deploy-config/${helmChartPath}' --namespace '${deployNamespace}'
    """.stripIndent().trim()

    new PipelineConfig(
      appName,
      (safeRaw.agentLabel ?: 'linux').toString(),
      safeRaw.checkoutFromScm == null ? true : toBooleanValue(safeRaw.checkoutFromScm),
      (safeRaw.repoUrl ?: '').toString().trim(),
      (safeRaw.repoBranch ?: DEFAULT_BRANCH).toString(),
      (safeRaw.buildCommand ?: defaultBuildCommand).toString(),
      (safeRaw.testCommand ?: defaultTestCommand).toString(),
      (safeRaw.securityCommand ?: defaultSecurityCommand).toString(),
      toBooleanValue(safeRaw.deploy),
      (safeRaw.deployCommand ?: defaultDeployCommand).toString(),
      (safeRaw.kubeconfigCredentialsId ?: 'kubeconfig-bootstrap-kind').toString().trim(),
      environment
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
