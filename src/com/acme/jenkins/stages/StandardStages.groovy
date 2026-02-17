package com.acme.jenkins.stages

import com.acme.jenkins.config.PipelineConfig
import com.acme.jenkins.steps.StepExecutor

class StandardStages implements Serializable {
  private final PipelineConfig config
  private final StepExecutor steps

  StandardStages(def script, PipelineConfig config) {
    this.config = config
    this.steps = new StepExecutor(script)
  }

  @SuppressWarnings('CatchException')
  void checkout() {
    if (!config.checkoutFromScm) {
      steps.echo('Skipping checkout stage by configuration (checkoutFromScm=false).')
      return
    }

    if (config.repoUrl) {
      steps.echo(
        "No scm binding available. Checking out from repoUrl '${config.repoUrl}' " +
          "(branch '${config.repoBranch}')."
      )
      steps.checkoutGit(config.repoUrl, config.repoBranch)
      return
    }

    try {
      steps.checkoutScm()
    } catch (Exception ex) {
      if (isScmContextUnavailable(ex)) {
        steps.echo("No SCM context available in this job. Skipping checkout stage. " +
          "Recommended: configure job as 'Pipeline script from SCM' for standard checkout.")
        return
      }
      throw ex
    }
  }

  void runBuild() {
    steps.echo("Building ${config.appName}")
    steps.shell(config.buildCommand)
  }

  void test() {
    steps.echo("Running tests for ${config.appName}")
    steps.shell(config.testCommand)
  }

  void securityScan() {
    steps.echo("Running security scan for ${config.appName}")
    steps.shell(config.securityCommand)
  }

  void deploy() {
    steps.echo("Deploying ${config.appName} to ${config.environment}")
    steps.shell(config.deployCommand)
  }

  private boolean isScmContextUnavailable(Exception ex) {
    String message = ex?.message ?: ''
    return message.contains("'checkout scm' is only available when using") ||
      message.contains("‘checkout scm’ is only available when using")
  }
}
