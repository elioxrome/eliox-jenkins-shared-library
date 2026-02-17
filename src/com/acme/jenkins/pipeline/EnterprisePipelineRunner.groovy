package com.acme.jenkins.pipeline

import com.acme.jenkins.config.PipelineConfig
import com.acme.jenkins.config.PipelineConfigFactory
import com.acme.jenkins.stages.StandardStages

/**
 * Orchestrates the enterprise pipeline execution lifecycle.
 *
 * Responsibilities:
 * - Build a typed {@link PipelineConfig} from raw input.
 * - Initialize stage handlers.
 * - Execute standard stages inside Jenkins wrappers (`node`, `timestamps`).
 */
class EnterprisePipelineRunner implements Serializable {
  private final Object script

  /**
   * @param script Jenkins pipeline script context (usually `this` from vars step)
   */
  EnterprisePipelineRunner(Object script) {
    this.script = script
  }

  /**
   * Runs the pipeline with the provided raw configuration map.
   *
   * Expected keys in {@code rawConfig} are interpreted by
   * {@link PipelineConfigFactory}.
   *
   * @param rawConfig untyped pipeline configuration
   */
  void run(Map rawConfig) {
    PipelineConfigFactory configFactory = new PipelineConfigFactory()
    PipelineConfig config = configFactory.from(rawConfig)
    StandardStages stages = new StandardStages(script, config)

    script.node(config.agentLabel) {
      script.timestamps {
        script.stage('Checkout') { stages.checkout() }
        script.stage('Build') { stages.runBuild() }
        script.stage('Test') { stages.test() }
        script.stage('Security Scan') { stages.securityScan() }

        if (config.deploy) {
          script.stage('Deploy') { stages.deploy() }
        }
      }
    }
  }
}
