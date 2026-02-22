package com.acme.jenkins.steps

/**
 * Wrapper over Jenkins script binding to keep classes testable.
 */
class StepExecutor implements Serializable {
  private final def script

  StepExecutor(def script) {
    this.script = script
  }

  void shell(String command) {
    script.sh(command)
  }

  void checkoutScm() {
    script.checkout(script.scm)
  }

  void checkoutGit(String repoUrl, String repoBranch) {
    script.git(url: repoUrl, branch: repoBranch)
  }

  void echo(String message) {
    script.echo(message)
  }

  void withFileCredential(String credentialsId, String variable, Closure body) {
    script.withCredentials([[
      $class: 'FileBinding',
      credentialsId: credentialsId,
      variable: variable
    ]]) {
      body.call()
    }
  }
}
