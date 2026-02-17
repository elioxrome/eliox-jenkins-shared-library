package com.acme.jenkins.config

/**
 * Immutable configuration model for the enterprise pipeline.
 *
 * This class contains normalized values ready to be consumed by runtime
 * orchestration and stage executors.
 */
class PipelineConfig implements Serializable {
  final String appName
  final String agentLabel
  final boolean checkoutFromScm
  final String repoUrl
  final String repoBranch
  final String buildCommand
  final String testCommand
  final String securityCommand
  final boolean deploy
  final String deployCommand
  final String environment

  /**
   * Creates a fully-specified pipeline configuration.
   *
   * @param appName application/service name to build
   * @param agentLabel Jenkins node label used for execution
   * @param checkoutFromScm whether the checkout stage should run
   * @param repoUrl explicit repository URL for inline jobs without SCM context
   * @param repoBranch repository branch to checkout when {@code repoUrl} is used
   * @param buildCommand command for the Build stage
   * @param testCommand command for the Test stage
   * @param securityCommand command for the Security Scan stage
   * @param deploy whether the Deploy stage should run
   * @param deployCommand command for the Deploy stage
   * @param environment target deployment environment
   */
  PipelineConfig(
    String appName,
    String agentLabel,
    boolean checkoutFromScm,
    String repoUrl,
    String repoBranch,
    String buildCommand,
    String testCommand,
    String securityCommand,
    boolean deploy,
    String deployCommand,
    String environment
  ) {
    this.appName = appName
    this.agentLabel = agentLabel
    this.checkoutFromScm = checkoutFromScm
    this.repoUrl = repoUrl
    this.repoBranch = repoBranch
    this.buildCommand = buildCommand
    this.testCommand = testCommand
    this.securityCommand = securityCommand
    this.deploy = deploy
    this.deployCommand = deployCommand
    this.environment = environment
  }
}
