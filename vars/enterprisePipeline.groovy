import com.acme.jenkins.pipeline.EnterprisePipelineRunner

/**
 * Global step entrypoint for the enterprise pipeline.
 *
 * This no-args variant is intended for Jenkins jobs that define parameters
 * (`APP_NAME`, `DEPLOY`, `ENV`, `DEPLOY_IMAGE_TAG`, etc.). It builds a
 * configuration map from
 * `params` and delegates execution to {@link #call(Map)}.
 *
 * Business logic is intentionally kept in `src/` classes to keep this DSL
 * entrypoint small and CPS-friendly.
 */
def call() {
  Map cfg = new HashMap()
  cfg.put('appName', params?.APP_NAME ?: '')
  cfg.put('deploy', toBooleanValue(params?.DEPLOY))
  cfg.put('environment', params?.ENV ?: 'dev')
  cfg.put('agentLabel', params?.AGENT_LABEL ?: 'linux')
  cfg.put('checkoutFromScm', params?.CHECKOUT_FROM_SCM == null ? true : toBooleanValue(params?.CHECKOUT_FROM_SCM))
  cfg.put('repoUrl', params?.REPO_URL ?: '')
  cfg.put('repoBranch', params?.REPO_BRANCH ?: 'main')
  cfg.put('deployImageTag', params?.DEPLOY_IMAGE_TAG ?: (params?.IMAGE_TAG ?: 'latest'))
  call(cfg)
}

/**
 * Executes the enterprise pipeline using an explicit configuration map.
 *
 * Supported keys:
 * - `appName` (String, required by downstream config validation)
 * - `deploy` (boolean-like, optional)
 * - `environment` (String, optional)
 * - `agentLabel` (String, optional)
 * - `checkoutFromScm` (boolean-like, optional)
 * - `repoUrl` (String, optional)
 * - `repoBranch` (String, optional)
 * - `deployRepoUrl` (String, optional)
 * - `deployRepoBranch` (String, optional)
 * - `helmChartPath` (String, optional)
 * - `deployNamespace` (String, optional)
 * - `kindClusterName` (String, optional)
 * - `deployImageTag` (String, optional)
 * - `kubeconfigCredentialsId` (String, optional)
 * - `deployCommand` (String, optional)
 *
 * @param rawConfig configuration provided by Jenkinsfile or composed by {@link #call()}
 */
def call(Map rawConfig) {
  Map safeRawConfig = rawConfig
  if (safeRawConfig == null) {
    safeRawConfig = new HashMap()
  }

  EnterprisePipelineRunner runner = new EnterprisePipelineRunner(this)
  runner.run(safeRawConfig)
}

/**
 * Converts common Jenkins parameter representations to boolean.
 *
 * Accepted truthy value:
 * - Boolean `true`
 * - String `"true"` (case-insensitive)
 *
 * Everything else is treated as `false`.
 *
 * @param value raw value from params or map
 * @return normalized boolean value
 */
private boolean toBooleanValue(Object value) {
  if (value == null) {
    return false
  }
  return 'true'.equalsIgnoreCase(value.toString())
}
