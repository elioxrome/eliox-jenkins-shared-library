package com.acme.jenkins.utils

class Versioning {
    static String normalizeTag(String tagName) {
        if (tagName == null) {
            return '0.0.0-unknown'
        }
        if (tagName.startsWith('v')) {
            return tagName.substring(1)
        }
        return tagName
    }
}
